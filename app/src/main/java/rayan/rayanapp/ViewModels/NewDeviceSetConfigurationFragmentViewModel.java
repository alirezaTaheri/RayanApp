package rayan.rayanapp.ViewModels;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NetworkConnection;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDeviceSetConfigurationFragmentViewModel extends NewDevicesListViewModel {

    private final String TAG = NewDeviceSetConfigurationFragmentViewModel.class.getSimpleName();
    public NewDeviceSetConfigurationFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<DeviceBaseResponse> toDeviceFirstConfig(SetPrimaryConfigRequest setPrimaryConfigRequest){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceFirstConfigObservable(setPrimaryConfigRequest, AppConstants.NEW_DEVICE_IP).subscribe(toDeviceFirstConfigObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceFirstConfigObservable(SetPrimaryConfigRequest setPrimaryConfigRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .ITET(getDeviceAddress(ip), setPrimaryConfigRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
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
    private Observable<DeviceBaseResponse> registerDeviceSendToDeviceObservable(SetPrimaryConfigRequest baseRequest, String ip){
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
    private Observable<String> connectToDeviceObservable(Activity activity){
        WifiHandler.connectToSSID(activity.getApplicationContext(),"Rayan_switch_1_132465", "12345678");
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();

    }
    public MutableLiveData<String> registerDeviceSendToDevice(AddNewDeviceActivity activity,RegisterDeviceRequest registerDeviceRequest,CreateTopicRequest createTopicRequest,SetPrimaryConfigRequest setPrimaryConfigRequest , String ip){
        MutableLiveData<String> result = new MutableLiveData<>();
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .registerDevice(RayanApplication.getPref().getToken(),registerDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse ->{
                    Device d = deviceResponse.getData().getDevice();
                    activity.getNewDevice().setId(d.getId());
                    activity.getNewDevice().setUsername(d.getUsername());
                    activity.getNewDevice().setPassword(d.getPassword());
                    return addDeviceToGroupObservable(new AddDeviceToGroupRequest(activity.getNewDevice().getId(),activity.getNewDevice().getGroupId()));
                })
                .flatMap(deviceResponse -> createTopicObservable(new CreateTopicRequest(activity.getNewDevice().getId(), activity.getNewDevice().getGroupId(), activity.getNewDevice().getChip_id(), AppConstants.MQTT_HOST)))
                .flatMap(deviceBaseResponse ->{
                    activity.getNewDevice().setTopic(deviceBaseResponse.getData().getDevice().getTopic());
                    return connectToDeviceObservable(activity);
                } )
                .flatMap(deviceResponse ->
                    toDeviceFirstConfigObservable(new SetPrimaryConfigRequest(activity.getNewDevice().getSsid(),activity.getNewDevice().getPwd(), activity.getNewDevice().getName(), AppConstants.MQTT_HOST, AppConstants.MQTT_PORT, activity.getNewDevice().getTopic().getTopic(), activity.getNewDevice().getUsername(), activity.getNewDevice().getPassword(), activity.getNewDevice().getHpwd()), ip))
                .subscribe(new Observer<DeviceBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceBaseResponse deviceBaseResponse) {
                        Log.e(TAG, "OnNext::::" + deviceBaseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError::::" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::" );
                    }
                });
        return result;

    }

    public String getDeviceAddress(String ip){
//        return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
        return "http://192.168.1.102/test.php";
    }
}
