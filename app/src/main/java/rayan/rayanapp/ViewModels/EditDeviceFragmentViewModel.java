package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
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

    public MutableLiveData<String> zip(String id , String name, String type, String groupId){
        MutableLiveData<String> response = new MutableLiveData<>();
        Observable.zip(
                toDeviceChangeNameObservable(new ChangeNameRequest(name)).subscribeOn(Schedulers.io()).doOnNext(changeNameResponse -> {
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

    public LiveData<ChangeNameResponse> toDeviceChangeName(String name){
        final MutableLiveData<ChangeNameResponse> results = new MutableLiveData<>();
        toDeviceChangeNameObservable(new ChangeNameRequest(name)).subscribe(toDeviceChangeNameObserver(results));
        return results;
    }
    private Observable<ChangeNameResponse> toDeviceChangeNameObservable(ChangeNameRequest changeNameRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .changeName("http://192.168.1.102/test.php", changeNameRequest)
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


    public LiveData<String> toDeviceEndSettings(){
        final MutableLiveData<String> results = new MutableLiveData<>();
        toDeviceEndSettingsObservable(new BaseRequest(AppConstants.END_SETTINGS)).subscribe(toDeviceEndSettingsObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceEndSettingsObservable(BaseRequest baseRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .endSettings("http://192.168.1.102/test.php", baseRequest)
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
}

