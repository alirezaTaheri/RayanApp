package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
import io.reactivex.internal.operators.observable.ObservableAll;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remote.MainData;
import rayan.rayanapp.Retrofit.remote.RemoteBrandsResponse;
import rayan.rayanapp.Retrofit.remote.RemoteDataResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteDatasResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.LiveResponse;
import rayan.rayanapp.Util.RemoteHub.Communicate.RemoteHubSendData;
import rayan.rayanapp.Util.RemoteHub.Communicate.RemoteHubSendMessage;
import rayan.rayanapp.Util.SingleLiveEvent;

public class AddNewRemoteViewModel extends EditDeviceFragmentViewModel {
    private final String[] types = {"TV","AC"};
    private final String[] tempBrands = {"Aux","Samsung","LG", "Toshiba"};
    private ApiService apiService;
    private Map<String, Integer> urlParams;
    private final String TAG = "AddNewRemoteViewModel";
    private final String URL_SKIP = "skip";
    private final String URL_LIMIT = "limit";
    private final static String URL_REMOTE_DATA = "REMOTE_Data";
    private final static String URL_REMOTE_BRANDS = "REMOTE_Brands";
    public AddNewRemoteViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiUtils.getApiService();
        urlParams = new HashMap<>();
    }
    public List<String> getAllTypes(){
        return new ArrayList<>(Arrays.asList(this.types));
    }
    public List<String> getAllBrands(Map<String, String> params){
//        urlParams.put(URL_REMOTE_BRANDS + URL_LIMIT, 150);
//        urlParams.put(URL_REMOTE_BRANDS + URL_SKIP, 0);
//        SingleLiveEvent<List<String>> callback = new SingleLiveEvent<>();
//        List<String> brands = new ArrayList<>();
//        getAllBrandsObservable(params, brands).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<RemoteBrandsResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(RemoteBrandsResponse remoteBrandsResponse) {
//                Log.d(TAG, "onNext() called with: remoteBrandsResponse = [" + remoteBrandsResponse + "]");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d(TAG, "onError() called with: e = [" + e + "]");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete() called");
//                callback.setValue(brands);
//            }
//        });
        return new ArrayList<>(Arrays.asList(this.tempBrands));
    }
    private Observable<RemoteBrandsResponse> getAllBrandsObservable(Map<String, String> params, List<String> brands){
        return Observable.interval(0, 300, TimeUnit.MILLISECONDS).flatMap(integer -> {
            params.put(URL_LIMIT, String.valueOf(urlParams.get(URL_REMOTE_BRANDS + URL_LIMIT)));
            params.put(URL_SKIP, String.valueOf(urlParams.get(URL_REMOTE_BRANDS + URL_SKIP)));
            return apiService.getAllRemoteBrands(RayanApplication.getPref().getToken(), params);
        }).takeWhile(remoteBrandsResponse -> {
            Log.e(TAG,  "REsponse: " + remoteBrandsResponse);
            if (remoteBrandsResponse != null)
                for (String a: remoteBrandsResponse.getData().getItems())
                    if (!brands.contains(a))
                        brands.add(a);
            Log.e("_)_)_)_)_", ""+(urlParams.get(URL_REMOTE_BRANDS+URL_LIMIT) +
                    urlParams.get(URL_REMOTE_BRANDS+URL_SKIP)));
            if (remoteBrandsResponse.getData().getCount() > urlParams.get(URL_REMOTE_BRANDS+URL_LIMIT) +
                    urlParams.get(URL_REMOTE_BRANDS+URL_SKIP)){
                urlParams.put(URL_REMOTE_BRANDS+URL_SKIP, urlParams.get(URL_REMOTE_BRANDS+URL_SKIP)+ urlParams.get(URL_REMOTE_BRANDS+URL_LIMIT));
                Log.e("TTTTTT", "TTTTTT");
                return true;
            }
            Log.e("FFFFFFFFF", "FFFFFFFFFFF");
            return false;
        });

    }
    private Disposable addRemoteDisposable;

    public Disposable getAddRemoteDisposable() {
        return addRemoteDisposable;
    }

    List<String> added_data;
    public SingleLiveEvent<String> addRemoteDataCreateRemote(List<Button> buttons, Remote remote) {
        SingleLiveEvent<String> callback = new SingleLiveEvent<>();
        added_data = new ArrayList<>();
        Observable.concat(Observable.fromIterable(buttons).flatMap(b -> addRemoteDataObservable(b)
                .doOnNext(response -> {
                    Log.d(TAG, "onNext" + response);
                    added_data.add(response.getData().getRemoteData().getId());
                    callback.setValue(response.getData().getRemoteData().getId());
                })).doOnComplete(() -> remote.setRemoteDatas(added_data))
                ,Observable.just(remote).flatMap(r -> {
                    Log.e(TAG, "Creating Remote: " + remote);
                    return addRemoteObservable(new AddRemoteRequest(remote));
                }))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Object response) {
                        Log.d(TAG, "onNext" + response);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        callback.setValue(handleError(e));
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                        callback.setValue(AppConstants.SUCCESSFUL);
                    }
                });
        return callback;
    }
    public SingleLiveEvent<Pair<String,String>> addEditRemoteDataEditRemote(List<Button> buttons, Remote remote, List<RemoteData> lastRemoteData) {
        SingleLiveEvent<Pair<String,String>> callback = new SingleLiveEvent<>();
        added_data = new ArrayList<>();
        List<Observable<RemoteDataResponse>> buttonsObservables = new ArrayList<>();
        List<Observable<RemoteDataResponse>> editButtonsObservables = new ArrayList<>();
        boolean exist = false;
        for (Button b: buttons) {
            exist = false;
            for (RemoteData r : lastRemoteData)
                if (r.getButton().equals(b.getName())) {
                    exist = true;
                    editButtonsObservables.add(editRemoteDataObservable(new MainData(r)));
                    break;
                }
            if (!exist)
                buttonsObservables.add(addRemoteDataObservable(b));
        }
        Log.e(TAG, "We Have "+buttonsObservables.size()+" to Add");
        Log.e(TAG, "We Have "+editButtonsObservables.size()+" to Edit");
        Log.e(TAG, "Remote is going to edit: " + remote);
        Observable.concat(Observable.concat(buttonsObservables)
                .doOnNext(response -> {
                    Log.d(TAG, "onNext" + response);
                    added_data.add(response.getData().getRemoteData().getId());
                    callback.setValue(new Pair<>(response.getData().getRemoteData().getButton(), response.getData().getRemoteData().getId()));
                }).doOnComplete(() -> {
                    Log.e(TAG, "ONCOMPLETE ADDING DATA: " + added_data);
                }),Observable.concat(editButtonsObservables)
                .doOnNext(response -> {
                    Log.d(TAG, "onNext" + response);
                    added_data.add(response.getData().getRemoteData().getId());
                    callback.setValue(new Pair<>(response.getData().getRemoteData().getButton(), response.getData().getRemoteData().getId()));
                }).doOnComplete(() -> {
                    Log.e(TAG, "ONCOMPLETE ADDING DATA: " + added_data);
                    remote.setRemoteDatas(added_data);
                    Log.e(TAG, "Final Remote For editing:"+remote);
                })
                ,Observable.just(remote).flatMap(r -> editRemoteObservable(new EditRemoteRequest(remote))))
                .subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe() called with: d = [" + d + "]");
            }

            @Override
            public void onNext(Object response) {
                Log.d(TAG, "onNext() called with: response = [" + response + "]");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                callback.setValue(new Pair<>(handleError(e), null));
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() called");
                callback.setValue(new Pair<>(AppConstants.SUCCESSFUL, null));
            }
        });
        return callback;
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
                        callback.setValue(handleError(e));
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
    private String handleError(Throwable e){
        if (e instanceof SocketTimeoutException) return AppConstants.SOCKET_TIME_OUT;
        else if (e instanceof UnknownHostException) return AppConstants.UNKNOWN_HOST_EXCEPTION;
        else return AppConstants.UNKNOWN_EXCEPTION;
    }
    private Observable<RemoteDataResponse> addRemoteDataObservable(Button button){
        Log.e(TAG, "addRemoteDataObservable() called with: button = [" + button + "]");
        return apiService
                .addRemoteData(RayanApplication.getPref().getToken(), new MainData(button))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<RemoteDataResponse> editRemoteDataObservable(MainData remotedata){
        Log.e(TAG, "addRemoteDataObservable() called with: button = [" + remotedata + "]");
        return apiService
                .editRemoteData(RayanApplication.getPref().getToken(), remotedata)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<RemoteHubsResponse> addRemoteObservable(AddRemoteRequest addRemoteRequest){
        Log.d(TAG, "addRemoteObservable() called with: addRemoteRequest = [" + addRemoteRequest + "]");
        return apiService
                .addRemote(RayanApplication.getPref().getToken(), addRemoteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public MutableLiveData<Pair<List<RemoteData>, String>> getRemoteData(Map<String, String> param) {
        urlParams.put(URL_REMOTE_DATA + URL_LIMIT, 500);
        urlParams.put(URL_REMOTE_DATA + URL_SKIP, 0);
        List<RemoteData> remoteData = new ArrayList<>();
        MutableLiveData<Pair<List<RemoteData>, String>> callback = new MutableLiveData<>();
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
                        if (e instanceof SocketTimeoutException) callback.postValue(new Pair<>(null,AppConstants.SOCKET_TIME_OUT));
                        else if (e instanceof ConnectException) callback.postValue(new Pair<>(null,AppConstants.CONNECT_EXCEPTION));
                        else if (e instanceof UnknownHostException) callback.postValue(new Pair<>(null,AppConstants.UNKNOWN_HOST_EXCEPTION));
                        else callback.postValue(new Pair<>(null,AppConstants.UNKNOWN_EXCEPTION));
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                        callback.postValue(new Pair<>(remoteData, AppConstants.SUCCESSFUL));
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
                        sendingParams.put(URL_LIMIT, String.valueOf(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT))));
                        sendingParams.put(URL_SKIP, String.valueOf(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP))));
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
                        if (Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP))+
                                Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT))< remotesResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE_DATA +URL_SKIP,Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE_DATA + URL_SKIP)) + Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)));
                            return true;
                        }
                        return false;
                    }
                });
    }

    public SingleLiveEvent<LiveResponse> enter_learn(RemoteHub remoteHub){
        RemoteHubSendMessage remoteHubSendMessage = new RemoteHubSendMessage();
        return remoteHubSendMessage.try_enter_learn_get_IR(remoteHub);
    }

    public SingleLiveEvent<String> send_signal(RemoteData remoteData, RemoteHub remoteHub){
        RemoteHubSendData sendData = new RemoteHubSendData();
        return sendData.send_data(remoteHub, remoteData.getMainFrame());
    }

    public SingleLiveEvent<String> get_signal(RemoteHub remoteHub){
        RemoteHubSendMessage remoteHubSendMessage = new RemoteHubSendMessage();
        /* All DONE ================================== Don't worry
        // RemoteHub decrypt_setStatusWord
        // AppconStants getdeviceaddress
        // RemoteHubSendMessage >>!<<sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))
        // RemoteHubSendData >>!<<sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))
        // And Here status word 1
        // And Up There enter_learn status word
        // RemoteFragment OnCreate RemoteHub set status word
        =======================================================*/
        return remoteHubSendMessage.send_request(remoteHub, AppConstants.REMOTE_HUB_GET_IR_SIGNAL);
    }

    public SingleLiveEvent<String> exit_learn(RemoteHub remoteHub){
        RemoteHubSendMessage remoteHubSendMessage = new RemoteHubSendMessage();
        return remoteHubSendMessage.send_request(remoteHub, AppConstants.REMOTE_HUB_EXIT_LEARN);
    }
}
