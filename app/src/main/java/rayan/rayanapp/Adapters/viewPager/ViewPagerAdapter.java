package rayan.rayanapp.Adapters.viewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Fragments.FavoritesFragment;
import rayan.rayanapp.Fragments.ScenariosFragment;

/**
 * Created by alireza321 on 24/01/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return ScenariosFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return DevicesFragment.newInstance();
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return FavoritesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}
