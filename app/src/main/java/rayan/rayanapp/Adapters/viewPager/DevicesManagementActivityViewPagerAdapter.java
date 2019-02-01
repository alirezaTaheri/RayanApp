package rayan.rayanapp.Adapters.viewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import rayan.rayanapp.Fragments.DevicesManagementListFragment;
import rayan.rayanapp.Fragments.EditDeviceFragment;

public class DevicesManagementActivityViewPagerAdapter extends MainActivityViewPagerAdapter {

    public DevicesManagementActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DevicesManagementListFragment.newInstance();
            case 1:
                return EditDeviceFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
