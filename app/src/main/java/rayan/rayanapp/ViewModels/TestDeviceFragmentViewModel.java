package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
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
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Util.AppConstants;

public class TestDeviceFragmentViewModel extends AndroidViewModel {
    private final String TAG = TestDeviceFragmentViewModel.class.getSimpleName();
    public TestDeviceFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ToggleDeviceResponse> toDeviceToggle(String command){
        final MutableLiveData<ToggleDeviceResponse> results = new MutableLiveData<>();
        toDeviceToggleObservable(new BaseRequest(command), AppConstants.NEW_DEVICE_IP).subscribe(toDeviceToggleObserver(results));
        return results;
    }
    private Observable<ToggleDeviceResponse> toDeviceToggleObservable(BaseRequest baseRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .toggle(AppConstants.getDeviceAddress(ip, AppConstants.NEW_DEVICE_TOGGLE_CMD), baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<ToggleDeviceResponse> toDeviceToggleObserver(MutableLiveData<ToggleDeviceResponse> results){
        return new DisposableObserver<ToggleDeviceResponse>() {

            @Override
            public void onNext(@NonNull ToggleDeviceResponse baseResponse) {
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
}
