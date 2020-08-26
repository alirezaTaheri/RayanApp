package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.NewDevicesRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Dialogs.ProgressDialog;
import rayan.rayanapp.Listeners.ConnectingToTarget;
import rayan.rayanapp.Listeners.NetworkConnectivityListener;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.Listeners.TestNewDeviceDialogControllerListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.NewDevicesListViewModel;

@SuppressLint("ParcelCreator")
public class NewDevicesListFragment extends BackHandledFragment implements OnNewDeviceClicked<AccessPoint>, ConnectingToTarget , View.OnClickListener, BlockingStep, NetworkConnectivityListener,TestNewDeviceDialogControllerListener {

    private final String TAG = NewDevicesListFragment.class.getSimpleName();
    public AccessPoint selectedAccessPoint;
    private NewDevicesListViewModel viewModel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    NewDevicesRecyclerViewAdapter newDevicesRecyclerViewAdapter;
    ConnectingToTarget connection;
    ConnectionStatus connectionStatus;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
//    @BindView(R.id.name)
//    EditText nameEditText;
    private String targetSSID;
    private String currentSSID;
    public static NewDevicesListFragment newInstance() {
        return new NewDevicesListFragment();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) Objects.requireNonNull(activity).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        newDevicesRecyclerViewAdapter = new NewDevicesRecyclerViewAdapter(getActivity(), this);
        newDevicesRecyclerViewAdapter.setListener(this);
        connection = this;
        this.searching();
    }

    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_devices_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel = ViewModelProviders.of(this).get(NewDevicesListViewModel.class);
        ((RayanApplication)activity.getApplication()).getWifiBus().toObservable().subscribeOn(Schedulers.io())
                .subscribe(scanResults -> {
                    this.idle();
                    List<AccessPoint> newDevices = new ArrayList<>();
                    for (int a = 0;a<scanResults.size();a++)
//                        if (!isItemExist(scanResults.get(a).BSSID, scanResults.get(a).SSID,scanResults))
                            newDevices.add(new AccessPoint(scanResults.get(a).SSID, scanResults.get(a).BSSID, scanResults.get(a).capabilities,scanResults.get(a).level));
                    Log.e(TAG,"Found SSIDs:" + newDevices);
                    newDevicesRecyclerViewAdapter.setItems(newDevices);
                });
        viewModel.scan();
        connectionStatus = ConnectionStatus.SEARCHING;
        newDevicesRecyclerViewAdapter.setItems(viewModel.getSSIDs());
        recyclerView.setAdapter(newDevicesRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClicked(AccessPoint item) {
        selectedAccessPoint = item;
        newDevicesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTestDeviceClicked(AccessPoint item) {
        currentSSID = getCurrentSSID();
        Log.e(TAG, "Item: " + item + "\nCurrent SSID: " + currentSSID);
        if (!currentSSID.equals(item.getSSID())){
            this.connecting(item);
        }
        else this.connectToSame(currentSSID);
    }

    ProgressDialog progressDialog;
    @Override
    public void connecting(AccessPoint target) {
        this.targetSSID = target.getSSID();
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        progressDialog.show();
        String password,chipId;
        if (target.getSSID().split("_")[target.getSSID().split("_").length-1].toLowerCase().equals("f"))
            chipId = target.getSSID().split("_")[target.getSSID().split("_").length-2];
        else chipId = target.getSSID().split("_")[target.getSSID().split("_").length-1];
        Device existingDevice = viewModel.getDevice(chipId);
        if (existingDevice != null)
            password = existingDevice.getSecret();
        else password = AppConstants.DEVICE_PRIMARY_PASSWORD;
        Log.e(TAG, "Connecting... password will be: "+password);
        if (targetSSID.toLowerCase().contains("_f"))
            password = AppConstants.DEVICE_PRIMARY_PASSWORD;
        Log.e(TAG, "Connecting... password will be: "+password);
        Log.e(TAG , "Connecting To: "+ target.getSSID() +" with password: " + password);
        WifiUtils.enableLog(true);
        WifiUtils.withContext(getActivity())
                .connectWith(targetSSID, password)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        progressDialog.dismiss();
                        Log.e("SuccessfullyConnected" , "isisisisi: ");
                    }

                    @Override
                    public void failed(@androidx.annotation.NonNull ConnectionErrorCode errorCode) {
                        progressDialog.dismiss();
                        targetSSID = null;
                        Log.e("NotSuccessfully" , "NotSuccessfullyConnected NONONONOISISISIS: " + errorCode);
                    }
                })
                .start();
