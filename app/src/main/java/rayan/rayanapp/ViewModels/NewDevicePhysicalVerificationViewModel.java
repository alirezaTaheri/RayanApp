package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.PhysicalVerificationRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.PlugPhysicalVerificationRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.DeviceBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.RemoteHub.Install.RemoteHubInstaller;
import rayan.rayanapp.Util.SingleLiveEvent;

public class NewDevicePhysicalVerificationViewModel extends NewDeviceSetConfigurationFragmentViewModel {
    private final String TAG = NewDevicePhysicalVerificationViewModel.class.getSimpleName();
    public NewDevicePhysicalVerificationViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent<String> to_remoteHub_physical_verification(){
        RemoteHubInstaller installer = new RemoteHubInstaller();
        return installer.to_remoteHub_physical_verification();
    }

    public LiveData<DeviceBaseResponse> toDeviceITET(){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceITETObservable(new PhysicalVerificationRequest(null), AppConstants.NEW_DEVICE_IP).subscribe(toDeviceITETObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceITETObservable(PhysicalVerificationRequest request, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .ITET(AppConstants.getDeviceAddress(ip, AppConstants.NEW_DEVICE_PHYSICAL_VERIFICATION), request)
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
                DeviceBaseResponse errorResponse = new DeviceBaseResponse();
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
                else {
                    errorResponse.setCmd(AppConstants.UNKNOWN_EXCEPTION);
                }
                results.postValue(errorResponse);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<DeviceBaseResponse> toDeviceStatus(int status){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceStatusObservable(new PlugPhysicalVerificationRequest(status), AppConstants.NEW_DEVICE_IP).subscribe(toDeviceStatusObserver(results));
        return results;
    }
    private Observable<DeviceBaseResponse> toDeviceStatusObservable(PlugPhysicalVerificationRequest request, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .plugStatusVerification(AppConstants.getDeviceAddress(ip, AppConstants.NEW_DEVICE_PHYSICAL_VERIFICATION), request)
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
