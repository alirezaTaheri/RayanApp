package rayan.rayanapp.Util.api;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Persistance.database.UserDatabase;
import rayan.rayanapp.Persistance.database.UserMembershipDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemotesResponse;
import rayan.rayanapp.Util.ApiResponseHandler;
import rayan.rayanapp.Util.RxPipeline;
import retrofit2.HttpException;

public class StartupApiRequests {

    public final static String URL_REMOTE_HUB = "REMOTE_HUB";
    public final static String URL_REMOTE = "REMOTE";
    public final static String URL_MAIN_DATA = "MAIN_DATA";
    public final static String URL_SKIP = "skip";
    public final static String URL_LIMIT = "limit";
    private String TAG = this.getClass().getName();
    private DeviceDatabase deviceDatabase;
    private GroupDatabase groupDatabase;
    private UserDatabase userDatabase;
    private RemoteHubDatabase remoteHubDatabase;
    private RemoteDatabase remoteDatabase;
    private UserMembershipDatabase membershipDatabase;
    private ApiService apiService;
    private Map<String, String> urlParams;
    private RayanApplication rayanApplication;
    private enum doAfterLogin{
        GET_GROUPS
    }
    public enum requestStatus{
            SENT,
            ERROR,
            NEXT,
            PROCESSING,
            AUTHORIZED,
            AUTHENTICATION_ERROR,
            DONE
    }
    public StartupApiRequests(ApiService apiService, DeviceDatabase deviceDatabase, GroupDatabase groupDatabase,
                              GroupDatabase groupDatabase1, UserDatabase userDatabase, UserMembershipDatabase membershipDatabase,
                              RemoteHubDatabase remoteHubDatabase, RemoteDatabase remoteDatabase, RayanApplication rayanApplication) {
        this.deviceDatabase = deviceDatabase;
        this.groupDatabase = groupDatabase;
        this.userDatabase = userDatabase;
        this.membershipDatabase = membershipDatabase;
        this.remoteHubDatabase = remoteHubDatabase;
        this.rayanApplication = rayanApplication;
        this.remoteDatabase = remoteDatabase;
        this.apiService = apiService;
        urlParams = new HashMap<>();
    }


