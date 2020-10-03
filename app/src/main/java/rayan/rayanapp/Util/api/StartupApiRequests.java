package rayan.rayanapp.Util.api;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
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
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.RemoteDataDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Persistance.database.UserDatabase;
import rayan.rayanapp.Persistance.database.UserMembershipDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.GroupsResponsev3;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteDatasResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemotesResponse;
import rayan.rayanapp.Util.ApiResponseHandler;
import retrofit2.HttpException;

public class StartupApiRequests {

    public final static String URL_REMOTE_HUB = "REMOTE_HUB";
    public final static String URL_REMOTE_DATA = "REMOTE_Data";
    public final static String URL_GROUP = "GROUP";
    public final static String URL_SKIP = "skip";
    public final static String URL_LIMIT = "limit";
    private String TAG = this.getClass().getName();
    private DeviceDatabase deviceDatabase;
    private GroupDatabase groupDatabase;
    private UserDatabase userDatabase;
    private RemoteHubDatabase remoteHubDatabase;
    private RemoteDatabase remoteDatabase;
    private RemoteDataDatabase remoteDataDatabase;
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
                              RemoteHubDatabase remoteHubDatabase, RemoteDatabase remoteDatabase,RemoteDataDatabase remoteDataDatabase, RayanApplication rayanApplication) {
        this.deviceDatabase = deviceDatabase;
        this.groupDatabase = groupDatabase;
        this.userDatabase = userDatabase;
        this.membershipDatabase = membershipDatabase;
        this.remoteHubDatabase = remoteHubDatabase;
        this.rayanApplication = rayanApplication;
        this.remoteDatabase = remoteDatabase;
        this.remoteDataDatabase = remoteDataDatabase;
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
    List<RemoteData> newRemoteDatas;
//    @SuppressLint("CheckResult")
//    public MutableLiveData<requestStatus> getGroupsV3() {
//        urlParams.put(URL_REMOTE_HUB+URL_LIMIT,"20");
//        urlParams.put(URL_REMOTE_HUB+URL_SKIP,"0");
//        urlParams.put(URL_REMOTE_DATA+URL_LIMIT,"20");
//        urlParams.put(URL_REMOTE_DATA+URL_SKIP,"0");
//        urlParams.put(URL_GROUP+URL_LIMIT,"20");
//        urlParams.put(URL_GROUP+URL_SKIP,"0");
//        newGroups = new ArrayList<>();
//        newRemoteHubs = new ArrayList<>();
//        newRemotes = new ArrayList<>();
//        MutableLiveData<requestStatus> requestStatusLiveData = new MutableLiveData<>();
//        apiService.getGroups()
//    }

    @SuppressLint("CheckResult")
    public MutableLiveData<requestStatus> getGroups1() {
        urlParams.put(URL_REMOTE_HUB+URL_LIMIT,"20");
        urlParams.put(URL_REMOTE_HUB+URL_SKIP,"0");
        urlParams.put(URL_REMOTE_DATA +URL_LIMIT,"20");
        urlParams.put(URL_REMOTE_DATA +URL_SKIP,"0");
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
                            Log.e(TAG, "RemoteHubs: " + remoteHubsResponse.getData().getRemoteHubs().size());
                            newRemoteHubs.addAll(remoteHubsResponse.getData().getRemoteHubs());
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
                        if (e instanceof HttpException && ((HttpException) e).message().contains("Unauthorized")) {
                            requestStatusLiveData.postValue(requestStatus.AUTHENTICATION_ERROR);
                            loginObservable().subscribe(loginObserver(requestStatusLiveData, doAfterLogin.GET_GROUPS));
                        }
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
    public MutableLiveData<requestStatus> getGroupsv3() {
        urlParams.put(URL_GROUP+URL_LIMIT,"15");
        urlParams.put(URL_GROUP+URL_SKIP,"0");
        urlParams.put(URL_REMOTE_HUB+URL_LIMIT,"15");
        urlParams.put(URL_REMOTE_HUB+URL_SKIP,"0");
        urlParams.put(URL_REMOTE_DATA +URL_LIMIT,"15");
        urlParams.put(URL_REMOTE_DATA +URL_SKIP,"0");
        newGroups = new ArrayList<>();
        newRemoteHubs = new ArrayList<>();
        newRemotes = new ArrayList<>();
        newRemoteDatas = new ArrayList<>();
        MutableLiveData<requestStatus> requestStatusLiveData = new MutableLiveData<>();
        Observable.concat(getGroupObservablev3(newGroups), getRemoteHubsObservablev3(newRemoteHubs, newRemotes), getRemoteDatasObservablev3((newRemoteDatas)))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("v3v3v3v3", "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e("v3v3v3v3", "onNext() called with: o = [" + o + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("v3v3v3v3", "Composed onError: "+ e + newGroups.size()+" / "+newRemoteHubs.size() + " / " + newRemotes + " / "+newRemoteDatas);
                        if (e instanceof HttpException && ((HttpException) e).message().contains("Unauthorized")) {
                            loginObservablev3().subscribe(loginObserver(requestStatusLiveData, doAfterLogin.GET_GROUPS));
                            requestStatusLiveData.postValue(requestStatus.AUTHENTICATION_ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e("v3v3v3v3", "onComplete() called "+newGroups.size()+" / "+newRemoteHubs.size() + " / " + newRemotes.size());
                        new ApiResponseHandler().syncGroupsv3(deviceDatabase, groupDatabase, userDatabase, membershipDatabase,
                                remoteHubDatabase, remoteDatabase, remoteDataDatabase, rayanApplication, newGroups, newRemoteHubs,newRemotes,newRemoteDatas);
                    }
                });
//        getGroupObservable()
//                .flatMap(new Function<GroupsResponse, Observable<Object>>() {
//                    @Override
//                    public Observable<Object> apply(GroupsResponse groupsResponse) throws Exception {
//                        Log.e("v3v3v3v3", "Groups Done");
//                        Log.e("v3v3v3v3", "Fetching RemoteHubs");
//                        newGroups = groupsResponse.getData().getGroups();
//                        return Observable.concat(getRemoteHubsObservable(), getRemotesObservable());
//                    }
//                })
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("MNMNMNMNMNMNMNMN", "onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        if (o instanceof RemoteHubsResponse){
//                            RemoteHubsResponse remoteHubsResponse = (RemoteHubsResponse)o;
//                            Log.e(TAG, "RemoteHubs: " + remoteHubsResponse.getData().getRemotes().size());
//                            newRemoteHubs.addAll(remoteHubsResponse.getData().getRemotes());
//                        }else if (o instanceof RemotesResponse){
//                            RemotesResponse remotesResponse = (RemotesResponse)o;
//                            Log.e(TAG, "Remotes: " + remotesResponse.getData().getRemotes().size());
//                            newRemotes.addAll(remotesResponse.getData().getRemotes());
//                        }
//                        Log.e("MNMNMNMN", "onNext" + o);
//                        Log.e("MNMNMNMN", "onNext" + o.getClass().getName());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (e instanceof HttpException && ((HttpException) e).message().contains("Unauthorized"))
//                            requestStatusLiveData.postValue(requestStatus.AUTHENTICATION_ERROR);
//                            loginObservable().subscribe(loginObserver(requestStatusLiveData, doAfterLogin.GET_GROUPS));
//                        Log.e("MNMNMNMN", "onError" + e +"/"+ e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("MNMNMNMN", "OnComplete");
//                        Log.e("MNMNMNMN", "RemoteHubs" + newRemoteHubs.size());
//                        Log.e("MNMNMNMN", "Remotes" + newRemotes.size());
//                        new ApiResponseHandler().syncGroups(deviceDatabase, groupDatabase, userDatabase, membershipDatabase,
//                                remoteHubDatabase, remoteDatabase, rayanApplication, newGroups, newRemoteHubs,newRemotes);
//                    }
//                });
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
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP)));
                        return apiService.getRemotes(RayanApplication.getPref().getToken(), params);
                    }
                })
                .takeWhile(new Predicate<RemotesResponse>() {
                    @Override
                    public boolean test(RemotesResponse remoteHubsResponse) throws Exception {
                        Log.e("pipipipi", urlParams.get(URL_REMOTE_DATA + URL_LIMIT)+" / " + urlParams.get(URL_REMOTE_DATA + URL_SKIP));
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP))) < remoteHubsResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE_DATA +URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE_DATA + URL_SKIP))) + 20));
                            return true;
                        }
                        return false;
                    }
                });
    }

    private Observable<GroupsResponsev3> getGroupObservablev3(List<Group> newGroups){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<GroupsResponsev3>>() {
                    @Override
                    public Observable<GroupsResponsev3> apply(Long aLong) throws Exception {
                        Log.e("v3v3v3v3","Sending: Limit: " + urlParams.get(URL_GROUP + URL_LIMIT) + " Skip: " + urlParams.get(URL_GROUP + URL_SKIP));
                        Map<String , String> params = new HashMap<>();
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_GROUP + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_GROUP + URL_SKIP)));
                        return apiService.getGroupsv3(RayanApplication.getPref().getToken(), params);
                    }
                }).takeWhile(new Predicate<GroupsResponsev3>() {
                    @Override
                    public boolean test(GroupsResponsev3 groupsResponse) throws Exception {
                        Log.e("v3v3v3v3", "Limit: "+urlParams.get(URL_GROUP + URL_LIMIT)+" / Skip: " + urlParams.get(URL_GROUP + URL_SKIP));
                        List<Group> results = groupsResponse.getData().getGroups();
                        if (results != null)
                            newGroups.addAll(results);
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_GROUP + URL_SKIP)))+
                                Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_GROUP + URL_LIMIT)))< groupsResponse.getData().getCount()) {
                            urlParams.put(URL_GROUP+URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_GROUP + URL_SKIP))) + Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_GROUP + URL_LIMIT)))));
                            return true;
                        }
                        return false;
                    }
                });
    }
    private Observable<RemoteHubsResponse> getRemoteHubsObservablev3(List<RemoteHub> newRemoteHubs, List<Remote> newRemotes){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<RemoteHubsResponse>>() {
                    @Override
                    public Observable<RemoteHubsResponse> apply(Long aLong) throws Exception {
                        Log.e("v3v3v3v3","Sending: Limit: " + urlParams.get(URL_REMOTE_HUB + URL_LIMIT) + " Skip: " + urlParams.get(URL_REMOTE_HUB + URL_SKIP));
                        Map<String , String> params = new HashMap<>();
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_SKIP)));
                        return apiService.getRemoteHubsv3(RayanApplication.getPref().getToken(), params);
                    }
                })
                .takeWhile(new Predicate<RemoteHubsResponse>() {
                    @Override
                    public boolean test(RemoteHubsResponse remoteHubsResponse) throws Exception {
                        Log.e("v3v3v3v3", "Limit: "+urlParams.get(URL_REMOTE_HUB + URL_LIMIT)+" / Skip: " + urlParams.get(URL_REMOTE_HUB + URL_SKIP));
                        List<RemoteHub> results = remoteHubsResponse.getData().getRemoteHubs();
                        Log.e("v3v3v3v3", "Response is:"+results);
                        if (results != null) {
                            newRemoteHubs.addAll(results);
                            for (int i = 0;i<results.size();i++) {
                                RemoteHub remoteHub = results.get(i);
                                List<Remote> remotes = results.get(i).getRemotes();
                                for (int j = 0; j<remotes.size();j++){
                                    remotes.get(j).setRemoteHubId(remoteHub.getId());
                                }
                                newRemotes.addAll(remotes);
                            }
                        }
                        if (Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_SKIP)))+
                                Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_LIMIT)))< remoteHubsResponse.getData().getCount()) {
                            urlParams.put(URL_REMOTE_HUB+URL_SKIP,String.valueOf(Integer.parseInt(Objects.requireNonNull(
                                    urlParams.get(URL_REMOTE_HUB + URL_SKIP))) + Integer.parseInt(Objects.requireNonNull(urlParams.get(URL_REMOTE_HUB + URL_LIMIT)))));
                            return true;
                        }
                        return false;
                    }
                });
    }

    private Observable<RemoteDatasResponse> getRemoteDatasObservablev3(List<RemoteData> newRemoteData){
        return Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .flatMap(new Function<Long, Observable<RemoteDatasResponse>>() {
                    @Override
                    public Observable<RemoteDatasResponse> apply(Long aLong) throws Exception {
                        Log.e("v3v3v3v3","Sending: Limit: " + urlParams.get(URL_REMOTE_DATA + URL_LIMIT) + " Skip: " + urlParams.get(URL_REMOTE_DATA + URL_SKIP));
                        Map<String , String> params = new HashMap<>();
                        params.put(URL_LIMIT, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_LIMIT)));
                        params.put(URL_SKIP, Objects.requireNonNull(urlParams.get(URL_REMOTE_DATA + URL_SKIP)));
                        return apiService.getRemoteDatasv3(RayanApplication.getPref().getToken(), params);
                    }
                })
                .takeWhile(new Predicate<RemoteDatasResponse>() {
                    @Override
                    public boolean test(RemoteDatasResponse remotesResponse) throws Exception {
                        Log.e("v3v3v3v3", "Limit: "+urlParams.get(URL_REMOTE_DATA + URL_LIMIT)+" / Skip: " + urlParams.get(URL_REMOTE_DATA + URL_SKIP));
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
    private Observable<BaseResponse> loginObservablev3(){
        ApiService apiService = ApiUtils.getApiService();
        Log.e("SDFLKJDSLKFJSD", "WWWWWWWWWWWWWWWWWWWW");
        return apiService
                .loginv3(RayanApplication.getPref().getUsername(), RayanApplication.getPref().getPassword())
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
                        getGroupsv3();
                        break;
                        default:Log.e(TAG,"There is no Specified Action After login" + action);
                }
                requestStatusPublishSubject.postValue(requestStatus.AUTHORIZED);
//                getGroups();
            }
        };
    }
}
