package rayan.rayanapp.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Adapters.viewPager.DevicesManagementActivityViewPagerAdapter;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.DeviceListDialogFragment;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.NonSwipeableViewPager;

public class DeviceManagementListActivity extends AppCompatActivity implements DevicesManagementListFragment.SendDevice {

    @BindView(R.id.viewPager)
    NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);
        ButterKnife.bind(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_deviceManagementActivity);
        DevicesManagementActivityViewPagerAdapter viewPagerAdapter = new DevicesManagementActivityViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void sendDevice(Device device) {
        String tag = "android:switcher:" + R.id.viewPager + ":" + 1;
        EditDeviceFragment editDeviceFragment = (EditDeviceFragment) getSupportFragmentManager().findFragmentByTag(tag);
        assert editDeviceFragment != null;
        editDeviceFragment.init(device);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (viewPager.getCurrentItem() == 0) {
                    super.onBackPressed();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                }
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}