//        WifiHandler.connectToSSID(getActivity(),targetSSID, AppConstants.DEVICE_PRIMARY_PASSWORD);
        connectionStatus = ConnectionStatus.CONNECTING;
    }

    @Override
    public void successful(String targetSSID) {
        if (progressDialog != null)
            progressDialog.dismiss();
        Log.e(TAG, "Successfully connected to: " + targetSSID);
        connectionStatus = ConnectionStatus.SUCCESSFUL;
        Toast.makeText(getActivity(), "باموفقیت متصل شد", Toast.LENGTH_SHORT).show();
        TestDeviceFragment.newInstance(targetSSID, this).show(getActivity().getSupportFragmentManager(), "testDevice");
    }

    @Override
    public void failure() {
        if (progressDialog != null)
            progressDialog.dismiss();
        connectionStatus = ConnectionStatus.FAILURE;
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"اتصال ناموفق بود. لطفا دوباره تلاش کنید");
    }

    @Override
    public void searching() {
        connectionStatus = ConnectionStatus.SEARCHING;
//        ((AddNewDeviceActivity)Objects.requireNonNull(getActivity())).setActionBarTitle("در حال جستجو");
    }

    @Override
    public void idle() {
//        if (getActivity() != null)
//            ((AddNewDeviceActivity)(getActivity())).setActionBarTitle("افزود دستگاه جدید");
    }

    @Override
    public void connectToSame(String targetSSID) {
        Log.e(TAG, "Already connected to: " + targetSSID);
        Toast.makeText(getActivity(), "در حال حاضر به این دستگاه متصل هستید", Toast.LENGTH_SHORT).show();
        TestDeviceFragment.newInstance(targetSSID, this).show(getActivity().getSupportFragmentManager(), "testDevice");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                break;
        }
    }

    @OnClick(R.id.fab)
    void search(){
        viewModel.scan();
        this.searching();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        super.onResume();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
//        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()))
//        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا نام دستگاه را وارد کنید");
        if (selectedAccessPoint == null)
            Toast.makeText(getContext(), "لطفا یک دستگاه را انتخاب کنید", Toast.LENGTH_SHORT).show();
        else{
            ((AddNewDeviceActivity)getActivity()).getNewDevice().setAccessPointName(selectedAccessPoint.getSSID());
            if (selectedAccessPoint.getSSID().split("_")[selectedAccessPoint.getSSID().split("_").length-1].toLowerCase().equals("f")){
            ((AddNewDeviceActivity)getActivity()).getNewDevice().setChip_id(selectedAccessPoint.getSSID().split("_")[selectedAccessPoint.getSSID().split("_").length-2]);
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setStatus(NewDevice.NodeStatus.NEW);
            }
            else{
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setChip_id(selectedAccessPoint.getSSID().split("_")[selectedAccessPoint.getSSID().split("_").length-1]);
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setStatus(NewDevice.NodeStatus.IDLE);
            }
            if (((AddNewDeviceActivity)getActivity()).getNewDevice().getAccessPointName().toLowerCase().contains(AppConstants.DEVICE_TYPE_SWITCH_1))
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setType(AppConstants.DEVICE_TYPE_SWITCH_1);

            else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getAccessPointName().toLowerCase().contains(AppConstants.DEVICE_TYPE_SWITCH_2))
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setType(AppConstants.DEVICE_TYPE_SWITCH_2);

            else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getAccessPointName().toLowerCase().contains(AppConstants.DEVICE_TYPE_TOUCH_2))
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setType(AppConstants.DEVICE_TYPE_TOUCH_2);

            else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getAccessPointName().toLowerCase().contains(AppConstants.DEVICE_TYPE_PLUG))
                ((AddNewDeviceActivity)getActivity()).getNewDevice().setType(AppConstants.DEVICE_TYPE_PLUG);

            Log.e("//////////" , "Device is:  " + ((AddNewDeviceActivity) getActivity()).getNewDevice());
            callback.goToNextStep();
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Override
    public void wifiNetwork(boolean connected, String ssid) {
        Log.d(TAG, "wifiNetwork() called with: connected = [" + connected + "], ssid = [" + ssid + " TargetSSID: "+targetSSID+".]");
        if (connected) {
            ((RayanApplication) (activity.getApplication())).getNetworkBus().send(ssid);
            if (targetSSID != null) {
                currentSSID = ssid;
                if (currentSSID.equals(targetSSID)) {
                    this.successful(targetSSID);
                } else {
                    this.failure();
                }
            }
        }else this.failure();
    }

    @Override
    public void mobileNetwork(boolean connected) {

    }

    @Override
    public void vpnNetwork() {

    }

    @Override
    public void notConnected() {

    }

    @Override
    public void testTerminated() {
        Log.e(TAG, "Testing Terminated for: " + targetSSID);
        this.targetSSID = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    private enum ConnectionStatus{
        IDLE,
        SEARCHING,
        CONNECTING,
        SUCCESSFUL,
        FAILURE
    }

    private String getCurrentSSID(){
//        wifiInfo = wifiManager.getConnectionInfo();
//        String currentSSID  = wifiInfo.getSSID();
//        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
//            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
//        }
//        Log.e("thisisCurrent SSID: ", "Current SSID: : " + currentSSID);

//        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        if (info != null && info.isConnected()) {
//            currentSSID = info.getExtraInfo();
//            Log.d(TAG, "WiFi SSID: " + currentSSID);
//            if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
//            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
//        }
//        }else {
//            currentSSID = AppConstants.UNKNOWN_SSID;
//            Log.d(TAG, "WiFi SSID: " + "null");
//        }
        if (activity.currentSsid != null) {
            currentSSID = activity.currentSsid;
            Log.d(TAG, "WiFi SSID: " + currentSSID);
            if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        }else {
            currentSSID = AppConstants.UNKNOWN_SSID;
            Log.d(TAG, "WiFi SSID: " + "null");
        }
        return currentSSID;
    }

    private boolean isItemExist(String BSSID, String SSID, List<ScanResult> items){
        for (int a = 0;a<items.size();a++)
            if (BSSID.equals(items.get(a).BSSID) && SSID.equals(items.get(a).SSID))
                return true;
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            this.activity = (AddNewDeviceActivity) context;
    }
    AddNewDeviceActivity activity;
}
