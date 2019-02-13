package rayan.rayanapp.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Objects;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.BackHandledFragment;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.DevicesManagementActivityViewModel;

public class DeviceManagementListActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice, BackHandledFragment.BackHandlerInterface {


    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    DevicesManagementActivityViewModel viewModel;
    BackHandledFragment currentFragment;
    Device device;
    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        viewModel = ViewModelProviders.of(this).get(DevicesManagementActivityViewModel.class);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_deviceManagementActivity);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        DevicesManagementListFragment devicesManagementListFragment = DevicesManagementListFragment.newInstance();
        transaction.replace(R.id.frameLayout, devicesManagementListFragment);
        transaction.commit();
        disposable = ((RayanApplication)getApplication()).getBus().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o.getString("cmd").equals(AppConstants.SETTINGS) && !(currentFragment instanceof EditDeviceFragment)){
                Toast.makeText(this, "Message: " + o, Toast.LENGTH_SHORT).show();
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
                EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(device);
                transaction.replace(R.id.frameLayout, editGroupFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void clickOnDevice(Device device) {
        this.device = device;
        if (device.getIp() == null)
            Toast.makeText(this, "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
        else {
            viewModel.setReadyForSettings(device.getIp());
        }

    }

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
        if(currentFragment == null || !currentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
    public void setActionBarTitle(){
        getSupportActionBar().setTitle(R.string.title_deviceManagementActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.dispose();
    }
}
