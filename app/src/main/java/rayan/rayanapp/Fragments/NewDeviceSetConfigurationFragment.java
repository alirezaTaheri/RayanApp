package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Listeners.DoneWithFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.NewDeviceSetConfigurationFragmentViewModel;

public class NewDeviceSetConfigurationFragment extends BackHandledFragment implements BlockingStep {

    private NewDeviceSetConfigurationFragmentViewModel mViewModel;
    @BindView(R.id.changeAccessPoint)
    TextView changeAccessPoint;
    @BindView(R.id.changeGroup)
    TextView changeGroup;
    @BindView(R.id.name)
    EditText nameEditText;
    DoneWithFragment listener;

    public static NewDeviceSetConfigurationFragment newInstance() {
        return new NewDeviceSetConfigurationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.new_device_set_configuration_fragment, container, false);
        ButterKnife.bind(this,view);
        return view;
    }
    @OnClick(R.id.changeAccessPoint)
    void changeAccessPoint(){
        ChangeDeviceAccessPointFragment.newInstance().show(getActivity().getSupportFragmentManager(), "changeAccessPoint");
    }

    @OnClick(R.id.changeGroup)
    void changeGroup(){
        ChangeGroupFragment.newInstance().show(getActivity().getSupportFragmentManager(), "changeGroup");
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getActivity(), "HaHaHaHa", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NewDeviceSetConfigurationFragmentViewModel.class);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = ((DoneWithFragment)getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void groupCreated(){
        ChangeGroupFragment changeGroupFragment = (ChangeGroupFragment)getChildFragmentManager().findFragmentByTag("changeGroupFragment");

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (TextUtils.isEmpty(nameEditText.getText().toString().trim()))
        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا نام دستگاه را وارد کنید");
        else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getGroupId() == null || ((AddNewDeviceActivity)getActivity()).getNewDevice().getGroupId().trim().length()<1)
          SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک گروه را انتخاب کنید");
        else if (((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid() == null || ((AddNewDeviceActivity)getActivity()).getNewDevice().getSsid().trim().length()<1)
            SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"لطفا یک مودم را انتخاب کنید");
        else
        mViewModel.registerDeviceSendToDevice(((AddNewDeviceActivity)getActivity()),new RegisterDeviceRequest("someUsername", "DeviceName", "DeviceType") , new CreateTopicRequest(((AddNewDeviceActivity) getActivity()).getNewDevice().getId(), ((AddNewDeviceActivity) getActivity()).getNewDevice().getGroupId(), ((AddNewDeviceActivity) getActivity()).getNewDevice().getChip_id(), AppConstants.MQTT_HOST),new SetPrimaryConfigRequest("SSID", "pwd", "hostName", "mqttHost", "mqttPort", "mqttTopic", "mqttUser", "mqttPass", "hpwd"), "192.168.1.102");
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }
}
