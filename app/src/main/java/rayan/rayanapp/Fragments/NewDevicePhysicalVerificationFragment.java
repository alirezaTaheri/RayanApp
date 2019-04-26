package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Listeners.StepperItemSimulation;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDevicePhysicalVerificationViewModel;

public class NewDevicePhysicalVerificationFragment extends Fragment implements BlockingStep {
    private OnFragmentInteractionListener mListener;
    NewDevicePhysicalVerificationViewModel viewModel;
    StepperItemSimulation currentFragment;

    public NewDevicePhysicalVerificationFragment() {

    }

    public static NewDevicePhysicalVerificationFragment newInstance() {
        NewDevicePhysicalVerificationFragment fragment = new NewDevicePhysicalVerificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(NewDevicePhysicalVerificationViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_device_physical_verification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        currentFragment.onNextClicked(callback);
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        if (((AddNewDeviceActivity)getActivity()).getNewDevice().getType().contains(AppConstants.DEVICE_TYPE_SWITCH_1)|| ((AddNewDeviceActivity)getActivity()).getNewDevice().getType().contains(AppConstants.DEVICE_TYPE_SWITCH_2) || ((AddNewDeviceActivity)getActivity()).getNewDevice().getType().contains(AppConstants.DEVICE_TYPE_TOUCH_2)){
            Log.e("aaaaaa","NewDevice_Switch_PhysicalVerificationFragment:");
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (currentFragment != null)
                transaction.remove((Fragment) currentFragment).commitNow();
//            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            currentFragment = NewDevice_Switch_PhysicalVerificationFragment.newInstance();
            transaction.add(R.id.frameLayout, (Fragment) currentFragment, "switchVerification");
            transaction.commit();
        }
        else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getType().contains(AppConstants.DEVICE_TYPE_PLUG)){
            Log.e("aaaaaa","NewDevice_Plug_PhysicalVerificationFragment:");
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (currentFragment != null)
                transaction.remove((Fragment) currentFragment).commitNow();
//            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            currentFragment = NewDevice_Plug_PhysicalVerificationFragment.newInstance();
            transaction.add(R.id.frameLayout, (Fragment) currentFragment, "plugVerification");
            transaction.commit();
        }
        else {
            Log.e("aaaaaa","NewDevice_Plug_PhysicalVerificationFragment:");
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (currentFragment != null)
                transaction.remove((Fragment) currentFragment).commitNow();
//            getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            currentFragment = NewDevice_Plug_PhysicalVerificationFragment.newInstance();
            transaction.add(R.id.frameLayout, (Fragment) currentFragment, "plugVerification");
            transaction.commit();
            Toast.makeText(getActivity(), "نوع دستگاه ناشناخته است", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
