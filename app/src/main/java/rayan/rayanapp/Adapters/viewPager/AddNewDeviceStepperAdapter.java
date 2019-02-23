package rayan.rayanapp.Adapters.viewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

import rayan.rayanapp.Fragments.NewDeviceSetConfigurationFragment;
import rayan.rayanapp.Fragments.NewDevice_Switch_PhysicalVerificationFragment;
import rayan.rayanapp.Fragments.NewDevicesListFragment;

public class AddNewDeviceStepperAdapter extends AbstractFragmentStepAdapter {

    public AddNewDeviceStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position){
            case 0:
                return NewDevicesListFragment.newInstance();
            case 1:
                return NewDeviceSetConfigurationFragment.newInstance();
            case 2:
                return NewDevice_Switch_PhysicalVerificationFragment.newInstance();
        }
        return  null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
