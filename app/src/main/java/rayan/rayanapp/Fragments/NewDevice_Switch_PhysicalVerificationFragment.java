package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.StepperItemSimulation;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.NewDevicePhysicalVerificationViewModel;

public class NewDevice_Switch_PhysicalVerificationFragment extends Fragment implements StepperItemSimulation, YesNoDialogListener {
    private OnFragmentInteractionListener mListener;
    private NewDevice device;
    private int toggleCount = -1;
    @BindView(R.id.command)
    TextView command;
    NewDevicePhysicalVerificationViewModel viewModel;

    public NewDevice_Switch_PhysicalVerificationFragment() {

    }

    public static NewDevice_Switch_PhysicalVerificationFragment newInstance() {
        NewDevice_Switch_PhysicalVerificationFragment fragment = new NewDevice_Switch_PhysicalVerificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_device_physical_verification_switch, container, false);
        ButterKnife.bind(this, view);
        toggleCount = ((AddNewDeviceActivity)getActivity()).getNewDevice().getToggleCount();
        command.setText("لطفا دستگاه را " + toggleCount + " بار روشن و خاموش کنید" + "\nسپس روی بعدی کلیک کنید");
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        viewModel.toDeviceITET().observe(this, deviceBaseResponse -> {
            switch (deviceBaseResponse.getCmd()){
                case AppConstants.PRIMARY_CONFIG_TRUE:
                    Toast.makeText(getActivity(), "دسترسی شما با موفقیت تایید شد", Toast.LENGTH_SHORT).show();
                    callback.goToNextStep();
                    break;
                case AppConstants.PRIMARY_CONFIG_FALSE:
                    Toast.makeText(getActivity(), "دسترسی شما تایید نشد\nدوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                    YesNoDialog yesNoDialog = new YesNoDialog(getActivity(), R.style.ProgressDialogTheme, this,"دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟");
                    yesNoDialog.show();
                    break;
                case AppConstants.EXPIRED:
                    Toast.makeText(getActivity(), "زمان شما به اتمام رسیده است", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onBackClicked() {
        ((AddNewDeviceActivity)getContext()).setStepperPosition(1);
    }

    @Override
    public void onYesClicked(YesNoDialog dialog) {
        viewModel.toDeviceFirstConfig(
                new SetPrimaryConfigRequest(((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getPwd(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getName(),
                        AppConstants.MQTT_HOST,
                        String.valueOf(AppConstants.MQTT_PORT),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getTopic().getTopic(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getUsername(),
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getPassword(),
                        AppConstants.DEVICE_CONNECTED_STYLE,
                        ((AddNewDeviceActivity)getActivity()).getNewDevice().getGroup().getSecret())).observe(this, configResponse -> {
                                            switch (configResponse.getCmd()) {
                                                case AppConstants.NEW_DEVICE_TOGGLE_CMD:
                                                    ((AddNewDeviceActivity)getActivity()).getNewDevice().setToggleCount(Integer.parseInt(configResponse.getCount()));
                                                    toggleCount = ((AddNewDeviceActivity)getActivity()).getNewDevice().getToggleCount();
                                                    command.setText("لطفا دستگاه را " + toggleCount + " بار روشن و خاموش کنید" + "\nسپس روی بعدی کلیک کنید");
                                                    dialog.dismiss();
                                                    break;
                                                case AppConstants.SOCKET_TIME_OUT:
                                                    Toast.makeText(getContext(), "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                                                    break;
                                                    default: Toast.makeText(getContext(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                                            }
                        });
    }

    @Override
    public void onNoClicked(YesNoDialog dialog) {
        getActivity().onBackPressed();
        dialog.dismiss();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
