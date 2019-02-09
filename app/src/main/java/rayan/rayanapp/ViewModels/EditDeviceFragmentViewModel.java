package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.CreateTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Responses.DeviceResponse;

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

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }
}

