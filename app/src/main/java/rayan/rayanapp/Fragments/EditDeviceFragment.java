package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        editDeviceFragmentViewModel.toDeviceEndSettings();
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

    void editDevice(){
        editDeviceFragmentViewModel.editDevice(device.getId(), name.getText().toString(), device.getType(), device.getGroupId()).observe(this, deviceResponse -> {
            if (deviceResponse.getStatus().getCode().equals("404") && deviceResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "دستگاه با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
            }
            else if (deviceResponse.getStatus().getCode().equals("403") && deviceResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "شما قادر به اصلاح دستگاه نیستید", Toast.LENGTH_SHORT).show();
            }
            else if (deviceResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "دسترسی با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show();
                device.setName1(name.getText().toString());
                editDevice.setVisibility(View.INVISIBLE);
                editDeviceFragmentViewModel.updateDevice(device);
            }
            else
                Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
        });
    }

    @OnClick(R.id.setTopic)
    void createTopic(){
        editDeviceFragmentViewModel.createTopic(device.getId(), device.getChipId(), AppConstants.MQTT_HOST, device.getGroupId()).observe(this, deviceResponse ->{
            if (deviceResponse.getStatus().getCode().equals("404") && deviceResponse.getData().getMessage().equals("User not found")){
                Toast.makeText(getActivity(), "دستگاه با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
            }
            else if (deviceResponse.getStatus().getCode().equals("403") && deviceResponse.getData().getMessage().equals("Repeated")){
                Toast.makeText(getActivity(), "شما قادر به ایجاد دسترسی نیستید", Toast.LENGTH_SHORT).show();
            }
            else if (deviceResponse.getStatus().getCode().equals("200")){
                Toast.makeText(getActivity(), "دستگاه با موفقیت اصلاح شد", Toast.LENGTH_SHORT).show();
                device.setName1(name.getText().toString());
                editDevice.setVisibility(View.INVISIBLE);
                editDeviceFragmentViewModel.updateDevice(device);
            }
            else
                Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
        });
    }

    @OnClick(R.id.editDevice)
    void toDeviceChangeName(){
        editDeviceFragmentViewModel.zip(device.getId(), name.getText().toString(), device.getType(), device.getGroupId()).observe(this, s -> {
            switch (s){
                case AppConstants.FORBIDDEN:
                    editDeviceFragmentViewModel.toDeviceChangeName(device.getName1());
                    Toast.makeText(getActivity(), "شما دسترسی لازم برای تغییر نام را ندارید", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    editDevice.setVisibility(View.INVISIBLE);
                    break;
                case AppConstants.CHANGE_NAME_FALSE:
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    Toast.makeText(getActivity(), "امکان ویرایش نام وجود ندارد", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    editDevice.setVisibility(View.INVISIBLE);
                    break;
                case AppConstants.OPERATION_DONE:
                    editDevice.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "ویرایش نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                    editDeviceFragmentViewModel.toDeviceChangeName(device.getName1());
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    name.setText(device.getName1());
                    editDevice.setVisibility(View.INVISIBLE);
                    break;
                case AppConstants.ERROR:
                    Toast.makeText(getActivity(), "خطایی رخ داد", Toast.LENGTH_SHORT).show();
                    name.setText(device.getName1());
                    editDevice.setVisibility(View.INVISIBLE);
                    break;
            }
        });
    }

    void toDeviceEndSettings(){
        editDeviceFragmentViewModel.toDeviceEndSettings();
    }
}
