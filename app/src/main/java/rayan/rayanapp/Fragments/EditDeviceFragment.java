package rayan.rayanapp.Fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.DeviceManagementActivity;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Dialogs.YesNoDialog;
import rayan.rayanapp.Listeners.DoneWithSelectAccessPointFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.Listeners.YesNoDialogListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.device.VersionResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.FTPClient;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.EditDeviceFragmentViewModel;

public class EditDeviceFragment extends BackHandledFragment implements DoneWithSelectAccessPointFragment, OnBottomSheetSubmitClicked, YesNoDialogListener {
    private static EditDeviceFragment instance = null;
    String readFromFileResult;
    private ArrayList<String> codeList= new ArrayList<>();
    private ArrayList<String> deviceFileList= new ArrayList<>();
    int startIndex=0, packetSize =150;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.editDevice)
    TextView editDevice;
    @BindView(R.id.progressBar)
    ProgressBar editDeviceProgressBar;
//    @BindView(R.id.setTopicProgressBar)
//    ProgressBar setTopicProgressBar;
//    @BindView(R.id.setTopicIcon)
//    ImageView setTopicIcon;
//    @BindView(R.id.setTopic)
//    TextView setTopic;
    @BindView(R.id.changeAccessPoint)
    TextView changeAccessPoint;
    @BindView(R.id.changeAccessPointIcon)
    ImageView changeAccessPointIcon;
    @BindView(R.id.changeAccessPointProgressBar)
    ProgressBar changeAccessPointProgressBar;
    EditDeviceFragmentViewModel editDeviceFragmentViewModel;
    Device device;
    @BindView(R.id.deviceUpdate)
    TextView deviceUpdateButton;
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
        Log.e("///////", "/////onbakcmanamanaman");
        try {
            editDeviceFragmentViewModel.toDeviceEndSettings(device).observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    if (s.equals(AppConstants.END_SETTINGS)){
                        Toast.makeText(getContext(), "تنظیمات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                    }else {
//                        Toast.makeText(getContext(), "تنظیمات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        ((DeviceManagementActivity)getActivity()).setActionBarTitle();
        getActivity().getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
        deviceUpdateButton.setVisibility(View.GONE);
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
//        onlineAccess.setVisibility(device.isReady4Mqtt()? View.VISIBLE : View.INVISIBLE);
    }


    @OnClick(R.id.editDevice)
    void toDeviceChangeName(){
        setDeviceNameStatus(NameStatus.CHANGING);
        editDeviceFragmentViewModel.zipChangeName(device ,device.getId(), name.getText().toString(), device.getType(), device.getGroupId(), device.getIp(), device.getSsid()).observe(this, s -> {
            switch (s){
                case AppConstants.FORBIDDEN:
                    Log.e(this.getClass().getSimpleName(), "FORBIDDEN CHANGENAME");
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما دسترسی لازم برای تغییر نام را ندارید");
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.CHANGE_NAME_FALSE:
                    Log.e(this.getClass().getSimpleName(), "CHANGE_NAME_FALSE CHANGENAME");
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"امکان ویرایش نام وجود ندارد");
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.OPERATION_DONE:
                    Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                    setDeviceNameStatus(NameStatus.CHANGED);
                    device.setName1(name.getText().toString());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ویرایش نام با موفقیت انجام شد");
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.CHANGE_NAME_TRUE:
                    Log.e(this.getClass().getSimpleName(), "DONE CHANGENAME");
                    setDeviceNameStatus(NameStatus.CHANGED);
                    device.setName1(name.getText().toString());
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"ویرایش نام با موفقیت انجام شد");
                    editDeviceFragmentViewModel.getGroups();
                    break;
                case AppConstants.SOCKET_TIME_OUT:
                    Log.e(this.getClass().getSimpleName(), "SOCKET_TIME_OUT CHANGENAME");
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطای اتصال");
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
                case AppConstants.ERROR:
                    Log.e(this.getClass().getSimpleName(), "ERROR CHANGENAME");
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"خطایی رخ داد");
                    name.setText(device.getName1());
                    setDeviceNameStatus(NameStatus.CHANGED);
                    break;
            }
        });
    }
    @SuppressLint("CheckResult")
    @OnClick(R.id.factoryReset)
    void toDeviceFactoryReset(){
//        editDeviceFragmentViewModel.toDeviceFactoryReset(device).observe(EditDeviceFragment.this, s -> {
//            assert s != null;
//            switch (s){
//                case AppConstants.FACTORY_RESET_DONE:
//                    Toast.makeText(getActivity(), "دستگاه با موفقیت ریست شد", Toast.LENGTH_SHORT).show();
//                    setDeviceTopicStatus(EditDeviceFragment.TopicStatus.CHANGED);
//                    editDeviceFragmentViewModel.getGroups();
//                    break;
//                case AppConstants.SOCKET_TIME_OUT:
//                    setDeviceTopicStatus(EditDeviceFragment.TopicStatus.CHANGED);
//                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
//                    break;
//                case AppConstants.ERROR:
//                    Toast.makeText(getActivity(), "خطایی رخ داد", Toast.LENGTH_SHORT).show();
//                    break;
//                case AppConstants.USER_NOT_FOUND_RESPONSE:
//                    Toast.makeText(getActivity(), "دستگاهی با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        });
        YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().instance("resetDevice","تایید", "لغو", "آیا مایل به ریست کردن دستگاه هستید؟");
        bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
    @SuppressLint("CheckResult")
    public void resetDevice(){
        editDeviceFragmentViewModel.internetProvided().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((aBoolean, throwable) -> {
                    if (aBoolean)
                        editDeviceFragmentViewModel.toDeviceFactoryReset(device).observe(EditDeviceFragment.this, s -> {
                            assert s != null;
                            switch (s){
                                case AppConstants.FACTORY_RESET_DONE:
                                    Toast.makeText(getActivity(), "دستگاه با موفقیت ریست شد", Toast.LENGTH_SHORT).show();
                                    setDeviceTopicStatus(EditDeviceFragment.TopicStatus.CHANGED);
                                    editDeviceFragmentViewModel.getGroups();
                                    break;
                                case AppConstants.SOCKET_TIME_OUT:
                                    setDeviceTopicStatus(EditDeviceFragment.TopicStatus.CHANGED);
                                    Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                                    break;
                                case AppConstants.ERROR:
                                    Toast.makeText(getActivity(), "خطایی رخ داد", Toast.LENGTH_SHORT).show();
                                    break;
                                case AppConstants.USER_NOT_FOUND_RESPONSE:
                                    Toast.makeText(getActivity(), "دستگاهی با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        });
                    else ProvideInternetFragment.newInstance().show(getActivity().getSupportFragmentManager(), "provideInternet");
                });
    }

    @OnClick(R.id.deviceUpdate)
    void toDeviceUpdate(){
        editDeviceFragmentViewModel.toDeviceReady4Update(device).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                switch (s){
                    case AppConstants.DEVICE_READY_FOR_UPDATE:
                        Toast.makeText(getActivity(), "شروع بروزرسانی", Toast.LENGTH_SHORT).show();
                        FTPClient ftpClient = new FTPClient();
                        Log.e(this.getClass().getSimpleName(), "Updating device: " + device);
                        ftpClient.uploadFile(getContext(), device.getIp(), device.getChipId(), device.getSecret());
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        Toast.makeText(getActivity(), "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.ERROR:
                        Toast.makeText(getActivity(), "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(), "پاسخ نامرتبط دریافت شد", Toast.LENGTH_SHORT).show();
                }
//                yesNoDialog.dismiss();
            }
        });
//        editDeviceFragmentViewModel.getDeviceVersion(device).observe(this, s -> Toast.makeText(getActivity(), ""+s, Toast.LENGTH_SHORT).show());
//        YesNoDialog yesNoDialog = new YesNoDialog(getActivity(), this, "دسترسی شما تایید نشد"+"\nآیا مایل به تلاش دوباره هستید؟", null);
//        yesNoDialog.show();
    }

    @OnClick(R.id.changeAccessPoint)
    void toDeviceChangeAccessPoint(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if(!editDeviceFragmentViewModel.hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        else
            statusCheck();
    }

    @Override
    public void accessPointSelected(String ssid, String pass) {
        Log.e(this.getClass().getSimpleName(),"Received SSID Password from dialog:" + ssid + pass);
        editDeviceFragmentViewModel.toDeviceChangeAccessPoint(device, ssid, pass).observe(this, s -> {
            assert s != null;
                switch (s){
                    case AppConstants.CHANGING_WIFI:
                        Toast.makeText(getActivity(), "دستگاه در‌حال اعمال تغییرات می‌باشد", Toast.LENGTH_SHORT).show();
                        setDeviceTopicStatus(TopicStatus.CHANGED);
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        setDeviceTopicStatus(TopicStatus.CHANGED);
                        Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.ERROR:
                        setDeviceTopicStatus(TopicStatus.CHANGED);
                        Toast.makeText(getActivity(), "خطای اتصال", Toast.LENGTH_SHORT).show();
                        break;
                }
        });
    }

    @Override
    public void submitClicked(String tag) {
        FTPClient ftpClient = new FTPClient();
        Log.e(this.getClass().getSimpleName(), "Updating device: " + device);
        ftpClient.uploadFile(getContext(), device.getIp(), device.getChipId(), device.getSecret());
    }

    @Override
    public void onYesClicked(YesNoDialog yesNoDialog, Bundle data) {
        editDeviceFragmentViewModel.toDeviceReady4Update(device).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                switch (s){
                    case AppConstants.DEVICE_READY_FOR_UPDATE:
                        Toast.makeText(getActivity(), "شروع بروزرسانی", Toast.LENGTH_SHORT).show();
                        FTPClient ftpClient = new FTPClient();
                        Log.e(this.getClass().getSimpleName(), "Updating device: " + device);
                        ftpClient.uploadFile(getContext(), device.getIp(), device.getChipId(), device.getSecret());
                        break;
                    case AppConstants.SOCKET_TIME_OUT:
                        Toast.makeText(getActivity(), "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                        break;
                    case AppConstants.ERROR:
                        Toast.makeText(getActivity(), "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                        break;
                        default:
                            Toast.makeText(getActivity(), "پاسخ نامرتبط دریافت شد", Toast.LENGTH_SHORT).show();
                }
                yesNoDialog.dismiss();
            }
        });
    }

    @Override
    public void onNoClicked(YesNoDialog yesNoDialog, Bundle data) {
        yesNoDialog.dismiss();
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
//        if (topicStatus.equals(TopicStatus.CHANGING)){
//            setTopicIcon.setVisibility(View.INVISIBLE);
//            setTopicProgressBar.setVisibility(View.VISIBLE);
//            setTopic.setEnabled(false);
//        } else{
//            setTopic.setEnabled(true);
//            setTopicProgressBar.setVisibility(View.INVISIBLE);
//            setTopicIcon.setVisibility(View.VISIBLE);
//        }
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else
            ChangeDeviceAccessPointFragment.newInstance(device.getSsid()).show(getActivity().getSupportFragmentManager(), "dialog");
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
