package rayan.rayanapp.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.base.BaseResponse_Device;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Topic;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.RemoteHub.Install.RemoteHubInstaller;
import rayan.rayanapp.Util.SingleLiveEvent;
import rayan.rayanapp.Util.Switch.Install.SwitchInstaller;

public class NewDeviceSetConfigurationFragmentViewModel extends NewDevicesListViewModel {

    private final String TAG = NewDeviceSetConfigurationFragmentViewModel.class.getSimpleName();
    public NewDeviceSetConfigurationFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<SetPrimaryConfigResponse> toDeviceFirstConfig(SetPrimaryConfigRequest setPrimaryConfigRequest){
        final MutableLiveData<SetPrimaryConfigResponse> results = new MutableLiveData<>();
        toDeviceFirstConfigObservable(setPrimaryConfigRequest, AppConstants.NEW_DEVICE_IP).subscribe(toDeviceFirstConfigObserver(results));
        return results;
    }
    private Observable<SetPrimaryConfigResponse> toDeviceFirstConfigObservable(SetPrimaryConfigRequest setPrimaryConfigRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfigV2(AppConstants.getDeviceAddress(ip, AppConstants.PRIMARY_CONFIG), setPrimaryConfigRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<SetPrimaryConfigResponse> toDeviceFirstConfigObserver(MutableLiveData<SetPrimaryConfigResponse> results){
        return new DisposableObserver<SetPrimaryConfigResponse>() {

            @Override
            public void onNext(@NonNull SetPrimaryConfigResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

//    public MutableLiveData<String> registerDeviceSendToDevice(SetPrimaryConfigRequest setPrimaryConfigRequest){
//        MutableLiveData<String> result = new MutableLiveData<>();
//        registerDeviceSendToDeviceObservable(setPrimaryConfigRequest, AppConstants.NEW_DEVICE_IP).subscribe(toDeviceFirstConfigObserver(results));
//        return result;
//    }
//private Observable<SetPrimaryConfigResponse> registerDeviceSendToDeviceObservable(SetPrimaryConfigRequest baseRequest, String ip){
//ApiService apiService = ApiUtils.getApiService();
//return apiService
//.ITET(getDeviceAddress(ip), baseRequest)
//.subscribeOn(Schedulers.io())
//.observeOn(AndroidSchedulers.mainThread());
//}
//private DisposableObserver<DeviceBaseResponse> registerDeviceSendToDeviceObserver(MutableLiveData<DeviceBaseResponse> results){
//return new DisposableObserver<DeviceBaseResponse>() {
//
//@Override
//public void onNext(@NonNull DeviceBaseResponse baseResponse) {
//Log.e(TAG,"OnNext "+baseResponse);
//results.postValue(baseResponse);
//}
//
//@Override
//public void onError(@NonNull Throwable e) {
//Log.d(TAG,"Error"+e);
//e.printStackTrace();
//}
//
//@Override
//public void onComplete() {
//Log.d(TAG,"Completed");
//}
//};
//}

    private Observable<DeviceResponse> createTopicObservable(CreateTopicRequest createTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createTopic(RayanApplication.getPref().getToken(), createTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<BaseResponse> addDeviceToGroupObservable(AddDeviceToGroupRequest addDeviceToGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addDeviceToGroup(RayanApplication.getPref().getToken(), addDeviceToGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<String> connectToDeviceObservable(Activity activity, WifiManager wifiManager, String password){
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        if (getCurrentSSID(wifiManager).equals(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()))
            return Observable.create(subscriber -> subscriber.onNext(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()));
//        WifiHandler.connectToSSID(activity.getApplicationContext(),((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), password);
        WifiUtils.enableLog(true);
        WifiUtils.withContext(activity)
                .connectWith(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), password)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("isSuccessful" , "isisisisi: ");
                        Toast.makeText(activity, "به دستگاه متصل شد", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@androidx.annotation.NonNull ConnectionErrorCode errorCode) {
                        Log.e("NOTsUCCESSFULL" , "Error isisisisi: " + errorCode);
                        ((AddNewDeviceActivity) activity).getNewDevice().setFailed(true);
                        Toast.makeText(activity, "خطا در اتصال به دستگاه", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();
    }

    private Observable<DeviceResponse> editDeviceTopicObservable(EditDeviceTopicRequest editDeviceTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDeviceTopic(RayanApplication.getPref().getToken(), editDeviceTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<DeviceResponse> editDeviceObservable(EditDeviceRequest editDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDevice(RayanApplication.getPref().getToken(), editDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public MutableLiveData<String> getDeviceVersion(Device device){
        MutableLiveData<String> results = new MutableLiveData<>();
        ApiUtils.getApiService().getVersion(AppConstants.getDeviceAddress(device.getIp(), AppConstants.NODE_INFO), new BaseRequest(AppConstants.GET_VERSION))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<DeviceBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Getting device version onSubscribe: ");
                    }

                    @Override
                    public void onNext(DeviceBaseResponse deviceBaseResponse) {
                        Log.e(TAG, "Getting device version on next: " + deviceBaseResponse);
                        results.postValue(deviceBaseResponse.getCmd());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Getting device version onError: " + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Getting device version onComplete: ");
                    }
                });
        return results;
    }

    private Observable<BaseResponse> deleteUserObservable(DeleteDeviceRequest deleteDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteDeviceFromGroup(RayanApplication.getPref().getToken(), deleteDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Disposable disposable;
    private Disposable setConfigDeviceDisposable;
    private Disposable resend_config_disposable;
    public Disposable getConfigDeviceDisposable(){
        return setConfigDeviceDisposable;
    }
    public SingleLiveEvent<SetPrimaryConfigResponse> registerDeviceSendToDevice(WifiManager wifiManager,AddNewDeviceActivity activity, RegisterDeviceRequest registerDeviceRequest, String ip){
//        if (RayanApplication.getPref().getIsNodeSoundOn())
//            WifiHandler.removeNetwork(activity, AddNewDeviceActivity.getNewDevice().getAccessPointName());
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        ApiService apiService = ApiUtils.getApiService();
//        apiService
//                .registerDevice(RayanApplication.getPref().getToken(),registerDeviceRequest)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .flatMap(deviceResponse -> {
//                    Log.e(TAG, "Device Registration Passed\nRemoving Device from previous group..."+deviceResponse);
//                    Device d = deviceResponse.getData().getDevice();
//                    AddNewDeviceActivity.getNewDevice().setId(d.getId());
//                    AddNewDeviceActivity.getNewDevice().setUsername(d.getUsername());
//                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage() != null && deviceResponse.getData().getMessage().equals(AppConstants.DUPLICATE_USER))
//                        AddNewDeviceActivity.getNewDevice().setPassword(d.getDevicePassword());
//                    else
//                        AddNewDeviceActivity.getNewDevice().setPassword(d.getPassword());
//                    Device existingDevice = deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id());
//                    if (existingDevice != null){
//                        Log.e(TAG, "Deleting Device From previous Group: " + existingDevice);
//                        AddNewDeviceActivity.getNewDevice().setPreGroupId(existingDevice.getGroupId());
//
//                        return deleteUserObservable(new DeleteDeviceRequest(existingDevice.getId(), existingDevice.getGroupId()));
//                    }
//                    else {
//                        Log.e(TAG, "There is no such device to delete from group ");
//                        return Observable.just(1);
//                    }
//                })
//                .flatMap(deviceResponse ->{
//                    Log.e(TAG, "Device Registration Passed\nAdding Device To new Group...");
//                    return addDeviceToGroupObservable(new AddDeviceToGroupRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId()));
//                })
//                .flatMap(baseResponse -> editDeviceTopicObservable(new EditDeviceTopicRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId(), AddNewDeviceActivity.getNewDevice().getName(), AddNewDeviceActivity.getNewDevice().getType(), AddNewDeviceActivity.getNewDevice().getSsid())))
//                .flatMap(deviceResponse -> {
//                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage().equals(AppConstants.FORBIDDEN)){
//                        Toast.makeText(activity, "شما قادر به نصب این دستگاه نیستید", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Action is forbidden... Operation will fail");
//                        return null;
//                    }
//                    Log.e(TAG, "Device Specification successfully Changed...");
//                    return createTopicObservable(new CreateTopicRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId(), AddNewDeviceActivity.getNewDevice().getChip_id(), AppConstants.MQTT_HOST));
//                })
//                .flatMap(baseResponse -> editDeviceObservable(new EditDeviceRequest(AddNewDeviceActivity.getNewDevice().getName(),
//                        AddNewDeviceActivity.getNewDevice().getType(),AddNewDeviceActivity.getNewDevice().getSsid(),null,
//                        AddNewDeviceActivity.getNewDevice().getGroup().getId(),AddNewDeviceActivity.getNewDevice().getId(),null, null)))
        Observable.just(1).flatMap(deviceBaseResponse ->{
            Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
//                    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Edited
//                    AddNewDeviceActivity.getNewDevice().setTopic(deviceBaseResponse.getData().getDevice().getTopic());
            AddNewDeviceActivity.getNewDevice().setChip_id("0000");
            AddNewDeviceActivity.getNewDevice().setUsername("username");
            AddNewDeviceActivity.getNewDevice().setPassword("password");
            AddNewDeviceActivity.getNewDevice().setTopic(new Topic("topic___"));

            byte[] data = AddNewDeviceActivity.getNewDevice().getName().getBytes();
            String baseName = Base64.encodeToString(data, Base64.DEFAULT);
            AddNewDeviceActivity.getNewDevice().setName(baseName);
            String secret;
            if (deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id()) != null){
                secret = deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id()).getSecret();
                Log.e(TAG, secret + "A device with this chip id is already saved on device and password will be: " + (AddNewDeviceActivity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret));
                return connectToDeviceObservable(activity, wifiManager, AddNewDeviceActivity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret);
            }
            Log.e(TAG, "this device never registerd in this device before password will be:  " + AppConstants.DEVICE_PRIMARY_PASSWORD);
            return connectToDeviceObservable(activity, wifiManager, AppConstants.DEVICE_PRIMARY_PASSWORD);
        })
                .flatMap(s -> {
                    Log.e(TAG,"Ok Now we will wait for n seconds"+s);
                    return Observable.timer(8,TimeUnit.SECONDS);})
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(deviceResponse -> {
                    Toast.makeText(activity, "ارسال اطلاعات به دستگاه", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ok Response of connection is here: "+deviceResponse);
                    return toDeviceFirstConfigObservable(new SetPrimaryConfigRequest(AddNewDeviceActivity.getNewDevice().getSsid(), AddNewDeviceActivity.getNewDevice().getPwd(), AddNewDeviceActivity.getNewDevice().getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), String.valueOf(AppConstants.MQTT_PORT_TCP), AddNewDeviceActivity.getNewDevice().getTopic().getTopic(), AddNewDeviceActivity.getNewDevice().getUsername(), AddNewDeviceActivity.getNewDevice().getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, AddNewDeviceActivity.getNewDevice().getGroup().getSecret()), ip);
                })
                .subscribe(new Observer<SetPrimaryConfigResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        setConfigDeviceDisposable = d;
                    }

                    @Override
                    public void onNext(SetPrimaryConfigResponse deviceBaseResponse) {
                        Log.e(TAG, "OnNext::::" + deviceBaseResponse);
                        result.postValue(deviceBaseResponse);
                        disposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Flow of install onError::::" + e);
                        SetPrimaryConfigResponse errorResponse = new SetPrimaryConfigResponse();
                        if (e instanceof SocketTimeoutException){
                            errorResponse.setResult(AppConstants.SOCKET_TIME_OUT);
                        }
                        else if (e instanceof UnknownHostException){
                            errorResponse.setResult(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        }
                        else if (e.toString().contains("Unauthorized"))
                            login();
                        else if (e instanceof ConnectException)
                            errorResponse.setResult(AppConstants.CONNECT_EXCEPTION);
                        else{
                            errorResponse.setResult(AppConstants.UNKNOWN_EXCEPTION);
                        }
                        e.printStackTrace();
                        result.postValue(errorResponse);
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::" );
                    }
                });
        return result;

    }

    public SingleLiveEvent<SetPrimaryConfigResponse> sendInfoToDevice(WifiManager wifiManager,AddNewDeviceActivity activity, RegisterDeviceRequest registerDeviceRequest, String ip){
//        if (RayanApplication.getPref().getIsNodeSoundOn())
//            WifiHandler.removeNetwork(activity, AddNewDeviceActivity.getNewDevice().getAccessPointName());
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        Toast.makeText(activity, "ارسال اطلاعات به دستگاه", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Click on Send info to device");
        toDeviceFirstConfigObservable(new SetPrimaryConfigRequest(AddNewDeviceActivity.getNewDevice().getSsid(), AddNewDeviceActivity.getNewDevice().getPwd(), AddNewDeviceActivity.getNewDevice().getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), String.valueOf(AppConstants.MQTT_PORT_TCP), AddNewDeviceActivity.getNewDevice().getTopic().getTopic(), AddNewDeviceActivity.getNewDevice().getUsername(), AddNewDeviceActivity.getNewDevice().getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, AddNewDeviceActivity.getNewDevice().getGroup().getSecret()), ip)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<SetPrimaryConfigResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        setConfigDeviceDisposable = d;
                    }

                    @Override
                    public void onNext(SetPrimaryConfigResponse deviceBaseResponse) {
                        Log.e(TAG, "OnNext::::" + deviceBaseResponse);
                        result.postValue(deviceBaseResponse);
                        disposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Flow of install onError::::" + e);
                        SetPrimaryConfigResponse errorResponse = new SetPrimaryConfigResponse();
                        if (e instanceof SocketTimeoutException){
                            errorResponse.setCmd(AppConstants.SOCKET_TIME_OUT);
                        }
                        else if (e instanceof UnknownHostException){
                            errorResponse.setCmd(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        }
                        else if (e.toString().contains("Unauthorized"))
                            login();
                        else if (e instanceof ConnectException)
                            errorResponse.setCmd(AppConstants.CONNECT_EXCEPTION);
                        else{
                            errorResponse.setCmd(AppConstants.UNKNOWN_EXCEPTION);
                        }
                        result.postValue(errorResponse);
                        disposable.dispose();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::" );
                    }
                });
        return result;

    }

    //    =============================== RemoteHub ==============================
    public SingleLiveEvent<RemoteHubBaseResponse> installRemoteHub(WifiManager wifiManager,
                                                                   AddNewDeviceActivity activity,
                                                                   NewDevice newDevice,
                                                                   String ip){
        RemoteHubInstaller remoteHubInstaller = new RemoteHubInstaller();
        Pair<Disposable,SingleLiveEvent<RemoteHubBaseResponse>> output = remoteHubInstaller.install(remoteHubDatabase,
                wifiManager,
                activity,
                newDevice,
                ip);
        setConfigDeviceDisposable = output.first;
        return output.second;
    }

    public SingleLiveEvent<BaseResponse_Device> installSwitch(WifiManager wifiManager,
                                                                RegisterDeviceRequest registerRequest,
                                                                AddNewDeviceActivity activity,
                                                                String ip){
        SwitchInstaller switchInstaller = new SwitchInstaller();
        Pair<Disposable,SingleLiveEvent<BaseResponse_Device>> output = switchInstaller.install(
                wifiManager,
                activity,
                deviceDatabase,
                registerRequest,
                ip);
        setConfigDeviceDisposable = output.first;
        return output.second;
    }

    public SingleLiveEvent<String> resend_config_to_remoteHub(NewDevice newDevice, String ip){
        RemoteHubInstaller installer = new RemoteHubInstaller();
        Pair<Disposable,SingleLiveEvent<String>> output = installer.resend_config(newDevice, ip);
        resend_config_disposable = output.first;
        return output.second;
    }

    private String getCurrentSSID(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }
}
