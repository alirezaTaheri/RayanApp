package rayan.rayanapp.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Objects;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.R;

public class DeviceManagementListActivity extends AppCompatActivity implements DevicesManagementListFragment.ClickOnDevice {


    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_deviceManagementActivity);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        DevicesManagementListFragment devicesManagementListFragment = DevicesManagementListFragment.newInstance();
        transaction.replace(R.id.frameLayout, devicesManagementListFragment);
        transaction.commit();
    }

    @Override
    public void clickOnDevice(Device device) {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        EditDeviceFragment editGroupFragment = EditDeviceFragment.newInstance(device);
        transaction.replace(R.id.frameLayout, editGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}
