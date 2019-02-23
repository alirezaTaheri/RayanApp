package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Adapters.recyclerView.NewDevicesRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Listeners.ConnectingToTarget;
import rayan.rayanapp.Listeners.OnNewDeviceClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.NewDevicesListViewModel;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDevicesListFragment extends Fragment implements OnNewDeviceClicked<AccessPoint>, ConnectingToTarget , View.OnClickListener, Step {

    private final String TAG = NewDevicesListFragment.class.getSimpleName();
    private NewDevicesListViewModel viewModel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    NewDevicesRecyclerViewAdapter newDevicesRecyclerViewAdapter;
    ConnectingToTarget connection;
    ConnectionStatus connectionStatus;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    private String targetSSID;
    private String currentSSID;
    public static NewDevicesListFragment newInstance() {
        return new NewDevicesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        newDevicesRecyclerViewAdapter = new NewDevicesRecyclerViewAdapter(getActivity());
        newDevicesRecyclerViewAdapter.setListener(this);
        connection = this;
        this.searching();
        ((RayanApplication)this.getActivity().getApplication()).getNetworkStatus().observe(this, networkConnection -> {
            String status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (status.equals(AppConstants.WIFI) && connectionStatus.equals(ConnectionStatus.CONNECTING)){
                currentSSID = getCurrentSSID();
                if (currentSSID.equals(targetSSID)){
                    this.successful();
                }else{
                    this.failure();
                }

            }
        });
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
        ((RayanApplication)getActivity().getApplication()).getWifiBus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanResults -> {
                    this.idle();
                    List<AccessPoint> newDevices = new ArrayList<>();
                    for (int a = 0;a<scanResults.size();a++)
                        newDevices.add(new AccessPoint(scanResults.get(a).SSID, scanResults.get(a).BSSID, scanResults.get(a).capabilities,scanResults.get(a).level));
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
        ((AddNewDeviceActivity)getActivity()).selectedNewDevice = item;
        newDevicesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTestDeviceClicked(AccessPoint item) {
        currentSSID = getCurrentSSID();
        Log.e(TAG, "Item: " + item + "\nCurrent SSID: " + currentSSID);
        if (!currentSSID.equals(item.getSSID())){
            this.connecting(item.getSSID());
        }
        else this.connectToSame();
    }

    @Override
    public void connecting(String targetSSID) {
        this.targetSSID = targetSSID;
        WifiHandler.connectToSSID(getActivity(),targetSSID, "12345678");
        connectionStatus = ConnectionStatus.CONNECTING;
    }

    @Override
    public void successful() {
        connectionStatus = ConnectionStatus.SUCCESSFUL;
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"باموفقیت متصل شد");
    }

    @Override
    public void failure() {
        connectionStatus = ConnectionStatus.FAILURE;
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"اتصال ناموفق بود. لطفا دوباره تلاش کنید");
    }

    @Override
    public void searching() {
        connectionStatus = ConnectionStatus.SEARCHING;
        ((AddNewDeviceActivity)Objects.requireNonNull(getActivity())).setActionBarTitle("در حال جستجو");
    }

    @Override
    public void idle() {
        ((AddNewDeviceActivity)Objects.requireNonNull(getActivity())).setActionBarTitle("افزود دستگاه جدید");
    }

    @Override
    public void connectToSame() {
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"در حال حاضر به این دستگاه متصل هستید");
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

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private enum ConnectionStatus{
        IDLE,
        SEARCHING,
        CONNECTING,
        SUCCESSFUL,
        FAILURE
    }

    private String getCurrentSSID(){
        wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }
}
