package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.StepperItemSimulation;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDevicePhysicalVerificationViewModel;

public class NewDevice_Plug_PhysicalVerificationFragment extends Fragment implements StepperItemSimulation, YesNoDialogListener {
    private OnFragmentInteractionListener mListener;
    NewDevicePhysicalVerificationViewModel viewModel;
    @BindView(R.id.responses)
    TextView responses;
    public NewDevice_Plug_PhysicalVerificationFragment() {

    }

    public static NewDevice_Plug_PhysicalVerificationFragment newInstance() {
        NewDevice_Plug_PhysicalVerificationFragment fragment = new NewDevice_Plug_PhysicalVerificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_device_physical_verification_plug, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @OnClick(R.id.onIcon)
    void sendOnStatus(){
        viewModel.toDeviceStatus(AppConstants.NEW_DEVICE_ON_STATUS).observe(this, deviceBaseResponse -> {
            responses.append("\n"+deviceBaseResponse.toString());
            switch (deviceBaseResponse.getCmd()){
                case AppConstants.NEW_DEVICE_PHV_TRUE:
                    Toast.makeText(getActivity(), "درست", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_FALSE:
//                    Toast.makeText(getActivity(), "غلط", Toast.LENGTH_SHORT).show();
                    YesNoDialog yesNoDialog = new YesNoDialog(getActivity(), this,"دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_TIMEOUT:
                    Toast.makeText(getActivity(), "زمان شما به پایان رسیده است", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_VERIFIED:
                    Toast.makeText(getActivity(), "دسترسی شما تایید شد", Toast.LENGTH_SHORT).show();
                    ((AddNewDeviceActivity)getContext()).setStepperPosition(3);
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getActivity(), "پاسخی دریافت نشد", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    @OnClick(R.id.offIcon)
    void sendOffStatus(){
        viewModel.toDeviceStatus(AppConstants.NEW_DEVICE_OFF_STATUS).observe(this, deviceBaseResponse -> {
            responses.append("\n"+deviceBaseResponse.toString());
            switch (deviceBaseResponse.getCmd()){
                case AppConstants.NEW_DEVICE_PHV_TRUE:
                    Toast.makeText(getActivity(), "درست", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_FALSE:
                    YesNoDialog yesNoDialog = new YesNoDialog(getActivity(), this,"دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
                    yesNoDialog.show();
//                    Toast.makeText(getActivity(), "غلط", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_TIMEOUT:
                    Toast.makeText(getActivity(), "زمان شما به پایان رسیده است", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstants.NEW_DEVICE_PHV_VERIFIED:
                    Toast.makeText(getActivity(), "دسترسی شما تایید شد", Toast.LENGTH_SHORT).show();
                    ((AddNewDeviceActivity)getContext()).setStepperPosition(3);
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getActivity(), "پاسخی دریافت نشد", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
    @Override
    public void onSelect() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
    }

    @Override
    public void onBackClicked() {
        ((AddNewDeviceActivity)getContext()).setStepperPosition(1);
    }

    @Override
    public void onYesClicked(YesNoDialog yesNoDialog, Bundle data) {
        viewModel.toDeviceFirstConfig(
                new SetPrimaryConfigRequest(((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getPwd(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getName(),
                        AppConstants.MQTT_HOST,
                        String.valueOf(AppConstants.MQTT_PORT_SSL),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getTopic().getTopic(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getUsername(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getPassword(),
                        AppConstants.DEVICE_CONNECTED_STYLE,
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getSecret())).observe(this, configResponse -> {
            switch (configResponse.getCmd()) {
                case AppConstants.NEW_DEVICE_PHV_START:
                    yesNoDialog.dismiss();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                    break;
                default: Toast.makeText(getContext(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoClicked(YesNoDialog yesNoDialog, Bundle data) {
        getActivity().onBackPressed();
        yesNoDialog.dismiss();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