    private Observable<GroupsResponse> getGroupObservable(){
        return apiService
                .getGroups(RayanApplication.getPref().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    List<Group> newGroups;
    List<RemoteHub> newRemoteHubs;
    List<Remote> newRemotes;
    @SuppressLint("CheckResult")
    public MutableLiveData<requestStatus> getGroups1() {
        urlParams.put(URL_REMOTE_HUB+URL_LIMIT,"20");
        urlParams.put(URL_REMOTE_HUB+URL_SKIP,"0");
        urlParams.put(URL_REMOTE+URL_LIMIT,"20");
        urlParams.put(URL_REMOTE+URL_SKIP,"0");
        newGroups = new ArrayList<>();
        newRemoteHubs = new ArrayList<>();
        newRemotes = new ArrayList<>();
        MutableLiveData<requestStatus> requestStatusLiveData = new MutableLiveData<>();
        getGroupObservable()
                .flatMap(new Function<GroupsResponse, Observable<Object>>() {
                    @Override
                    public Observable<Object> apply(GroupsResponse groupsResponse) throws Exception {
                        Log.e("LLLLLLLLLL", "Groups Done");
                        Log.e("LLLLLLLLLL", "Fetching RemoteHubs");
                        newGroups = groupsResponse.getData().getGroups();
                        return Observable.concat(getRemoteHubsObservable(), getRemotesObservable());
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("MNMNMNMNMNMNMNMN", "onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof RemoteHubsResponse){
                            RemoteHubsResponse remoteHubsResponse = (RemoteHubsResponse)o;
                            Log.e(TAG, "RemoteHubs: " + remoteHubsResponse.getData().getRemotes().size());
                            newRemoteHubs.addAll(remoteHubsResponse.getData().getRemotes());
                        }else if (o instanceof RemotesResponse){
                            RemotesResponse remotesResponse = (RemotesResponse)o;
                            Log.e(TAG, "Remotes: " + remotesResponse.getData().getRemotes().size());
                            newRemotes.addAll(remotesResponse.getData().getRemotes());
                        }
                        Log.e("MNMNMNMN", "onNext" + o);
                        Log.e("MNMNMNMN", "onNext" + o.getClass().getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException && ((HttpException) e).message().contains("Unauthorized"))
                            requestStatusLiveData.postValue(requestStatus.AUTHENTICATION_ERROR);
                            loginObservable().subscribe(loginObserver(requestStatusLiveData, doAfterLogin.GET_GROUPS));
                        Log.e("MNMNMNMN", "onError" + e +"/"+ e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("MNMNMNMN", "OnComplete");
                        Log.e("MNMNMNMN", "RemoteHubs" + newRemoteHubs.size());
                        Log.e("MNMNMNMN", "Remotes" + newRemotes.size());
                        new ApiResponseHandler().syncGroups(deviceDatabase, groupDatabase, userDatabase, membershipDatabase,
                                remoteHubDatabase, remoteDatabase, rayanApplication, newGroups, newRemoteHubs,newRemotes);
                    }
                });

//                .switchMap(new Function<RemoteHubsResponse, Observable<RemotesResponse>>() {
//                    @Override
//                    public Observable<RemotesResponse> apply(RemoteHubsResponse remoteHubsResponse) throws Exception {
//                        Log.e("LLLLLLLLLL", "RemoteHubs Done");
//                        Log.e("LLLLLLLLLL", "Fetching Remotes");
//                        return getRemotesObservable();
//                    }
//                })
//                .subscribe(getRemoteObserver(requestStatusLiveData));
        return requestStatusLiveData;
    }
    @SuppressLint("CheckResult")
    public MutableLiveData<requestStatus> getGroups(){
        return new MutableLiveData<>();
    }

    private Observer<GroupsResponse> getGroupObserver(PublishSubject<requestStatus> requestStatusLiveData){
        return new Observer<GroupsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                requestStatusLiveData.onNext(requestStatus.SENT);
            }

            @Override
            public void onNext(GroupsResponse groupsResponse) {
                Log.d(TAG, "onNext() called with: groupsResponse = [" + groupsResponse + "]");
            }

            @Override
            public void onError(Throwable e) {
                requestStatusLiveData.onNext(requestStatus.ERROR);
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() called");

                requestStatusLiveData.onNext(requestStatus.NEXT);
//                getRemoteHubsObservable().subscribe(getRemoteHubObserver(requestStatusLiveData));
            }
        };
    }
    
    private Observable<RemoteHubsResponse> getRemoteHubsObservable(){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<RemoteHubsResponse>>() {
                    @Override
                    public Observable<RemoteHubsResponse> apply(Long aLong) throws Exception {
                        Log.e("//////////","Sending: Limit" + URL_LIMIT + " Skip: " + URL_SKIP);
                        Map<String , String> params = new HashMap<>();
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_SKIP)));
                        return apiService.getRemoteHubs(RayanApplication.getPref().getToken(), params);
                    }
                })
                .takeWhile(new Predicate<RemoteHubsResponse>() {
                    @Override
                    public boolean test(RemoteHubsResponse remoteHubsResponse) throws Exception {
                        Log.e("pipipipi", urlParams.get(URL_REMOTE_HUB + URL_LIMIT)+" / " + urlParams.get(URL_REMOTE_HUB + URL_SKIP));
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_SKIP))) < remoteHubsResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE_HUB+URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE_HUB + URL_SKIP))) + 20));
                            return true;
                        }
                        return false;
                    }
                })
                .doOnNext(new Consumer<RemoteHubsResponse>() {
                    @Override
                    public void accept(RemoteHubsResponse remoteHubsResponse) throws Exception {
                        Log.e("MNMNMNMNTAGTAG", "Now I have On Next Man");
                    }
                });
    }

    private Observable<RemotesResponse> getRemotesObservable(){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<RemotesResponse>>() {
                    @Override
                    public Observable<RemotesResponse> apply(Long aLong) throws Exception {
                        Log.e("//////////","Sending: Limit" + URL_LIMIT + " Skip: " + URL_SKIP);
                        Map<String , String> params = new HashMap<>();
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE + URL_SKIP)));
                        return apiService.getRemotes(RayanApplication.getPref().getToken(), params);
                    }
                })
                .takeWhile(new Predicate<RemotesResponse>() {
                    @Override
                    public boolean test(RemotesResponse remoteHubsResponse) throws Exception {
                        Log.e("pipipipi", urlParams.get(URL_REMOTE + URL_LIMIT)+" / " + urlParams.get(URL_REMOTE + URL_SKIP));
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE + URL_SKIP))) < remoteHubsResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE+URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE + URL_SKIP))) + 20));
                            return true;
                        }
                        return false;
                    }
                });
    }

    public Observer<RemotesResponse> getRemoteObserver(MutableLiveData<requestStatus> requestStatusMutableLiveData){
        return new Observer<RemotesResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "Shoroo shod");

            }

            @Override
            public void onNext(RemotesResponse remoteHubsResponse) {
                Log.e(TAG, "badi ro dad"+remoteHubsResponse);

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error dad dada"+e);
                requestStatusMutableLiveData.postValue(requestStatus.ERROR);

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "tamoom shod");
                requestStatusMutableLiveData.postValue(requestStatus.DONE);

            }
        };
    }

    private Observable<BaseResponse> loginObservable(){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .login(RayanApplication.getPref().getUsername(), RayanApplication.getPref().getPassword())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<BaseResponse> loginObserver(MutableLiveData<requestStatus> requestStatusPublishSubject, doAfterLogin action){
        return new DisposableObserver<BaseResponse>() {
            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);
                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed Let's" + action);
                switch (action){
                    case GET_GROUPS:
                        getGroups1();
                        break;
                        default:Log.e(TAG,"There is no Specified Action After login" + action);
                }
                requestStatusPublishSubject.postValue(requestStatus.AUTHORIZED);
//                getGroups();
            }
        };
    }
}
