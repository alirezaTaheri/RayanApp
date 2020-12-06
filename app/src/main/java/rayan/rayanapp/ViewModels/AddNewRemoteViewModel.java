package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteDatasResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubsResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;

public class AddNewRemoteViewModel extends DevicesFragmentViewModel {
    private final String[] types = {"TV","AC","Learn"};
    private final String[] brands = {"Aux","Samsung","LG", "Toshiba"};
    private ApiService apiService;
    private Map<String, String> urlParams;
    private final String TAG = "AddNewRemoteViewModel";
    private final String URL_SKIP = "skip";
    private final String URL_LIMIT = "limit";
    private final static String URL_REMOTE_DATA = "REMOTE_Data";
    public AddNewRemoteViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiUtils.getApiService();
        urlParams = new HashMap<>();
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

    public MutableLiveData<List<RemoteData>> getRemoteData(Map<String, String> param) {
        urlParams.put(URL_REMOTE_DATA + URL_LIMIT, "20");
        urlParams.put(URL_REMOTE_DATA + URL_SKIP, "0");
        List<RemoteData> remoteData = new ArrayList<>();
        MutableLiveData<List<RemoteData>> callback = new MutableLiveData<>();
        getRemoteDataObservable(remoteData, param).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Observer<RemoteDatasResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(RemoteDatasResponse remoteDatasResponse) {
                        Log.d(TAG, "onNext() called with: remoteDatasResponse = [" + remoteDatasResponse + "]");
                        Log.e(TAG, "onNext() called with: remoteDatasResponse = [" + remoteData + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                        callback.postValue(remoteData);
                    }
                });
                return callback;
    }

    private Observable<RemoteDatasResponse> getRemoteDataObservable(List<RemoteData> newRemoteData, Map<String,String> params){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<RemoteDatasResponse>>() {
                    @Override
                    public Observable<RemoteDatasResponse> apply(Long aLong) throws Exception {
                        Log.e(TAG,"Sending: Limit: " + urlParams.get(URL_REMOTE_DATA + URL_LIMIT) + " Skip: " + urlParams.get(URL_REMOTE_DATA + URL_SKIP));
                        Map<String , String> sendingParams = params;
                        sendingParams.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)));
                        sendingParams.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP)));
                        return apiService.getRemoteDatasv3(RayanApplication.getPref().getToken(), sendingParams);
                    }
                })
                .takeWhile(new Predicate<RemoteDatasResponse>() {
                    @Override
                    public boolean test(RemoteDatasResponse remotesResponse) throws Exception {
                        Log.e(TAG, "Limit: "+urlParams.get(URL_REMOTE_DATA + URL_LIMIT)+" / Skip: " + urlParams.get(URL_REMOTE_DATA + URL_SKIP));
                        List<RemoteData> results = remotesResponse.getData().getRemoteDatas();
                        if (results != null)
                            newRemoteData.addAll(results);
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP)))+
                                Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)))< remotesResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE_DATA +URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE_DATA + URL_SKIP))) + Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)))));
                            return true;
                        }
                        return false;
                    }
                });
    }


}
