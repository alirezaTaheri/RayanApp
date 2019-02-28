package rayan.rayanapp.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
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

    private Observable<String> connectToDeviceObservable(Activity activity, WifiManager wifiManager){
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        if (getCurrentSSID(wifiManager).equals(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()))
            return Observable.create(subscriber -> subscriber.onNext(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()) );
        WifiHandler.connectToSSID(activity.getApplicationContext(),((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), AppConstants.DEVICE_PRIMARY_PASSWORD);
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();
    }

    Disposable disposable;
    public SingleLiveEvent<SetPrimaryConfigResponse> registerDeviceSendToDevice(WifiManager wifiManager,AddNewDeviceActivity activity, RegisterDeviceRequest registerDeviceRequest, String ip){
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .registerDevice(RayanApplication.getPref().getToken(),registerDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse ->{
                    Log.e(TAG, "Device Registration Passed\nAdding Device To Group...");
                    Device d = deviceResponse.getData().getDevice();
                    activity.getNewDevice().setId(d.getId());
                    activity.getNewDevice().setUsername(d.getUsername());
                    activity.getNewDevice().setPassword(d.getPassword());
                    return addDeviceToGroupObservable(new AddDeviceToGroupRequest(activity.getNewDevice().getId(),activity.getNewDevice().getGroup().getId()));
                })
                .flatMap(deviceResponse -> createTopicObservable(new CreateTopicRequest(activity.getNewDevice().getId(), activity.getNewDevice().getGroup().getId(), activity.getNewDevice().getChip_id(), AppConstants.MQTT_HOST)))
                .flatMap(deviceBaseResponse ->{
                    Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
                    activity.getNewDevice().setTopic(deviceBaseResponse.getData().getDevice().getTopic());
                    byte[] data = activity.getNewDevice().getName().getBytes();
                    String baseName = Base64.encodeToString(data, Base64.DEFAULT);
                    activity.getNewDevice().setName(baseName);
                    return connectToDeviceObservable(activity, wifiManager);
                })
                .flatMap(deviceResponse ->
                    toDeviceFirstConfigObservable(new SetPrimaryConfigRequest(activity.getNewDevice().getSsid(),activity.getNewDevice().getPwd(), activity.getNewDevice().getName(), AppConstants.MQTT_HOST, AppConstants.MQTT_PORT, activity.getNewDevice().getTopic().getTopic(), activity.getNewDevice().getUsername(), activity.getNewDevice().getPassword(), activity.getNewDevice().getHpwd(), AppConstants.DEVICE_CONNECTED_STYLE), ip))
                .subscribe(new Observer<SetPrimaryConfigResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
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

    public String getDeviceAddress(String ip){
        return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
//        return "http://192.168.137.1/test.php";
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
