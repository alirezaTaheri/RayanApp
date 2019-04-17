package rayan.rayanapp.Adapters.viewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Fragments.FinishAddNewDeviceFragment;
import rayan.rayanapp.Fragments.NewDevicePhysicalVerificationFragment;
import rayan.rayanapp.Fragments.NewDeviceSetAccessPoint;
import rayan.rayanapp.Fragments.NewDeviceSetConfigurationFragment;
import rayan.rayanapp.Fragments.NewDeviceSetGroupFragment;
import rayan.rayanapp.Fragments.NewDevice_Plug_PhysicalVerificationFragment;
import rayan.rayanapp.Fragments.NewDevice_Switch_PhysicalVerificationFragment;
import rayan.rayanapp.Fragments.NewDevicesListFragment;

public class AddNewDeviceStepperAdapter extends AbstractFragmentStepAdapter {

    FragmentManager fm;
    NewDevice_Plug_PhysicalVerificationFragment physicalPlug;
    NewDevice_Switch_PhysicalVerificationFragment physicalSwitch;
    BlockingStep physicalFragment;
    public AddNewDeviceStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
        this.fm = fm;
        physicalPlug = NewDevice_Plug_PhysicalVerificationFragment.newInstance();
        physicalSwitch = NewDevice_Switch_PhysicalVerificationFragment.newInstance();
    }

    @Override
    public Step createStep(int position) {
        Log.e("///////////","createStep " + position + "\nPhysical: " + physicalFragment);
        switch (position){
            case 0:
                return NewDevicesListFragment.newInstance();
            case 1:
                return NewDeviceSetGroupFragment.newInstance();
            case 2:
                return NewDeviceSetAccessPoint.newInstance();
            case 3:
//                Log.e("CCCCCCCCCCC", "COuntER: " + AddNewDeviceActivity.counter);
//                if (AddNewDeviceActivity.counter %2 == 0){
//                    Log.e("CCCCCCCCCCC", "NewDevice_Switch_PhysicalVerificationFragment: " + AddNewDeviceActivity.counter);
//                    return NewDevice_Switch_PhysicalVerificationFragment.newInstance();
//                }
//                else{
//                    Log.e("CCCCCCCCCCC", "NewDevice_Plug_PhysicalVerificationFragment: " + AddNewDeviceActivity.counter);
//                    return NewDevice_Plug_PhysicalVerificationFragment.newInstance();
//                }
//                if (((AddNewDeviceActivity)context).getNewDevice().getAccessPointName().contains("Alireza")){
//                    if (physicalFragment != null)
//                        fm.beginTransaction().remove((Fragment)physicalFragment).commit();
//                    physicalFragment = NewDevice_Switch_PhysicalVerificationFragment.newInstance();
////                    notifyDataSetChanged();
//                }
//                else{
//                    if (physicalFragment != null)
//                        fm.beginTransaction().remove((Fragment)physicalFragment).commit();
//                    physicalFragment = NewDevice_Plug_PhysicalVerificationFragment.newInstance();
//            }
//            Log.e("iiiiiiiiiii", "iiiiiiiiii: " + physicalFragment);
                return NewDevicePhysicalVerificationFragment.newInstance();
            case 4:
                return FinishAddNewDeviceFragment.newInstance();
        }
        return  null;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(int position) {
        return super.getViewModel(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_UNCHANGED;
    }


    @Override
    public int getCount() {
        return 5;
    }
}
