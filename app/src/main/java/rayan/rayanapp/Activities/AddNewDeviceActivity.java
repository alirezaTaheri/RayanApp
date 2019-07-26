package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.stepstone.stepper.StepperLayout;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Adapters.viewPager.AddNewDeviceStepperAdapter;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.ChangeGroupFragment;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.NewDevicesListFragment;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.NetworkConnectivityListener;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.ConnectionLiveData;
import rayan.rayanapp.Receivers.WifiScanReceiver;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;
import rayan.rayanapp.Wifi.WifiHandler;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewDeviceActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface, DoneWithFragment, DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked, YesNoDialogListener, NetworkConnectivityListener {
    private final String TAG = AddNewDeviceActivity.class.getSimpleName();
    private WifiHandler wifiHandler;
    WifiScanReceiver wifiReceiver;
    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;
    AddNewDeviceStepperAdapter stepperAdapter;
    public FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public AccessPoint selectedAccessPoint;
    public Group selectedGroup;
    BackHandledFragment currentFragment;
    private SetPrimaryConfigRequest setPrimaryConfigRequest;
    private static NewDevice newDevice;
    GroupsListFragmentViewModel viewModel;
    String testCaseSSID;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);
        viewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        initNewDevice();
        setPrimaryConfigRequest = new SetPrimaryConfigRequest();
        ButterKnife.bind(this);
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext());
        connectionLiveData.observe(this, connectionStatusModel -> {
            Log.e("AddnewdeviceActivity", "connectoinStatusModel: " + connectionStatusModel);
            if (connectionStatusModel == null){
                this.notConnected();
            }else {
                if (newDevice.isFailed() && newDevice.getPreGroupId() != null){
                    Log.e("AddNewDeviceActivity", "In The AddNewDeviceActivity: Going to addDeviceToPreviousGroup");
//                    Observable.zip(viewModel.deleteUserObservable(new DeleteUserRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId())),
//                            viewModel.addDeviceToGroupObservable(new AddDeviceToGroupRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getPreGroupId())),
//                            new BiFunction<BaseResponse, BaseResponse, Object>() {
//                                @Override
//                                public Object apply(BaseResponse baseResponse, BaseResponse baseResponse2) throws Exception {
//                                    Log.e("AddNewDeviceActivity", "Results of tofmal: " + baseResponse);
//                                    Log.e("AddNewDeviceActivity", "Results of tofmal: " + baseResponse2);
//                                    if (baseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION) && baseResponse2.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION)){
//                                        Log.e("AddNewDeviceActivity", "Both Done ");
//                                        Toast.makeText(AddNewDeviceActivity.this, "Fixed", Toast.LENGTH_SHORT).show();
//                                        AddNewDeviceActivity.getNewDevice().setFailed(false);
//                                    }
//                                    return new Object();
//                                }
//                            }).subscribe(new Consumer<Object>() {
//                        @Override
//                        public void accept(Object o) throws Exception {
//
//                        }
//                    });
//                    viewModel.addDeviceToGroup(new AddDeviceToGroupRequest(newDevice.getId(), newDevice.getPreGroupId()));
                }
                if (connectionStatusModel.getType() == AppConstants.WIFI_NETWORK) {
                    String extraInfo = connectionStatusModel.getSsid();
                    if (extraInfo != null && connectionStatusModel.getSsid().charAt(connectionStatusModel.getSsid().length()-1) == connectionStatusModel.getSsid().charAt(0) && String.valueOf(connectionStatusModel.getSsid().charAt(0)).equals("\""))
                        extraInfo = connectionStatusModel.getSsid().substring(1,extraInfo.length()-1);
                    this.wifiNetwork(connectionStatusModel.getState().equals(NetworkInfo.State.CONNECTED), extraInfo);
                } else if (connectionStatusModel.getType() == AppConstants.MOBILE_DATA) {
                    this.mobileNetwork(connectionStatusModel.getState().equals(NetworkInfo.State.CONNECTED));
                }
            }
        });
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("");
        stepperAdapter = new AddNewDeviceStepperAdapter(getSupportFragmentManager(), this);
        stepperLayout.setAdapter(stepperAdapter);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//        setActionBarTitle("");
        wifiReceiver = new WifiScanReceiver();
        wifiHandler = new WifiHandler();
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, NewDevicesListFragment.newInstance())
//                    .commitNow();
//        }
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (wifiReceiver != null)
        unregisterReceiver(wifiReceiver);
        super.onStop();
    }

    @SuppressLint("CheckResult")
    protected void onResume() {
        super.onResume();
        wifiHandler.setWifiEnabled();
//        ReactiveNetwork
//                .observeNetworkConnectivity(this)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(connectivity -> {
//                    Log.e("NetworkDetector", "From context: " + this + " connectivityIs: " + connectivity);
//                    switch (connectivity.type()){
//                        case AppConstants.WIFI_NETWORK:
//                            String extraInfo = connectivity.extraInfo();
//                            if (extraInfo != null && connectivity.extraInfo().charAt(connectivity.extraInfo().length()-1) == connectivity.extraInfo().charAt(0) && String.valueOf(connectivity.extraInfo().charAt(0)).equals("\""))
//                                extraInfo = connectivity.extraInfo().substring(1,extraInfo.length()-1);
//                            this.wifiNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED), extraInfo);
//                            break;
//                        case AppConstants.MOBILE_DATA:
//                            this.mobileNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED));
//                            break;
//                        case AppConstants.NOT_CONNECTED_NETWORK:
//                            this.notConnected();
//                            break;
//                    }
//                });
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        registerReceiver(wifiReceiver, intentFilter);
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        Log.e("=========---", "status Check: " + (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED));
        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            statusCheck();
            wifiHandler.scan();
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            buildAlertMessageNoGps();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            wifiHandler.scan();
            Log.e(TAG, "Permission Granted...");
        } else {
            Log.e(TAG, "Permission not Granted...");
            super.onBackPressed();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == null || !currentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.currentFragment = backHandledFragment;
    }

    @Override
    public void operationDone() {
        viewModel.getGroups();
//        NewDeviceSetConfigurationFragment setConfigFragment =(NewDeviceSetConfigurationFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:2131296496:1");
//        setConfigFragment.groupCreated();
        for (int a = 0; a<getSupportFragmentManager().getFragments().size();a++){
            Log.e("TAGGGGG: " , "TAG of: " +a+" Is: "+ getSupportFragmentManager().getFragments().get(a).getTag());
            Log.e("TAGGGGG: " , "Activity of: " +a+" Is: "+ getSupportFragmentManager().getFragments().get(a).getActivity());
            Log.e("TAGGGGG: " , "Id of: " +a+" Is: "+ getSupportFragmentManager().getFragments().get(a).getId());
            Log.e("TAGGGGG: " , "Parent Fragment of: " +a+" Is: "+ getSupportFragmentManager().getFragments().get(a).getParentFragment());
        }

        ChangeGroupFragment changeGroupFragment = (ChangeGroupFragment) getSupportFragmentManager().findFragmentByTag("changeGroup");
        changeGroupFragment.selectGroupMode();
    }

    public SetPrimaryConfigRequest getSetPrimaryConfigRequest() {
        return setPrimaryConfigRequest;
    }

    public static NewDevice getNewDevice(){
        return newDevice;
    }

    public void setStepperPosition(int current){
        stepperLayout.setCurrentStepPosition(current);
    }

    private void initNewDevice(){
        newDevice = new NewDevice();
        newDevice.setChip_id("");
        newDevice.setPin1("off");
        newDevice.setPin2("off");
    }

    @Override
    public void accessPointSelected(String ssid, String pass) {
        getNewDevice().setSsid(ssid);
        getNewDevice().setPwd(pass);
    }

    public AddNewDeviceStepperAdapter getStepperAdapter() {
        return stepperAdapter;
    }

    @Override
    public void submitClicked(String tag) {
        ChangeGroupFragment changeGroupFragment = (ChangeGroupFragment) getSupportFragmentManager().findFragmentByTag("changeGroup");
        CreateGroupFragment createGroupFragment = (CreateGroupFragment) changeGroupFragment.getChildFragmentManager().findFragmentByTag("createGroup");
        createGroupFragment.clickOnSubmit();

    }

    @Override
    public void onYesClicked(YesNoDialog yesNoDialog, Bundle data) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        yesNoDialog.dismiss();
    }

    @Override
    public void onNoClicked(YesNoDialog yesNoDialog, Bundle data) {
        yesNoDialog.dismiss();
        AddNewDeviceActivity.super.onBackPressed();
    }

    private void buildAlertMessageNoGps() {
        YesNoDialog yesNoDialog = new YesNoDialog(this, this,"برای ادامه نیاز به سرویس Location داریم", null);
        yesNoDialog.show();
    }

    @Override
    public void wifiNetwork(boolean connected, String ssid) {
//        Toast.makeText(this, "Wifi "+this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        List<Fragment> fs = getSupportFragmentManager().getFragments();
        NewDevicesListFragment newDevicesListFragment = null;
        for (int a = 0;a<fs.size();a++)
            if (fs.get(a) instanceof NewDevicesListFragment)
                newDevicesListFragment = (NewDevicesListFragment) fs.get(a);
        if (newDevicesListFragment != null)
            newDevicesListFragment.wifiNetwork(connected, ssid);
    }

    @Override
    public void mobileNetwork(boolean connected) {

    }

    @Override
    public void vpnNetwork() {

    }

    @Override
    public void notConnected() {
//        Toast.makeText(this, "NotConnected "+this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
    }
}
