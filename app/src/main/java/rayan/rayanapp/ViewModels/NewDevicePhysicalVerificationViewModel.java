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
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.PlugPhysicalVerificationRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;

public class NewDevicePhysicalVerificationViewModel extends NewDeviceSetConfigurationFragmentViewModel {
    private final String TAG = NewDevicePhysicalVerificationViewModel.class.getSimpleName();
    public NewDevicePhysicalVerificationViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<DeviceBaseResponse> toDeviceITET(){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceITETObservable(new BaseRequest(AppConstants.NEW_DEVICE_ITET), AppConstants.NEW_DEVICE_IP).subscribe(toDeviceITETObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceITETObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .ITET(AppConstants.getDeviceAddress(ip), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceITETObserver(MutableLiveData<DeviceBaseResponse> results){
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

    public LiveData<DeviceBaseResponse> toDeviceStatus(String status){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceStatusObservable(new PlugPhysicalVerificationRequest(status), AppConstants.NEW_DEVICE_IP).subscribe(toDeviceStatusObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceStatusObservable(PlugPhysicalVerificationRequest request, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .plugStatusVerification(AppConstants.getDeviceAddress(ip), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceStatusObserver(MutableLiveData<DeviceBaseResponse> results){
        return new DisposableObserver<DeviceBaseResponse>() {

            @Override
            public void onNext(@NonNull DeviceBaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                results.postValue(new DeviceBaseResponse(AppConstants.SOCKET_TIME_OUT));
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


}
