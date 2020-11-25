package rayan.rayanapp.Adapters.viewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Fragments.FavoritesFragment;
import rayan.rayanapp.Fragments.ScenariosFragment;

public class BottomNavigationViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public BottomNavigationViewPagerAdapter(FragmentManager manager) {
        super(manager);
        ScenariosFragment scenariosFragment=new ScenariosFragment();
        DevicesFragment devicesFragment=new DevicesFragment();
        FavoritesFragment favoritesFragment=new FavoritesFragment();
        mFragmentList.add(scenariosFragment);
        mFragmentList.add(devicesFragment);
        mFragmentList.add(favoritesFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}