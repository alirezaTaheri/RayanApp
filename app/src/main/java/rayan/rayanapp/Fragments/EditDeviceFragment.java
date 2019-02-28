package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

import static android.content.Context.CLIPBOARD_SERVICE;

public class EditDeviceFragment extends BackHandledFragment{
    private static EditDeviceFragment instance = null;
    private ArrayList<String> codeList= new ArrayList<>();
    private ArrayList<String> deviceFileList= new ArrayList<>();
    int startIndex=0, packetSize =150;
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
    @BindView(R.id.changeAccessPoint)
    TextView changeAccessPoint;
    @BindView(R.id.changeAccessPointIcon)
    ImageView changeAccessPointIcon;
    @BindView(R.id.changeAccessPointProgressBar)
    ProgressBar changeAccessPointProgressBar;
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
        ((DeviceManagementActivity)getActivity()).setActionBarTitle();
        getActivity().getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = getArguments().getParcelable("device");
        instance=this;
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
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"دسترسی اینترنتی با موفقیت ایجاد شد");
                    setDeviceTopicStatus(TopicStatus.CHANGED);
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    setDeviceTopicStatus(TopicStatus.CHANGED);
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطای اتصال");
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
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما دسترسی لازم برای تغییر نام را ندارید");
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.CHANGE_NAME_FALSE:
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"امکان ویرایش نام وجود ندارد");
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.OPERATION_DONE:
                    setDeviceNameStatus(NameStatus.CHANGED);
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ویرایش نام با موفقیت انجام شد");
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطای اتصال");
                    editDeviceFragmentViewModel.toDeviceChangeName(device.getName1(),device.getIp());
                    editDeviceFragmentViewModel.editDevice(device.getId(), device.getName1(), device.getType(), device.getGroupId());
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.ERROR:
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطایی رخ داد");
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
            }
        });
    }
    @OnClick(R.id.factoryReset)
    void toDeviceFactoryReset(){
        setDeviceTopicStatus(TopicStatus.CHANGING);
        editDeviceFragmentViewModel.toDeviceFactoryReset(device.getIp()).observe(this, s -> {
            assert s != null;
                switch (s){
                    case AppConstants.DEVICE_READY_FOR_UPDATE:

                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        setDeviceTopicStatus(TopicStatus.CHANGED); SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطای اتصال");
                        break;
                }
        });
    }

    @OnClick(R.id.deviceUpdate)
    void toDeviceUpdate(){
        String result=  editDeviceFragmentViewModel.readFromFile();
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().editDeviceInstance("EditDeviceFragment","بروز رسانی دستگاه", "بازگشت", "آیا مایل به بروزرسانی دستگاه هستید؟",convertCodeStringToList(result), AppConstants.DEVICE_DO_UPDATE, device.getIp());
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    public void clickOnDeviceUpdateSubmit(ArrayList<String> codelist, String cmd, String deviceIp){
        getDeviceFileList(AppConstants.DEVICE_ALL_FILES_LIST,deviceIp);
        if (permitToSendFiles()) {
            Toast.makeText(getActivity(), "trueeeee", Toast.LENGTH_SHORT).show();
            for (int i=0;i<=deviceFileList.size()-1;i++){
                Log.e("codelist items",deviceFileList.get(i));
                Toast.makeText(getActivity(), "codelist items"+deviceFileList.get(i), Toast.LENGTH_SHORT).show();
            }
            // TODO: 2/28/2019 uncomment below lines in the last adit of code(when connected to real device)

//            editDeviceFragmentViewModel.toDeviceUpdate(deviceIp).observe(this, s -> {
//                assert s != null;
//                switch (s) {
//                    case AppConstants.DEVICE_READY_FOR_UPDATE:
//                        editDeviceFragmentViewModel.toDeviceDoUpdate(cmd, codelist, deviceIp).observe(this, res -> {
//                            Log.e("response", res.toString());
//                        });
//                        break;
//                    case AppConstants.SOCKET_TIME_OUT:
//                        setDeviceTopicStatus(TopicStatus.CHANGED);
//                        SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content), "خطای اتصال");
//                        break;
//                }
//            });
        }
    }


    @OnClick(R.id.changeAccessPoint)
    void toDeviceChangeAccessPoint(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        else
            statusCheck();
//        editDeviceFragmentViewModel.toDeviceChangeAccessPoint().observe(this, s -> {
//            assert s != null;
//                switch (s){
//                    case AppConstants.FACTORY_RESET_DONE:
//                        Toast.makeText(getActivity(), "دستگاه با موفقیت ریست شد", Toast.LENGTH_SHORT).show();
//                        setDeviceTopicStatus(TopicStatus.CHANGED);
//                        break;
//                    case AppConstants.SOCKET_TIME_OUT:
//                        setDeviceTopicStatus(TopicStatus.CHANGED);
//                        Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//        });
    }

    public enum TopicStatus{
        CHANGING,
        CHANGED
    }
    public enum NameStatus {
        CHANGING,
        CHANGED
    }
    public enum ChangeAccessPointStatus {
        CHANGING,
        CHANGED
    }
    public enum UpdateStatus {
        UPDATING,
        UPDATED
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
    public void setChangeAccessPointStatus(ChangeAccessPointStatus changeAccessPointStatus){
        if (changeAccessPointStatus.equals(ChangeAccessPointStatus.CHANGING)){
            changeAccessPointIcon.setVisibility(View.INVISIBLE);
            changeAccessPointProgressBar.setVisibility(View.VISIBLE);
            changeAccessPoint.setEnabled(false);
        } else{
            changeAccessPoint.setEnabled(true);
            changeAccessPointProgressBar.setVisibility(View.INVISIBLE);
            changeAccessPointIcon.setVisibility(View.VISIBLE);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else
            ChangeDeviceAccessPointFragment.newInstance().show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("برای ادامه نیاز به سرویس Location داریم")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }



    public ArrayList<String> convertCodeStringToList(String result){
        codeList.clear();
        int j=result.length()/150;
        Log.e("String Length/150",""+j);
        for (int i=0;i<=j;i++){
            if(!(result.length()-startIndex >=150)){
                codeList.add(result.substring(startIndex, result.length()));
            }else {
                codeList.add(result.substring(startIndex, packetSize));
                startIndex += 150;
                packetSize += 150;
            }
        }
        Log.e("codeList",""+codeList.toString());
        Log.e("codeListsize",""+codeList.size());
        return codeList;
    }
    public static EditDeviceFragment getInstance() {
        return instance;
    }

    public void getDeviceFileList(String cmd, String deviceip){
        editDeviceFragmentViewModel.toDeviceAllFilesList(cmd, deviceip ).observe(this,res->{
            deviceFileList=res;
            Log.e("file listsss",deviceFileList.get(0));
            Toast.makeText(getActivity(), "file listsss"+deviceFileList.get(0), Toast.LENGTH_SHORT).show();
        });
    }

    boolean value=false;
    private boolean permitToSendFiles() {
        editDeviceFragmentViewModel.sendFilesToDevicePermit(AppConstants.DEVICE_SEND_FILES_PERMIT).observe(this,per->{
            Log.e("permitttttt",per);
            Toast.makeText(getActivity(), "permitttttt"+per, Toast.LENGTH_SHORT).show();
            switch (per){
                case "yes":
                    value=true;
                    break;
                case "no":
                    value=false;
                    break;
                default:
                    break;
            }
        });
        return value;
    }
}
