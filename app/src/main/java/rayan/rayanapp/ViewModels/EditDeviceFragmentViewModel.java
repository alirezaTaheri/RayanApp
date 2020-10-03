package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditRemoteHubRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditRemoteRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.SendFilesToDevicePermitRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.EndSettingsRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.FactoryResetRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.MqttTopicRequest;
//<<<<<<< HEAD
import rayan.rayanapp.Retrofit.Models.Requests.device.Ready4SettingsRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.UpdateRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
//=======
import rayan.rayanapp.Retrofit.Models.Requests.device.UpdateDeviceRequest;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemoteHubsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.RemotesResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.SendFilesToDevicePermitResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.AllFilesListResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeAccessPointResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.EndSettingsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.FactoryResetResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.Ready4SettingsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.UpdateResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.VersionResponse;
import rayan.rayanapp.Util.AppConstants;
import retrofit2.HttpException;
import retrofit2.Response;

public class EditDeviceFragmentViewModel extends DevicesFragmentViewModel {

    private final String TAG = EditDeviceFragmentViewModel.class.getSimpleName();
    ApiService apiService;
    ApiService apiServiceScalar;
    public EditDeviceFragmentViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiUtils.getApiService();
        apiServiceScalar = ApiUtils.getApiServiceScalar();
    }

    private Observable<DeviceResponse> editDeviceObservable(EditDeviceTopicRequest editDeviceTopicRequest){
        Log.e("?><?><?><?><?><", "editing: " + editDeviceTopicRequest);
        return apiService
                .editDeviceTopic(RayanApplication.getPref().getToken(), editDeviceTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<RemoteHubResponse> editRemoteHubObservable(EditRemoteHubRequest editRemoteHubRequest){
        Log.e("?><?><?><?><?><", "editing remoteHub: " + editRemoteHubRequest);
        return apiService
                .editRemoteHub(RayanApplication.getPref().getToken(), editRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<DeviceResponse> editRemoteObservable(EditRemoteRequest editRemoteRequest){
        Log.e("editRemoteObservable", "editing Remote: " + editRemoteRequest);
        return apiService
                .editRemote(RayanApplication.getPref().getToken(), editRemoteRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<RemoteHubsResponse> deleteRemoteObservable(String remoteId, String remoteHubId){
        Log.e("editRemoteObservable", "editing Remote: " + remoteId + "/ RemoteHub: "+ remoteHubId);
        return apiService
                .deleteRemote(RayanApplication.getPref().getToken(), remoteHubId, remoteId)
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

    public MutableLiveData<String> zipChangeName(Device device, String id , String name, String type, String groupId, String ip, String ssid){
        MutableLiveData<String> results = new MutableLiveData<>();
        byte[] data = name.getBytes();
        String baseName = Base64.encodeToString(data, Base64.DEFAULT);
        Observable<Response<String>> deviceChangeNameObservable =
                Observable.just(device)
                        .flatMap(new Function<Device, Observable<Response<String>>>() {
                            @Override
                            public Observable<Response<String>> apply(Device ready4SettingsResponse) throws Exception {
                                return toDeviceChangeNameObservable(new ChangeNameRequest(baseName, Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())),device);
                            }
                        })
                        .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(3500, TimeUnit.MILLISECONDS))
                .takeWhile(response -> {
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                    Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                    Log.e("rererererere", "HMACs Are Equal");
                    ChangeNameResponse changeNameResponse = RayanUtils.convertToObject(ChangeNameResponse.class, response.body());
                    device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeNameResponse.getStword(),device.getSecret()).split("#")[1])+1));
                    device.setHeader(Encryptor.decrypt(changeNameResponse.getStword(),device.getSecret()).split("#")[0]);
                    Log.e("TAGTAGTAG", "Should I go: " + changeNameResponse.getCmd().equals("wrong_stword"));
                    if (changeNameResponse.getCmd().equals("wrong_stword")) {
                        Log.e(TAG, "stword is wrong");
                        return true;
                    }
                    else{
                        Log.e(TAG, "Continue to Rest Of Stream");
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                });
        Observable.concat(deviceChangeNameObservable, editDeviceObservable(new EditDeviceTopicRequest(id, groupId, name, type, ssid)))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG+"?><?><?><?><", "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof DeviceResponse){
                            DeviceResponse deviceResponse = (DeviceResponse) o;
                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.CHANGE_NAME_TRUE);
                            else if (deviceResponse.getData().getMessage() != null)
                                results.postValue(deviceResponse.getData().getMessage());
                            else results.postValue(AppConstants.CHANGE_NAME_FALSE);
                        }
                        Log.d(TAG+"?><?><?><?><", "onNext() called with: o = [" + o + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG+"?><?><?><?><", "onError() called with: e = [" + e + "]");
                        if (e instanceof HttpException)
                            results.postValue(AppConstants.CHANGE_NAME_FALSE);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG+"?><?><?><?><", "onComplete() called");
                    }
                });
        return results;
    }
    public MutableLiveData<String> editRemoteHub(RemoteHub remoteHub){
        MutableLiveData<String> results = new MutableLiveData<>();
        byte[] data = remoteHub.getName().getBytes();
        String baseName = Base64.encodeToString(data, Base64.DEFAULT);
        Observable<Response<String>> remoteHubChangeNameObservable =
                Observable.just(remoteHub)
                        .flatMap(new Function<RemoteHub, Observable<Response<String>>>() {
                            @Override
                            public Observable<Response<String>> apply(RemoteHub remoteHub1) throws Exception {
                                return toRemoteHubChangeNameObservable(new ChangeNameRequest(baseName, Encryptor.encrypt(remoteHub1.getHeader().concat("#").concat(remoteHub1.getStatusWord()).concat("#"), remoteHub1.getSecret())),remoteHub1);
                            }
                        })
                        .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(3500, TimeUnit.MILLISECONDS))
                .takeWhile(response -> {
                    Log.e("rerererererere", "Toggle remoteHub Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + remoteHub.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), remoteHub.getSecret()));
                    if (response.headers().get("auth") != null){
                    Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                    Log.e("rererererere", "HMACs Are Equal");
                    ChangeNameResponse changeNameResponse = RayanUtils.convertToObject(ChangeNameResponse.class, response.body());
                    remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeNameResponse.getStword(),remoteHub.getSecret()).split("#")[1])+1));
                    remoteHub.setHeader(Encryptor.decrypt(changeNameResponse.getStword(),remoteHub.getSecret()).split("#")[0]);
                    Log.e("TAGTAGTAG", "Should I go: " + changeNameResponse.getCmd().equals("wrong_stword"));
                    if (changeNameResponse.getCmd().equals("wrong_stword")) {
                        Log.e(TAG, "stword is wrong");
                        return true;
                    }
                    else{
                        Log.e(TAG, "Continue to Rest Of Stream");
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                });
        Observable.concat(remoteHubChangeNameObservable, editRemoteHubObservable(new EditRemoteHubRequest(remoteHub)))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG+"?><?><?><?><", "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof DeviceResponse){
                            DeviceResponse deviceResponse = (DeviceResponse) o;
                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.OPERATION_DONE);
                            else if (deviceResponse.getData().getMessage() != null)
                                results.postValue(deviceResponse.getData().getMessage());
                            else results.postValue(AppConstants.CHANGE_NAME_FALSE);
                        }
                        Log.d(TAG+"?><?><?><?><", "onNext() called with: o = [" + o + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG+"?><?><?><?><", "onError() called with: e = [" + e + "]");
                        e.printStackTrace();
                        if (e instanceof HttpException)
                            results.postValue(AppConstants.CHANGE_NAME_FALSE);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG+"?><?><?><?><", "onComplete() called");
                    }
                });
        return results;
    }

    public MutableLiveData<String> editRemote(Remote remote){
        MutableLiveData<String> results = new MutableLiveData<>();
        editRemoteObservable(new EditRemoteRequest(remote))
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG+"editRemote", "onSubscribe() called with: d = [" + d + "]");
                    }
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof DeviceResponse){
                            DeviceResponse deviceResponse = (DeviceResponse) o;
                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.OPERATION_DONE);
                            else if (deviceResponse.getData().getMessage() != null)
                                results.postValue(deviceResponse.getData().getMessage());
                            else results.postValue(AppConstants.ERROR);
                        }
                        Log.d(TAG+"?><?><?><?><", "onNext() called with: o = [" + o + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG+"?><?><?><?><", "onError() called with: e = [" + e + "]");
                        if (e instanceof HttpException)
                            results.postValue(AppConstants.NETWORK_ERROR);
                        else if (e instanceof SocketTimeoutException)
                            results.postValue(AppConstants.SOCKET_TIME_OUT);
                        else results.postValue(AppConstants.ERROR);

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG+"?><?><?><?><", "onComplete() called");
                    }
                });

        return results;
    }

    public MutableLiveData<String> deleteRemote(Remote remote){
        MutableLiveData<String> results = new MutableLiveData<>();
        deleteRemoteObservable(remote.getId(), remote.getRemoteHubId())
                .subscribe(new Observer<RemoteHubsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG+"editRemote", "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(RemoteHubsResponse o) {
                        Log.e(TAG+"?><?><?><?><", "onNext() called with: o = [" + o + "]");
                        if (o.getStatus().getCode().startsWith("20"))
                            results.postValue(AppConstants.OPERATION_DONE);
                        else if (o.getStatus().getCode().equals("400"))
                            results.postValue(AppConstants.ERROR);
                        else if (o.getStatus().getCode().equals("401"))
                            results.postValue(AppConstants.AUTHENTICATION_ERROR);
                        else if (o.getStatus().getCode().equals("403"))
                            results.postValue(AppConstants.FORBIDDEN);
                        else if (o.getStatus().getCode().equals("404"))
                            results.postValue(AppConstants.NOT_FOUND);
                        else if (o.getStatus().getCode().equals("422"))
                            results.postValue(AppConstants.MISSING_PARAMS);
                        else if (o.getStatus().getCode().equals("500"))
                            results.postValue(AppConstants.SERVER_ERROR);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG+"?><?><?><?><", "onError() called with: e = [" + e + "]");
                        if (e instanceof HttpException)
                            results.postValue(AppConstants.NETWORK_ERROR);
                        else if (e instanceof SocketTimeoutException)
                            results.postValue(AppConstants.SOCKET_TIME_OUT);
                        else results.postValue(AppConstants.ERROR);

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG+"?><?><?><?><", "onComplete() called");
                    }
                });

        return results;
    }

    private Observable<Response<String>> toDeviceChangeNameObservable(ChangeNameRequest changeNameRequest, Device device) {
        try {
            return apiServiceScalar
                    .changeName(AppConstants.sha1(changeNameRequest.ToString(), device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), changeNameRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Observable<Response<String>> toRemoteHubChangeNameObservable(ChangeNameRequest changeNameRequest, RemoteHub remoteHub) {
        try {
            return apiServiceScalar
                    .changeName(AppConstants.sha1(changeNameRequest.ToString(), remoteHub.getSecret()),AppConstants.getDeviceAddress(remoteHub.getIp()), changeNameRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
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

    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceFactoryReset(Device device){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable<Response<String>> toDeviceReset =
                Observable.just(device)
                .flatMap(new Function<Device, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(Device device) throws Exception {
                        return toDeviceFactoryResetObservable(device);
                    }
                })
                        .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                        .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                        .takeWhile(response ->{
                            Log.e("rerererererere", "Toggle Device Response: " + response);
                            Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                            Log.e("rerererererere", "Body Expected To Be: " + response.body());
                            Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                            Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                            Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                            Log.e("rererererere", "HMACs Are Equal");
                            FactoryResetResponse factoryResetResponse = RayanUtils.convertToObject(FactoryResetResponse.class, response.body());
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[1])+1));
                            device.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[0]);
                            Log.e("TAGTAGTAG", "Should I go: " + factoryResetResponse.getCmd());
                            if (factoryResetResponse.getCmd().equals("wrong_stword"))
                                return true;
                            else{
                                Device deviceToUpdate = new Device(device);
                                device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[1])+1));
                                device.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[0]);
                                deviceDatabase.updateDevice(deviceToUpdate);
                                return false;
                            }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                        }).timeout(4, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());

        Observable.concat(toDeviceReset, deleteUserObservable(new DeleteUserRequest(device.getId(), device.getGroupId())))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "Response is: " + o.getClass().getCanonicalName());
                        if (o instanceof BaseResponse){
                            BaseResponse baseResponse = (BaseResponse) o;
                            if (baseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.FACTORY_RESET_DONE);
                            else if (baseResponse.getData().getMessage() != null)
                                results.postValue(baseResponse.getData().getMessage());
                            else results.postValue(AppConstants.ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        results.postValue(AppConstants.ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return results;
    }


    @SuppressLint("CheckResult")
    public LiveData<String> toRemoteHubFactoryReset(RemoteHub remoteHub){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable<Response<String>> toDeviceReset =
                Observable.just(remoteHub)
                .flatMap(new Function<RemoteHub, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(RemoteHub remoteHub) throws Exception {
                        return toRemoteHubFactoryResetObservable(remoteHub);
                    }
                })
                        .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                        .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                        .takeWhile(response ->{
                            Log.e("rerererererere", "Toggle Device Response: " + response);
                            Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                            Log.e("rerererererere", "Body Expected To Be: " + response.body());
                            Log.e("rerererererere", "Sending Status Word Is: " + remoteHub.getStatusWord());
                            Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), remoteHub.getSecret()));
                    if (response.headers().get("auth") != null){
                            Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                            Log.e("rererererere", "HMACs Are Equal");
                            FactoryResetResponse factoryResetResponse = RayanUtils.convertToObject(FactoryResetResponse.class, response.body());
                            remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),remoteHub.getSecret()).split("#")[1])+1));
                            remoteHub.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),remoteHub.getSecret()).split("#")[0]);
                            Log.e("TAGTAGTAG", "Should I go: " + factoryResetResponse.getCmd());
                            if (factoryResetResponse.getCmd().equals("wrong_stword"))
                                return true;
                            else{
                                RemoteHub remoteHubToUpdate = new RemoteHub(remoteHub);
                                remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),remoteHub.getSecret()).split("#")[1])+1));
                                remoteHub.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),remoteHub.getSecret()).split("#")[0]);
                                remoteHubDatabase.updateRemoteHub(remoteHubToUpdate);
                                return false;
                            }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                        }).timeout(4, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());

        Observable.concat(toDeviceReset, deleteUserObservable(new DeleteUserRequest(remoteHub.getId(), remoteHub.getGroupId())))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        resetRemoteHubDisposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "Response is: " + o.getClass().getCanonicalName());
                        if (o instanceof BaseResponse){
                            BaseResponse baseResponse = (BaseResponse) o;
                            if (baseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.FACTORY_RESET_DONE);
                            else if (baseResponse.getData().getMessage() != null)
                                results.postValue(baseResponse.getData().getMessage());
                            else results.postValue(AppConstants.ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        results.postValue(AppConstants.ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return results;
    }


    private Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Response<String>> toDeviceFactoryResetObservable(Device device) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Log.e(TAG, "Sending StWord Is: " + device.getStatusWord());
        return apiServiceScalar
                .factoryReset(AppConstants.sha1(new FactoryResetRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())).ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()),new FactoryResetRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private Observable<Response<String>> toRemoteHubFactoryResetObservable(RemoteHub remoteHub) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Log.e(TAG, "Sending StWord Is: " + remoteHub.getStatusWord());
        return apiServiceScalar
                .factoryReset(AppConstants.sha1(new FactoryResetRequest(Encryptor.encrypt(remoteHub.getHeader().concat("#").concat(remoteHub.getStatusWord()).concat("#"), remoteHub.getSecret())).ToString(), remoteHub.getSecret()), AppConstants.getDeviceAddress(remoteHub.getIp()),new FactoryResetRequest(Encryptor.encrypt(remoteHub.getHeader().concat("#").concat(remoteHub.getStatusWord()).concat("#"), remoteHub.getSecret())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceChangeAccessPoint(Device device, String ssid, String password){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable<Response<String>> toDeviceChangeAccessPoint = Observable.just(device)
                .flatMap(new Function<Device, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(Device device) throws Exception {
                        return toDeviceChangeAccessPointObservable(device, new ChangeAccessPointRequest(device.getName1(), password, ssid, device.getStyle(), Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())));
                    }
                })
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response ->{
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                    Log.e("rererererere", "Auth Is Not NUll");
                    ChangeAccessPointResponse changeAccessPointResponse = RayanUtils.convertToObject(ChangeAccessPointResponse.class, response.body());
                    if (AppConstants.sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                    Log.e("rererererere", "HMACs Are Equal");
                    device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeAccessPointResponse.getStword(),device.getSecret()).split("#")[1])+1));
                    device.setHeader(Encryptor.decrypt(changeAccessPointResponse.getStword(),device.getSecret()).split("#")[0]);
                    Log.e("TAGTAGTAG", "Should I go: " + changeAccessPointResponse.getCmd());
                    if (changeAccessPointResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else{
                        Device deviceToUpdate = new Device(device);
                        device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeAccessPointResponse.getStword(),device.getSecret()).split("#")[1])+1));
                        device.setHeader(Encryptor.decrypt(changeAccessPointResponse.getStword(),device.getSecret()).split("#")[0]);
                        deviceDatabase.updateDevice(deviceToUpdate);
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        Observable.concat(toDeviceChangeAccessPoint, editDeviceObservable(new EditDeviceTopicRequest(device.getId(), device.getGroupId(), device.getName1(), device.getType(), ssid)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "onNext() called with: o = [" + o + "]");
                        if (o instanceof DeviceResponse){
                            DeviceResponse deviceResponse = (DeviceResponse) o;
                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.CHANGING_WIFI);
                            else if (deviceResponse.getData().getMessage() != null)
                                results.postValue(deviceResponse.getData().getMessage());
                            else results.postValue(AppConstants.ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }
                });
        return results;
    }
    private Observable<Response<String>> toDeviceChangeAccessPointObservable(Device device, ChangeAccessPointRequest changeAccessPointRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return apiServiceScalar
                .changeAccessPoint(AppConstants.sha1(changeAccessPointRequest.ToString(), device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), changeAccessPointRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    @SuppressLint("CheckResult")
    public LiveData<String> toRemoteHubChangeAccessPoint(RemoteHub remoteHub, String ssid, String password){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable<Response<String>> toDeviceChangeAccessPoint = Observable.just(remoteHub)
                .flatMap(new Function<RemoteHub, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(RemoteHub remoteHub) throws Exception {
                        return toRemoteHubChangeAccessPointObservable(remoteHub, new ChangeAccessPointRequest(remoteHub.getName(), password, ssid, remoteHub.getStyle(), Encryptor.encrypt(remoteHub.getHeader().concat("#").concat(remoteHub.getStatusWord()).concat("#"), remoteHub.getSecret())));
                    }
                })
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response ->{
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + remoteHub.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), remoteHub.getSecret()));
                    if (response.headers().get("auth") != null){
                    Log.e("rererererere", "Auth Is Not NUll");
                    ChangeAccessPointResponse changeAccessPointResponse = RayanUtils.convertToObject(ChangeAccessPointResponse.class, response.body());
                    if (AppConstants.sha1(response.body(), remoteHub.getSecret()).equals(response.headers().get("auth"))){
                    Log.e("rererererere", "HMACs Are Equal");
                        remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeAccessPointResponse.getStword(),remoteHub.getSecret()).split("#")[1])+1));
                        remoteHub.setHeader(Encryptor.decrypt(changeAccessPointResponse.getStword(),remoteHub.getSecret()).split("#")[0]);
                    Log.e("TAGTAGTAG", "Should I go: " + changeAccessPointResponse.getCmd());
                    if (changeAccessPointResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else{
                        RemoteHub deviceToUpdate = new RemoteHub(remoteHub);
                        remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(changeAccessPointResponse.getStword(),remoteHub.getSecret()).split("#")[1])+1));
                        remoteHub.setHeader(Encryptor.decrypt(changeAccessPointResponse.getStword(),remoteHub.getSecret()).split("#")[0]);
                        remoteHubDatabase.updateRemoteHub(deviceToUpdate);
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        Observable.concat(toDeviceChangeAccessPoint, editDeviceObservable(new EditDeviceTopicRequest(remoteHub.getId(), remoteHub.getGroupId(), remoteHub.getName(),remoteHub.getDeviceType(), ssid)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "onNext() called with: o = [" + o + "]");
                        if (o instanceof DeviceResponse){
                            DeviceResponse deviceResponse = (DeviceResponse) o;
                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
                                results.postValue(AppConstants.CHANGING_WIFI);
                            else if (deviceResponse.getData().getMessage() != null)
                                results.postValue(deviceResponse.getData().getMessage());
                            else results.postValue(AppConstants.ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }
                });
        return results;
    }
    private Observable<Response<String>> toRemoteHubChangeAccessPointObservable(RemoteHub remoteHub, ChangeAccessPointRequest changeAccessPointRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return apiServiceScalar
                .changeAccessPoint(AppConstants.sha1(changeAccessPointRequest.ToString(), remoteHub.getSecret()),AppConstants.getDeviceAddress(remoteHub.getIp()), changeAccessPointRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceChangeAccessPointObserver(MutableLiveData<String> results){
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
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<DeviceBaseResponse> toDeviceMqtt(String host, String user, String pass, String topic, String port, String ip){
        final MutableLiveData<DeviceBaseResponse> results = new MutableLiveData<>();
        toDeviceMqttObservable(new MqttTopicRequest(host,user,pass,topic,port),ip).subscribe(toDeviceMqttObserver(results));
        return results;
    }
    public Observable<DeviceBaseResponse> toDeviceMqttObservable(MqttTopicRequest mqttTopicRequest, String ip){
        return apiService
                .sendMqtt(AppConstants.getDeviceAddress(ip), mqttTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceBaseResponse> toDeviceMqttObserver(MutableLiveData<DeviceBaseResponse> results){
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
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<String> toDeviceEndSettings(Device device) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        final MutableLiveData<String> result = new MutableLiveData<>();
        Observable.just(device)
                .flatMap(new Function<Device, Observable<Response<EndSettingsResponse>>>() {
                    @Override
                    public Observable<Response<EndSettingsResponse>> apply(Device device) throws Exception {
                        return toDeviceEndSettingsObservable(device, new EndSettingsRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())));
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(settingsResponseResponse ->{
                    Log.e("toDeviceEndSettings", "Toggle Device Response: " + settingsResponseResponse);
                    Log.e("toDeviceEndSettings", "Auth: " + settingsResponseResponse.headers().get("auth"));
                    Log.e("toDeviceEndSettings", "Body Expected To Be: " + settingsResponseResponse.body().ToString());
                    Log.e("toDeviceEndSettings", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("toDeviceEndSettings", "HMAC of Body: " + AppConstants.sha1(settingsResponseResponse.body().ToString(), device.getSecret()));
                    if (settingsResponseResponse.headers().get("auth") != null){
                        Log.e("toDeviceEndSettings", "Auth Is Not NUll");
                        if (AppConstants.sha1(settingsResponseResponse.body().ToString(), device.getSecret()).equals(settingsResponseResponse.headers().get("auth"))
                                || AppConstants.sha1(settingsResponseResponse.body().wrongToString(), device.getSecret()).equals(settingsResponseResponse.headers().get("auth"))){
                            Log.e("toDeviceEndSettings", "HMACs Are Equal");
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(settingsResponseResponse.body().getStword(),device.getSecret()).split("#")[1])+1));
                            device.setHeader(Encryptor.decrypt(settingsResponseResponse.body().getStword(),device.getSecret()).split("#")[0]);
                            Log.e("toDeviceEndSettings", "Should I go: " + settingsResponseResponse.body().getCmd());
                            if (settingsResponseResponse.body().getCmd().equals("wrong_stword"))
                                return true;
                            else{
                                Device deviceToUpdate = new Device(device);
                                deviceDatabase.updateDevice(deviceToUpdate);
                                result.postValue(AppConstants.END_SETTINGS);
                                return false;
                            }
                        }else Log.e("toDeviceEndSettings", "HMACs Are NOT Equal");
                    }else Log.e("toDeviceEndSettings", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .retry(3)
                .subscribe(new Observer<Response<EndSettingsResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                    }

                    @Override
                    public void onNext(Response<EndSettingsResponse> endSettingsResponseResponse) {
                        Log.d(TAG, "onNext() called with: endSettingsResponseResponse = [" + endSettingsResponseResponse + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("toDeviceEndSettings", "/onError/////" + e + e.getClass());
                        if (e instanceof SocketTimeoutException || e instanceof TimeoutException){
                            result.postValue(AppConstants.SOCKET_TIME_OUT);
                        }
                        result.postValue(e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }
                });
        return result;
    }

    private Observable<Response<EndSettingsResponse>> toDeviceEndSettingsObservable(Device device, EndSettingsRequest endSettingsRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return apiService
                .endSettings(AppConstants.sha1(endSettingsRequest.ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()), endSettingsRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<EndSettingsResponse> toDeviceEndSettingsObserver(MutableLiveData<String> results){
        return new DisposableObserver<EndSettingsResponse>() {

            @Override
            public void onNext(@NonNull EndSettingsResponse baseResponse) {
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
    public void updateRemote(Remote remote){
        remoteDatabase.updateRemote(remote);
    }
    public void updateRemoteHub(RemoteHub remoteHub){
        remoteHubDatabase.updateRemoteHub(remoteHub);
    }

    public LiveData<String> toDeviceReady4Update(Device device){
        final MutableLiveData<String> result = new MutableLiveData<>();
        Observable.just(device).
                flatMap(new Function<Device, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(Device device) throws Exception {
                        return toDeviceReady4UpdateObservable(new UpdateRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())),device);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response ->{
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                    Log.e("rererererere", "Auth Is Not NUll");
                    UpdateResponse updateResponse = RayanUtils.convertToObject(UpdateResponse.class, response.body());
                        if (AppConstants.sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                    Log.e("rererererere", "HMACs Are Equal");
                    device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(updateResponse.getStword(),device.getSecret()).split("#")[1])+1));
                    device.setHeader(Encryptor.decrypt(updateResponse.getStword(),device.getSecret()).split("#")[0]);
                    Log.e("TAGTAGTAG", "Should I go: " + updateResponse.getCmd());
                    if (updateResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else{
                        Device deviceToUpdate = new Device(device);
                        deviceDatabase.updateDevice(deviceToUpdate);
                        result.postValue(AppConstants.DEVICE_READY_FOR_UPDATE);
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> updateResponseResponse) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        result.postValue(AppConstants.ERROR);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return result;
    }

    private Observable<Response<String>> toDeviceReady4UpdateObservable(UpdateRequest baseRequest, Device device){
        try {
            return apiServiceScalar
                    .deviceUpdate(AppConstants.sha1(baseRequest.ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()), baseRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Disposable resetRemoteHubDisposable;
    private Disposable settingsRemoteHubObservable;
    public Disposable getSettingsRemoteHubObservable() { return settingsRemoteHubObservable; }
    public Disposable getResetRemoteHubDisposable() { return resetRemoteHubDisposable; }

    @SuppressLint("CheckResult")
    public MutableLiveData<String> remoteHubReadyForSettings(RemoteHub remoteHub) {
        Log.e("//////////","Sending ready to device: " + remoteHub);
        MutableLiveData<String> result = new MutableLiveData<>();
        Observable.just(remoteHub)
                .flatMap(new Function<RemoteHub, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(RemoteHub remoteHub) throws Exception {
                        Log.d(TAG, "apply() called with: upstream = [" + remoteHub + "]");
                        Log.d(TAG, "StatusWord: " + remoteHub.getStatusWord());
                        return remoteHubReady4SettingsObservable(remoteHub);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response -> {
                    Log.e("rerererererere", "Toggle Device RawResponse: " + response);
                    Log.e("rerererererere", "Toggle Device RawResponse: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + remoteHub.getStatusWord());
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), remoteHub.getSecret()));
                    Ready4SettingsResponse ready4SettingsResponse = RayanUtils.convertToObject(Ready4SettingsResponse.class, response.body());
                    if (ready4SettingsResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else if (ready4SettingsResponse.getCmd().equals(AppConstants.SETTINGS)){
                        result.postValue(AppConstants.SETTINGS);
                        remoteHubDatabase.updateRemoteHub(remoteHub);
                        return false;
                    }
                    else {
                        Log.e(this.getClass().getSimpleName(), "So What received? " + ready4SettingsResponse.getCmd());
                        result.postValue(ready4SettingsResponse.getCmd());
                        return false;
                    }
                })
                .timeout(4, TimeUnit.SECONDS)
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                        settingsRemoteHubObservable = d;
                    }

                    @Override
                    public void onNext(Response<String> ready4SettingsResponse) {
                        Log.d(TAG, "onNext() called with: ready4SettingsResponse = [" + ready4SettingsResponse + "]");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException || e instanceof TimeoutException){
                            result.postValue(AppConstants.SOCKET_TIME_OUT);
                        }
                        if (e instanceof NullPointerException){
                            result.postValue(AppConstants.MISSING_PARAMS);
                        }else
                        result.postValue(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete() called");
                    }
                });
        return result;
    }

    private Observable<Response<String>> remoteHubReady4SettingsObservable(RemoteHub remoteHub) {
        Log.e(TAG, "device: " + remoteHub);
        try {
            return ApiUtils.getApiServiceScalar().settings(AppConstants.sha1(new Ready4SettingsRequest(Encryptor.encrypt(remoteHub.getHeader().concat("#").concat(remoteHub.getStatusWord()).concat("#"), remoteHub.getSecret())).ToString(), remoteHub.getSecret()), AppConstants.getDeviceAddress(remoteHub.getIp()),new Ready4SettingsRequest(Encryptor.encrypt(remoteHub.getHeader().concat("#").concat(remoteHub.getStatusWord()).concat("#"), remoteHub.getSecret())))
                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MutableLiveData<VersionResponse> getDeviceVersion(Device device){
        MutableLiveData<VersionResponse> results = new MutableLiveData<>();
        apiService.getVersion(AppConstants.getDeviceAddress(device.getIp()), new BaseRequest(AppConstants.GET_VERSION))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<VersionResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Getting device version onSubscribe: ");
                    }

                    @Override
                    public void onNext(VersionResponse versionResponse) {
                        Log.e(TAG, "Getting device version on next: " + versionResponse);
                        results.postValue(versionResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Getting device version onError: " + e);
                        if (e instanceof SocketTimeoutException)
                            results.postValue(new VersionResponse());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Getting device version onComplete: ");
                    }
                });
        return results;
    }


    @SuppressLint("CheckResult")
    public LiveData<String> toDeviceDoUpdate(String cmd, List<String> codeList, String ip){
        final MutableLiveData<String> results = new MutableLiveData<>();
        Observable.fromIterable(codeList)
                .concatMap(s -> toDeviceDoUpdateObservable(new UpdateDeviceRequest(cmd,s),ip))
                .takeWhile(deviceBaseResponse -> {
                    Log.e("////////////" ," ////////: "+ deviceBaseResponse);
                    if(deviceBaseResponse.getCmd().equals(AppConstants.DEVICE_UPDATE_CODE_WROTE)){
                        return true;
                    }
                    return false;
                }).subscribe(toDeviceDoUpdateObserver(results));
        return results;
    }

    private Observable<DeviceBaseResponse> toDeviceDoUpdateObservable(UpdateDeviceRequest updateDeviceRequest, String ip){
        return apiService.deviceDoUpdate(AppConstants.getDeviceAddress(ip), updateDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<DeviceBaseResponse> toDeviceDoUpdateObserver(MutableLiveData<String> results){
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
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public LiveData<ArrayList<String>> toDeviceAllFilesList(String cmd,String ip){
        final MutableLiveData<ArrayList<String>> results = new MutableLiveData<>();
        toDeviceAllFilesListObservable(new BaseRequest(cmd),ip).subscribe(toDeviceAllFilesListObserver(results));
        return results;
    }
    private Observable<AllFilesListResponse> toDeviceAllFilesListObservable(BaseRequest baseRequest, String ip){
        return apiService
                .deviceFileList("http://10.0.3.2/DeviceGetFileList.php", baseRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<AllFilesListResponse> toDeviceAllFilesListObserver(MutableLiveData<ArrayList<String>> results){
        return new DisposableObserver<AllFilesListResponse>() {

            @Override
            public void onNext(@NonNull AllFilesListResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getFiles());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    ArrayList<String> res=new ArrayList<>();
                    res.add(AppConstants.SOCKET_TIME_OUT);
                    results.postValue(res);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }


    public LiveData<String> sendFilesToDevicePermit(String request){
        final MutableLiveData<String> results = new MutableLiveData<>();
        SendFilesToDevicePermitObservable(new SendFilesToDevicePermitRequest(request)).subscribe(SendFilesToDevicePermitObserver(results));
        return results;
    }
    private Observable<SendFilesToDevicePermitResponse> SendFilesToDevicePermitObservable(SendFilesToDevicePermitRequest sendFilesToDevicePermitRequest){

        return apiService
                .deviceSendFilePermit("http://10.0.3.2/YesNoApi.php", sendFilesToDevicePermitRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<SendFilesToDevicePermitResponse> SendFilesToDevicePermitObserver(MutableLiveData<String> results){
        return new DisposableObserver<SendFilesToDevicePermitResponse>() {

            @Override
            public void onNext(@NonNull SendFilesToDevicePermitResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse.getPermit());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e instanceof SocketTimeoutException){
                    results.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

    public BaseDevice getBaseDevice(Pair<String, String> pair){
        switch (pair.first) {
            case AppConstants.DEVICE_TYPE_DEVICE:
                return deviceDatabase.getDevice(pair.second);
            case AppConstants.DEVICE_TYPE_RemoteHub:
                return remoteHubDatabase.getRemoteHub(pair.second);
            case AppConstants.DEVICE_TYPE_Remote:
                return remoteDatabase.getRemote(pair.second);
        }
        return null;
    }
    public void buildAlertMessageNoGps(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("برای ادامه نیاز به سرویس Location داریم")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
