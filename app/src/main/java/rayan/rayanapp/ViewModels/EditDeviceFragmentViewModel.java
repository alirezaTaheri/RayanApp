package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.MqttTopicRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Util.AppConstants;

public class EditDeviceFragmentViewModel extends DevicesFragmentViewModel {

    private final String TAG = EditDeviceFragmentViewModel.class.getSimpleName();
    public EditDeviceFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<DeviceResponse> editDevice(String id, String name, String type, String groupId){
        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
        editDeviceObservable(new EditDeviceRequest(id, groupId, name, type)).subscribe(editDeviceObserver(results));
        return results;
    }
    private Observable<DeviceResponse> editDeviceObservable(EditDeviceRequest editDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDevice(RayanApplication.getPref().getToken(), editDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceResponse> editDeviceObserver(MutableLiveData<DeviceResponse> results){
        return new DisposableObserver<DeviceResponse>() {

            @Override
            public void onNext(@NonNull DeviceResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<DeviceResponse> createTopic(String id, String code_10, String domain, String groupId){
        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
        createTopicObservable(new CreateTopicRequest(id, groupId, code_10, domain)).subscribe(createTopicObserver(results));
        return results;
    }
    private Observable<DeviceResponse> createTopicObservable(CreateTopicRequest createTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createTopic(RayanApplication.getPref().getToken(), createTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceResponse> createTopicObserver(MutableLiveData<DeviceResponse> results){
        return new DisposableObserver<DeviceResponse>() {

            @Override
            public void onNext(@NonNull DeviceResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public MutableLiveData<String> zipChangeName(String id , String name, String type, String groupId, String ip){
        MutableLiveData<String> response = new MutableLiveData<>();
        byte[] data = name.getBytes();
        String baseName = Base64.encodeToString(data, Base64.DEFAULT);
        Observable.zip(
                toDeviceChangeNameObservable(new ChangeNameRequest(baseName),ip).subscribeOn(Schedulers.io()).doOnNext(changeNameResponse -> {
                    Log.e(TAG, " 00000000 ChangeNameResponse: " + changeNameResponse);
                }),
                editDeviceObservable(new EditDeviceRequest(id, groupId, name, type)).subscribeOn(Schedulers.io()).doOnNext(deviceResponse -> {
                    Log.e(TAG, "000000000 DeviceResponse: " + deviceResponse);
                }),
                (BiFunction<ChangeNameResponse, DeviceResponse, Object>) (changeNameResponse, deviceResponse) -> {
                    Log.e(TAG, "ChangeNameResponse: " + changeNameResponse);
                    Log.e(TAG, "DeviceResponse: " + deviceResponse);
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR)){
                        if (deviceResponse.getData().getMessage() != null)
                        return deviceResponse.getData().getMessage();
                        else return AppConstants.ERROR;
                    }
                    if (deviceResponse.getData().getMessage()!=null && deviceResponse.getData().getMessage().toLowerCase().contains(AppConstants.FORBIDDEN)){
                        return AppConstants.FORBIDDEN;
                    }
                    if (changeNameResponse.getCmd().contains(AppConstants.CHANGE_NAME_FALSE)){
                        return AppConstants.CHANGE_NAME_FALSE;
                    }
                    return AppConstants.OPERATION_DONE;
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
                response.postValue((String)o);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SocketTimeoutException){
                    response.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "ChangeName: onComplete " );
            }
        });
        return response;
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<String> flatMqtt(Device device){
        MutableLiveData<String> result = new MutableLiveData<>();
        createTopicObservable(new CreateTopicRequest(device.getId(), device.getGroupId(),device.getChipId(), AppConstants.MQTT_HOST))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse -> toDeviceMqttObservable(new MqttTopicRequest(AppConstants.MQTT_HOST,
                        deviceResponse.getData().getDevice().getUsername(),
                        ((deviceResponse.getData().getDevice().getPassword() != null) ? deviceResponse.getData().getDevice().getPassword():"Password Not Set"),
                        deviceResponse.getData().getDevice().getTopic().getTopic(),
                        AppConstants.MQTT_PORT
                        ),device.getIp()))
        .subscribe(new Observer<DeviceBaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(DeviceBaseResponse deviceBaseResponse) {
                Log.e(TAG, "onNext"+deviceBaseResponse);
                result.postValue(deviceBaseResponse.getCmd());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError" + e);
                if (e instanceof SocketTimeoutException){
                    result.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete Mqtt Sent To Device");
            }
        });
        return result;
    }

    public LiveData<ChangeNameResponse> toDeviceChangeName(String name, String ip){
        final MutableLiveData<ChangeNameResponse> results = new MutableLiveData<>();
        toDeviceChangeNameObservable(new ChangeNameRequest(name),ip).subscribe(toDeviceChangeNameObserver(results));
        return results;
    }
    private Observable<ChangeNameResponse> toDeviceChangeNameObservable(ChangeNameRequest changeNameRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .changeName(getDeviceAddress(ip), changeNameRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<ChangeNameResponse> toDeviceChangeNameObserver(MutableLiveData<ChangeNameResponse> results){
        return new DisposableObserver<ChangeNameResponse>() {

            @Override
            public void onNext(@NonNull ChangeNameResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceFactoryReset(Device device){
        final MutableLiveData<String> results = new MutableLiveData<>();
        deleteUserObservable(new DeleteUserRequest(device.getId(), device.getGroupId())).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .flatMap(deviceBaseResponse -> {
                    if (deviceBaseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                        return toDeviceFactoryResetObservable(new BaseRequest(AppConstants.FACTORY_RESET), device.getIp());
                    else {
                        results.postValue(AppConstants.ERROR_DESCRIPTION);
                        return null;
                    }
                }).subscribe(toDeviceFactoryResetObserver(results));
//        toDeviceFactoryResetObservable(new BaseRequest(AppConstants.FACTORY_RESET),ip).subscribe(toDeviceFactoryResetObserver(results));
        return results;
    }


    private Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<DeviceBaseResponse> toDeviceFactoryResetObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .factoryReset(getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceFactoryResetObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<String> toDeviceChangeAccessPoint(String hname, String pwd, String ssid, String style, String ip){
        final MutableLiveData<String> results = new MutableLiveData<>();
        toDeviceChangeAccessPointObservable(new ChangeAccessPointRequest(hname, pwd, ssid, style),ip).subscribe(toDeviceChangeAccessPointObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceChangeAccessPointObservable(ChangeAccessPointRequest changeAccessPointRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .changeAccessPoint(getDeviceAddress(ip), changeAccessPointRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceChangeAccessPointObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<DeviceBaseResponse> toDeviceMqtt(String host, String user, String pass, String topic, String port, String ip){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceMqttObservable(new MqttTopicRequest(host,user,pass,topic,port),ip).subscribe(toDeviceMqttObserver(results));
        return results;
    }
    public Observable<DeviceBaseResponse> toDeviceMqttObservable(MqttTopicRequest mqttTopicRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendMqtt(getDeviceAddress(ip), mqttTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceMqttObserver(MutableLiveData<DeviceBaseResponse> results){
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
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<String> toDeviceEndSettings(String ip){
        final MutableLiveData<String> results = new MutableLiveData<>();
        toDeviceEndSettingsObservable(new BaseRequest(AppConstants.END_SETTINGS), ip).subscribe(toDeviceEndSettingsObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceEndSettingsObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .endSettings(getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceEndSettingsObserver(MutableLiveData<String> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getCmd());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                results.postValue(AppConstants.ERROR);
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }

    public String getDeviceAddress(String ip){
        return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
    }
}
