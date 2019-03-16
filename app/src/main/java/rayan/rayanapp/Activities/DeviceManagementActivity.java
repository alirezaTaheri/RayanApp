package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DeviceManagementActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface, DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
//=======
//public class DeviceManagementActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface, OnBottomSheetSubmitClicked {
    EditDeviceFragment editDeviceFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
//=======
//public class DeviceManagementActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface, DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked {
//    EditDeviceFragment editDeviceFragment;
//    YesNoButtomSheetFragment yesNoButtomSheetFragment;
//>>>>>>> 61f7df95c05f5e7b5402a088a45aa1e4642821eb
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    DevicesManagementActivityViewModel viewModel;
    BackHandledFragment currentFragment;
    Device device;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        viewModel = ViewModelProviders.of(this).get(DevicesManagementActivityViewModel.class);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        yesNoButtomSheetFragment=new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        fragmentManager = getSupportFragmentManager();
         transaction = fragmentManager.beginTransaction();
        DevicesManagementListFragment devicesManagementListFragment = DevicesManagementListFragment.newInstance();
        transaction.replace(R.id.frameLayout, devicesManagementListFragment);
        transaction.commit();
    }

    @SuppressLint("CheckResult")
    @Override
    public void clickOnDevice(Device device) {}

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.currentFragment = backHandledFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && currentFragment == null || !currentFragment.onBackPressed()){
                return super.onOptionsItemSelected(item);
        }
        else
            return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
////<<<<<<< HEAD
//        device = viewModel.getDevice("111111");
//        device.setIp("192.168.1.102");
//        if (device != null){
//        transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
//        EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(device);
//        transaction.replace(R.id.frameLayout, editGroupFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();}
////=======
////        device = viewModel.getDevice("137067");
////        if (device != null){
////        transaction = fragmentManager.beginTransaction();
////        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
////        editDeviceFragment = EditDeviceFragment.newInstance(device);
////        transaction.replace(R.id.frameLayout, editDeviceFragment);
////        transaction.addToBackStack(null);
////        transaction.commit();
////        }
////        if(currentFragment == null || !currentFragment.onBackPressed()) {
////            super.onBackPressed();
////>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
////        }
////        if(currentFragment == null || !currentFragment.onBackPressed()) {
////            super.onBackPressed();
////        }
//    }
    public void setActionBarTitle(){
//        getSupportActionBar().setTitle(R.string.title_deviceManagementActivity);
        getSupportActionBar().setTitle("");
    }

    @Override
//<<<<<<< HEAD
//<<<<<<< HEAD
    public void accessPointSelected(String ssid, String pass) {

    }
//=======
//=======
//>>>>>>> 61f7df95c05f5e7b5402a088a45aa1e4642821eb
    public void submitClicked(String tag) {
        Log.e("tag of fragment",tag);
        switch (tag){
            case "EditDeviceFragment":
                editDeviceFragment.clickOnDeviceUpdateSubmit();
                break;
            default:
                break;
        }
//<<<<<<< HEAD
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
//=======


}

//    @Override
//    public void accessPointSelected(String ssid, String pass) {
////>>>>>>> 61f7df95c05f5e7b5402a088a45aa1e4642821eb
//
//    }
}
