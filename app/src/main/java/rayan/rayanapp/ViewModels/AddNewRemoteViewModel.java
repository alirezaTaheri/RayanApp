package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddRemoteRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditRemoteRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;

public class AddNewRemoteViewModel extends DevicesFragmentViewModel {
    private final String[] types = {"TV","AC","Learn"};
    private final String[] brands = {"Aux","Samsung","LG", "Toshiba"};
    private ApiService apiService;
    private final String TAG = "AddNewRemoteViewModel";
    public AddNewRemoteViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiUtils.getApiService();
    }
    public List<String> getAllTypes(){
        return new ArrayList<>(Arrays.asList(this.types));
    }
    public List<String> getAllBrands(){
        return new ArrayList<>(Arrays.asList(this.brands));
    }

    private Disposable addRemoteDisposable;

    public Disposable getAddRemoteDisposable() {
        return addRemoteDisposable;
    }

    public SingleLiveEvent<String> addRemote(Remote remote){
        SingleLiveEvent<String> callback = new SingleLiveEvent<>();
        addRemoteObservable(new AddRemoteRequest(remote)).subscribeOn(Schedulers.io())
                .subscribe(new Observer<RemoteHubsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                        addRemoteDisposable = d;
                    }

                    @Override
                    public void onNext(RemoteHubsResponse remoteHubsResponse) {
                        Log.d(TAG, "onNext() called with: remoteHubsResponse = [" + remoteHubsResponse + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                        callback.postValue(AppConstants.OPERATION_DONE);
                    }
                });
        return callback;
    }

    private Observable<RemoteHubsResponse> addRemoteObservable(AddRemoteRequest addRemoteRequest){
        Log.d(TAG, "addRemoteObservable() called with: addRemoteRequest = [" + addRemoteRequest + "]");
        return apiService
                .addRemote(RayanApplication.getPref().getToken(), addRemoteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
