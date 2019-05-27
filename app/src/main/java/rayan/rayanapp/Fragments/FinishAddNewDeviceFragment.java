package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDevicePhysicalVerificationViewModel;

public class FinishAddNewDeviceFragment extends Fragment implements BlockingStep {
    private OnFragmentInteractionListener mListener;
    private NewDevice device;
    private int toggleCount = -1;
    @BindView(R.id.command)
    TextView message;
    NewDevicePhysicalVerificationViewModel viewModel;

    public FinishAddNewDeviceFragment() {

    }

    public static FinishAddNewDeviceFragment newInstance() {
        FinishAddNewDeviceFragment fragment = new FinishAddNewDeviceFragment();
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
        View view = inflater.inflate(R.layout.fragment_finish_add_new_device, container, false);
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
        viewModel.toDeviceITET().observe(this, deviceBaseResponse -> {
            switch (deviceBaseResponse.getCmd()){
                case AppConstants.PRIMARY_CONFIG_TRUE:
                    Toast.makeText(getActivity(), "دسترسی شما با موفقیت تایید شد", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.PRIMARY_CONFIG_FALSE:
                    Toast.makeText(getActivity(), "دسترسی شما تایید نشد\nدوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.EXPIRED:
                    Toast.makeText(getActivity(), "زمان شما به اتمام رسیده است", Toast.LENGTH_SHORT).show();
                    break;

            }
        });
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
//        Objects.requireNonNull(getActivity()).onBackPressed();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        startActivity(new Intent(getActivity(), MainActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
