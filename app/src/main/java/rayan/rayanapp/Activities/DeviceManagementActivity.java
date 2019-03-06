package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.Objects;

import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DeviceManagementActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface, DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked {
    EditDeviceFragment editDeviceFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;
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
        yesNoButtomSheetFragment=new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_deviceManagementActivity);
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
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
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

    @Override
    public void onBackPressed() {
        device = viewModel.getDevice("137067");
        if (device != null){
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        editDeviceFragment = EditDeviceFragment.newInstance(device);
        transaction.replace(R.id.frameLayout, editDeviceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        }
//        if(currentFragment == null || !currentFragment.onBackPressed()) {
//            super.onBackPressed();
//        }
    }
    public void setActionBarTitle(){
        getSupportActionBar().setTitle(R.string.title_deviceManagementActivity);
    }

    @Override
    public void submitClicked(String tag) {
        Log.e("tag of fragment",tag);
        switch (tag){
            case "EditDeviceFragment":
                editDeviceFragment.clickOnDeviceUpdateSubmit();
                break;
            default:
                break;
        }


}

    @Override
    public void accessPointSelected(String ssid, String pass) {

    }
}
