package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.DeviceManagementListActivity;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

public class EditDeviceFragment extends BackHandledFragment{

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.onlineAccessTextView)
    TextView onlineAccess;
    @BindView(R.id.editDevice)
    TextView editDevice;
    @BindView(R.id.progressBar)
    ProgressBar editDeviceProgressBar;
    @BindView(R.id.setTopicProgressBar)
    ProgressBar setTopicProgressBar;
    @BindView(R.id.setTopicIcon)
    ImageView setTopicIcon;
    @BindView(R.id.setTopic)
    TextView setTopic;
    EditDeviceFragmentViewModel editDeviceFragmentViewModel;
    Device device;
    public EditDeviceFragment() {
    }

    public static EditDeviceFragment newInstance(Device device) {
        EditDeviceFragment fragment = new EditDeviceFragment();
        Bundle args = new Bundle();
        args.putParcelable("device",device);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public boolean onBackPressed() {
        editDeviceFragmentViewModel.toDeviceEndSettings(device.getIp());
        ((DeviceManagementListActivity)getActivity()).setActionBarTitle();
        getActivity().getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = getArguments().getParcelable("device");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_device, container, false);
        ButterKnife.bind(this, view);
        init();
        editDeviceFragmentViewModel = ViewModelProviders.of(this).get(EditDeviceFragmentViewModel.class);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().equals(device.getName1())){
                    editDevice.setVisibility(View.INVISIBLE);
                }
                else{
                    editDevice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    public void init(){
        name.setText(device.getName1());
        onlineAccess.setVisibility(device.isReady4Mqtt()? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick(R.id.setTopic)
    void createTopic(){
        setDeviceTopicStatus(TopicStatus.CHANGING);
        editDeviceFragmentViewModel.flatMqtt(device).observe(this, s -> {
            assert s != null;
            switch (s){
                case AppConstants.SET_TOPIC_MQTT_Response:
                    Toast.makeText(getActivity(), "دسترسی اینترنتی با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                    setDeviceTopicStatus(TopicStatus.CHANGED);
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    setDeviceTopicStatus(TopicStatus.CHANGED);
                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @OnClick(R.id.editDevice)
    void toDeviceChangeName(){
        setDeviceNameStatus(NameStatus.CHANGING);
        editDeviceFragmentViewModel.zipChangeName(device.getId(), name.getText().toString(), device.getType(), device.getGroupId(), device.getIp()).observe(this, s -> {
            switch (s){
                case AppConstants.FORBIDDEN:
                    editDeviceFragmentViewModel.toDeviceChangeName(device.getName1(), device.getIp());
                    Toast.makeText(getActivity(), "شما دسترسی لازم برای تغییر نام را ندارید", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.CHANGE_NAME_FALSE:
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    Toast.makeText(getActivity(), "امکان ویرایش نام وجود ندارد", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.OPERATION_DONE:
                    setDeviceNameStatus(NameStatus.CHANGED);
                    Toast.makeText(getActivity(), "ویرایش نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                    editDeviceFragmentViewModel.toDeviceChangeName(device.getName1(),device.getIp());
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.ERROR:
                    Toast.makeText(getActivity(), "خطایی رخ داد", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
            }
        });
    }

    @OnClick(R.id.factoryReset)
    void toDeviceFactoryReset(){
        editDeviceFragmentViewModel.toDeviceFactoryReset(device.getIp()).observe(this, s -> {
            assert s != null;
                switch (s){
                    case AppConstants.FACTORY_RESET_DONE:
                        Toast.makeText(getActivity(), "دسترسی اینترنتی با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                        setDeviceTopicStatus(TopicStatus.CHANGED);
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        setDeviceTopicStatus(TopicStatus.CHANGED);
                        Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                        break;
                }
        });
    }

    public enum TopicStatus{
        CHANGING,
        CHANGED
    }
    public enum NameStatus {
        CHANGING,
        CHANGED
    }

    public void setDeviceNameStatus(NameStatus nameStatus){
        if (nameStatus.equals(NameStatus.CHANGING)){
            editDeviceProgressBar.setVisibility(View.VISIBLE);
            editDevice.setVisibility(View.INVISIBLE);
        }else{
            editDeviceProgressBar.setVisibility(View.INVISIBLE);
            editDevice.setVisibility(View.INVISIBLE);
        }
    }
    public void setDeviceTopicStatus(TopicStatus topicStatus){
        if (topicStatus.equals(TopicStatus.CHANGING)){
            setTopicIcon.setVisibility(View.INVISIBLE);
            setTopicProgressBar.setVisibility(View.VISIBLE);
            setTopic.setEnabled(false);
        } else{
            setTopic.setEnabled(true);
            setTopicProgressBar.setVisibility(View.INVISIBLE);
            setTopicIcon.setVisibility(View.VISIBLE);
        }
    }
}
