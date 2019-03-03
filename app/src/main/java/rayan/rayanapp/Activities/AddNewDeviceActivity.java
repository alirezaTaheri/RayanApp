package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.stepstone.stepper.StepperLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Adapters.viewPager.AddNewDeviceStepperAdapter;
import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.ChangeGroupFragment;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.WifiScanReceiver;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;
import rayan.rayanapp.Wifi.WifiHandler;

public class AddNewDeviceActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface, DoneWithFragment, DoneWithSelectAccessPointFragment {
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
    private NewDevice newDevice;
    GroupsListFragmentViewModel viewModel;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);
        viewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        initNewDevice();
        setPrimaryConfigRequest = new SetPrimaryConfigRequest();
        ButterKnife.bind(this);
        stepperAdapter = new AddNewDeviceStepperAdapter(getSupportFragmentManager(), this);
        stepperLayout.setAdapter(stepperAdapter);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        setActionBarTitle("افزودن دستگاه جدید");
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
    protected void onStop() {
        if (wifiReceiver != null)
        unregisterReceiver(wifiReceiver);
        super.onStop();
    }

    protected void onResume() {
        wifiHandler.setWifiEnabled();
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        if (ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            statusCheck();
            wifiHandler.scan();
        }
        super.onResume();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("برای ادامه نیاز به سرویس Location داریم")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        AddNewDeviceActivity.super.onBackPressed();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    public NewDevice getNewDevice(){
        return newDevice;
    }

    public void setStepperPosition(int current){
        stepperLayout.setCurrentStepPosition(current);
    }

    private void initNewDevice(){
        newDevice = new NewDevice();
        newDevice.setChip_id("132465");
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
}
