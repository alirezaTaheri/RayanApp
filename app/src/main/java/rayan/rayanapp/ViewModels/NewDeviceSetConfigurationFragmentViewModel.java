package rayan.rayanapp.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDeviceSetConfigurationFragmentViewModel extends NewDevicesListViewModel {

    private final String TAG = NewDeviceSetConfigurationFragmentViewModel.class.getSimpleName();
    public NewDeviceSetConfigurationFragmentViewModel(@NonNull Application application) {
        super(application);
    }


//    public LiveData<DeviceBaseResponse> toDeviceFirstConfig(SetPrimaryConfigRequest setPrimaryConfigRequest){
//        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
//        toDeviceFirstConfigObservable(setPrimaryConfigRequest, AppConstants.NEW_DEVICE_IP).subscribe(toDeviceFirstConfigObserver(results));
//        return results;
//    }
    private Observable<SetPrimaryConfigResponse> toDeviceFirstConfigObservable(SetPrimaryConfigRequest setPrimaryConfigRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfig(getDeviceAddress(ip), setPrimaryConfigRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
/*
private DisposableObserver<DeviceBaseResponse> toDeviceFirstConfigObserver(MutableLiveData<DeviceBaseResponse> results){
return new DisposableObserver<DeviceBaseResponse>() {

@Override
public void onNext(@NonNull DeviceBaseResponse baseResponse) {
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
private Observable<SetPrimaryConfigResponse> registerDeviceSendToDeviceObservable(SetPrimaryConfigRequest baseRequest, String ip){
ApiService apiService = ApiUtils.getApiService();
return apiService
.ITET(getDeviceAddress(ip), baseRequest)
.subscribeOn(Schedulers.io())
.observeOn(AndroidSchedulers.mainThread());
}
private DisposableObserver<DeviceBaseResponse> registerDeviceSendToDeviceObserver(MutableLiveData<DeviceBaseResponse> results){
return new DisposableObserver<DeviceBaseResponse>() {

@Override
public void onNext(@NonNull DeviceBaseResponse baseResponse) {
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
*/

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
            return Observable.create(subscriber -> subscriber.onNext(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()) );
        WifiHandler.connectToSSID(activity.getApplicationContext(),((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), AppConstants.DEVICE_PRIMARY_PASSWORD);
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();
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
        ApiUtils.getApiService().getVersion(getDeviceAddress(device.getIp()), new BaseRequest(AppConstants.GET_VERSION))
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

    Disposable disposable;
    Disposable setConfigDeviceDisposable;

    public Disposable getConfigDeviceDisposable(){
        return setConfigDeviceDisposable;
    }
    public SingleLiveEvent<SetPrimaryConfigResponse> registerDeviceSendToDevice(WifiManager wifiManager,AddNewDeviceActivity activity, RegisterDeviceRequest registerDeviceRequest, String ip){
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .registerDevice(RayanApplication.getPref().getToken(),registerDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse ->{
                    Log.e(TAG, "Device Registration Passed\nAdding Device To Group...");
                    Log.e(TAG, "deviceResponse: " + deviceResponse.getData().getMessage());
                        Device d = deviceResponse.getData().getDevice();
                        activity.getNewDevice().setId(d.getId());
                        activity.getNewDevice().setUsername(d.getUsername());
                        activity.getNewDevice().setPassword(d.getPassword());
                        return addDeviceToGroupObservable(new AddDeviceToGroupRequest(activity.getNewDevice().getId(), activity.getNewDevice().getGroup().getId()));
                })
                .flatMap(baseResponse -> editDeviceObservable(new EditDeviceRequest(activity.getNewDevice().getId(), activity.getNewDevice().getGroup().getId(), activity.getNewDevice().getName(), activity.getNewDevice().getType(), activity.getNewDevice().getSsid())))
                .flatMap(deviceResponse -> createTopicObservable(new CreateTopicRequest(activity.getNewDevice().getId(), activity.getNewDevice().getGroup().getId(), activity.getNewDevice().getChip_id(), AppConstants.MQTT_HOST)))
                .flatMap(deviceBaseResponse ->{
                    Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
                    activity.getNewDevice().setTopic(deviceBaseResponse.getData().getDevice().getTopic());
                    byte[] data = activity.getNewDevice().getName().getBytes();
                    String baseName = Base64.encodeToString(data, Base64.DEFAULT);
                    activity.getNewDevice().setName(baseName);
                    String secret;
                    if (deviceDatabase.getDevice(activity.getNewDevice().getChip_id()) != null){
                        secret = deviceDatabase.getDevice(activity.getNewDevice().getChip_id()).getSecret();
                        Log.e(TAG, "A device with this chip id is already saved on device and password will be: " + (activity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret));
                        return connectToDeviceObservable(activity, wifiManager, activity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret);
                    }
                    return connectToDeviceObservable(activity, wifiManager, AppConstants.DEVICE_PRIMARY_PASSWORD);
                })
                .flatMap(deviceResponse ->
                    toDeviceFirstConfigObservable(new SetPrimaryConfigRequest(activity.getNewDevice().getSsid(),activity.getNewDevice().getPwd(), activity.getNewDevice().getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT), activity.getNewDevice().getTopic().getTopic(), activity.getNewDevice().getUsername(), activity.getNewDevice().getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, activity.getNewDevice().getGroup().getSecret()), ip))
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
                        Log.e(TAG, "onError::::" + e);
                        if (e.toString().contains("Unauthorized"))
                            login();
                        else{
                        SetPrimaryConfigResponse errorResponse = new SetPrimaryConfigResponse();
                        errorResponse.setCmd(AppConstants.SOCKET_TIME_OUT);
                        result.postValue(errorResponse);
                        }
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::" );
                    }
                });
        return result;

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
