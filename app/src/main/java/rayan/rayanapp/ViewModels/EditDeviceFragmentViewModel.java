package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.SendFilesToDevicePermitRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.ChangeAccessPointRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.EndSettingsRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.FactoryResetRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.MqttTopicRequest;
//<<<<<<< HEAD
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.UpdateRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
//=======
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.UpdateDeviceRequest;
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.SendFilesToDevicePermitResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.AllFilesListResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.ChangeAccessPointResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.EndSettingsResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.FactoryResetResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.UpdateResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.VersionResponse;
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


//    public LiveData<DeviceResponse> editDevice(String id, String name, String type, String groupId, String ssid){
//        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
//        editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid)).subscribe(editDeviceObserver(results));
//        return results;
//    }
    private Observable<DeviceResponse> editDeviceObservable(EditDeviceRequest editDeviceRequest){
        Log.e("?><?><?><?><?><", "editing: " + editDeviceRequest);
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
//                        Device deviceToUpdate = new Device(device);
//                        deviceToUpdate.setPin1(changeNameResponse.body().getPin1());
//                        deviceToUpdate.setPin2(changeNameResponse.body().getPin2());
//                        deviceDatabase.updateDevice(deviceToUpdate);
                        return false;
                    }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                });
//                .subscribe(new Observer<Response<ChangeNameResponse>>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
//            }
//
//            @Override
//            public void onNext(Response<ChangeNameResponse> changeNameResponseResponse) {
//                Log.d(TAG, "onNext() called with: changeNameResponseResponse = [" + changeNameResponseResponse + "]");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "onError: ", e);
//                if (e instanceof SocketTimeoutException || e instanceof TimeoutException){
//                    response.postValue(AppConstants.SOCKET_TIME_OUT);
//                }
//                response.postValue(e.toString());
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete() called");
//            }
//        });
//        Observable.concat(deviceChangeNameObservable, Observable.just(1,2,3,4,5,6))
        Observable.concat(deviceChangeNameObservable, editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid)))
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
//        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
//                Observable.just(1).flatMap(integer -> {
//                    return
//                })
//                        .repeatWhen(completed -> {
//                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
//                            return completed.delay(200, TimeUnit.MILLISECONDS);
//                        }).flatMap(new Function<ChangeNameResponse, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(ChangeNameResponse factoryResetResponse) throws Exception {
//                        if (factoryResetResponse.getStword() != null){
//                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[1])+1));
//                            device.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[0]);
//                            updateDevice(device);
//                        }
//                        if (factoryResetResponse.getCmd().equals(AppConstants.CHANGE_NAME_TRUE)) {
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd");
//                            response.postValue(AppConstants.OPERATION_DONE);
//                            return editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid));
////                            return Observable.just(0);
//                        }
//                        else{
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd000000000000000000");
//                            return Observable.just(1);
//                        }
//                    }
//                })
//                        .takeWhile(toggleDeviceResponse -> {
//                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
//                            if (toggleDeviceResponse instanceof Integer && (int)toggleDeviceResponse == 1)
//                                return true;
//                            else return false;
//                        }),
//                (changeNameResponse, deviceResponse) -> {
//                    return deviceResponse;})
//                .timeout(4, TimeUnit.SECONDS)
////                .takeWhile(aLong -> aLong<1)
//                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("//////////","onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Object aLong) {
//                        Log.e("//////////","onNext: " + aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("//////////", "/onError/////" + e + e.getClass());
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("//////////","onComplete");
//                    }
//                });
//        Observable.zip(
//                toDeviceChangeNameObservable(new ChangeNameRequest(baseName),ip).subscribeOn(Schedulers.io())
//                        .doOnError(throwable -> {
//                            Log.e(TAG, "error occurred: "+throwable);
//                        })
//                        .doOnNext(changeNameResponse -> {
//                    Log.e(TAG, " 00000000 ChangeNameResponse: " + changeNameResponse);
//                }),
//                editDeviceObservable(new EditDeviceRequest(id, groupId, name, type, ssid)).subscribeOn(Schedulers.io()).doOnNext(deviceResponse -> {
//                    Log.e(TAG, "000000000 DeviceResponse: " + deviceResponse);
//                }),
//                (BiFunction<ChangeNameResponse, DeviceResponse, Object>) (changeNameResponse, deviceResponse) -> {
//                    Log.e(TAG, "ChangeNameResponse: " + changeNameResponse);
//                    Log.e(TAG, "DeviceResponse: " + deviceResponse);
//                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR)){
//                        if (deviceResponse.getData().getMessage() != null)
//                        return deviceResponse.getData().getMessage();
//                        else return AppConstants.ERROR;
//                    }
//                    if (deviceResponse.getData().getMessage()!=null && deviceResponse.getData().getMessage().toLowerCase().contains(AppConstants.FORBIDDEN)){
//                        return AppConstants.FORBIDDEN;
//                    }
//                    if (changeNameResponse.getCmd().contains(AppConstants.CHANGE_NAME_FALSE)){
//                        return AppConstants.CHANGE_NAME_FALSE;
//                    }
//                    return AppConstants.OPERATION_DONE;
//                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Object>() {
//            @Override
//            public void onNext(Object o) {
//                Log.e(TAG, "onNext: " + o);
//                response.postValue((String)o);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                if (e instanceof SocketTimeoutException){
//                    response.postValue(AppConstants.SOCKET_TIME_OUT);
//                }
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "ChangeName: onComplete " );
//            }
//        });
        return results;
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<String> flatMqtt(Device device){
        MutableLiveData<String> result = new MutableLiveData<>();
        createTopicObservable(new CreateTopicRequest(device.getId(), device.getGroupId(),device.getChipId(), AppConstants.MQTT_HOST))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse -> toDeviceMqttObservable(new MqttTopicRequest(AppConstants.MQTT_HOST,
                        deviceResponse.getData().getDevice().getUsername(),
                        ((deviceResponse.getData().getDevice().getPassword() != null) ? deviceResponse.getData().getDevice().getPassword():"Password Not Set"),
                        deviceResponse.getData().getDevice().getTopic().getTopic(),
                        String.valueOf(AppConstants.MQTT_PORT)
                        ),device.getIp()))
        .subscribe(new Observer<DeviceBaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(DeviceBaseResponse deviceBaseResponse) {
                Log.e(TAG, "onNext"+deviceBaseResponse);
                result.postValue(deviceBaseResponse.getCmd());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError" + e);
                if (e instanceof SocketTimeoutException){
                    result.postValue(AppConstants.SOCKET_TIME_OUT);
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete Mqtt Sent To Device");
            }
        });
        return result;
    }

//    public LiveData<ChangeNameResponse> toDeviceChangeName(String name, String ip){
//        final MutableLiveData<ChangeNameResponse> results = new MutableLiveData<>();
//        toDeviceChangeNameObservable(new ChangeNameRequest(name),ip).subscribe(toDeviceChangeNameObserver(results));
//        return results;
//    }
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
//                        if (o instanceof DeviceResponse){
//                            DeviceResponse deviceResponse = (DeviceResponse) o;
//                            if (deviceResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION))
//                                results.postValue(AppConstants.FACTORY_RESET_DONE);
//                            else if (deviceResponse.getData().getMessage() != null)
//                                results.postValue(deviceResponse.getData().getMessage());
//                            else results.postValue(AppConstants.ERROR);
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
//                Observable.just(1).flatMap(integer -> {
//                    return toDeviceFactoryResetObservable(device);
//                })
//                        .repeatWhen(completed -> {
//                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
//                            return completed.delay(200, TimeUnit.MILLISECONDS);
//                        }).flatMap(new Function<FactoryResetResponse, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(FactoryResetResponse factoryResetResponse) throws Exception {
//                        if (factoryResetResponse.getStword() != null){
//                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[1])+1));
//                            device.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[0]);
//                            updateDevice(device);
//                        }
//                        if (factoryResetResponse.getCmd().equals(AppConstants.FACTORY_RESET_DONE)) {
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd");
//                            results.postValue(AppConstants.FACTORY_RESET_DONE);
//                            return ;
////                            return Observable.just(0);
//                        }
//                        else{
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd000000000000000000");
//                            return Observable.just(1);
//                        }
//                    }
//                })
//                        .takeWhile(toggleDeviceResponse -> {
//                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
//                            if (toggleDeviceResponse instanceof Integer && (int)toggleDeviceResponse == 1)
//                                return true;
//                            else return false;
//                        }),
//                (changeNameResponse, deviceResponse) -> {
//                    return deviceResponse;})
//                .timeout(4, TimeUnit.SECONDS)
////                .takeWhile(aLong -> aLong<1)
//                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("//////////","onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Object aLong) {
//                        Log.e("//////////","onNext: " + aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("//////////", "/onError/////" + e + e.getClass());
//                        if (e instanceof SocketTimeoutException || e instanceof TimeoutException){
//                            results.postValue(AppConstants.SOCKET_TIME_OUT);
//                        }
//                        results.postValue(e.toString());
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("//////////","onComplete");
//                    }
//                });
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
    private DisposableObserver<DeviceBaseResponse> toDeviceFactoryResetObserver(MutableLiveData<String> results){
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
        Observable.concat(toDeviceChangeAccessPoint, editDeviceObservable(new EditDeviceRequest(device.getId(), device.getGroupId(), device.getName1(), device.getType(), ssid)))
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

//        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
//                Observable.just(1).flatMap(integer -> {
//                    return
//                })
//                        .repeatWhen(completed -> {
//                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
//                            return completed.delay(200, TimeUnit.MILLISECONDS);
//                        }).flatMap(new Function<ChangeAccessPointResponse, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(ChangeAccessPointResponse factoryResetResponse) throws Exception {
//                        if (factoryResetResponse.getStword() != null){
//                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[1])+1));
//                            device.setHeader(Encryptor.decrypt(factoryResetResponse.getStword(),device.getSecret()).split("#")[0]);
//                            updateDevice(device);
//                        }
//                        if (factoryResetResponse.getCmd().equals(AppConstants.CHANGING_WIFI)) {
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd");
//                            results.postValue(AppConstants.CHANGE_WIFI);
//                            return
////                            return Observable.just(0);
//                        }
//                        else{
//                            Log.e(TAG, "dddddddddddddddddddddddddddddd000000000000000000");
//                            return Observable.just(1);
//                        }
//                    }
//                })
//                        .takeWhile(toggleDeviceResponse -> {
//                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
//                            if (toggleDeviceResponse instanceof Integer && (int)toggleDeviceResponse == 1)
//                                return true;
//                            else return false;
//                        }),
//                (changeNameResponse, deviceResponse) -> {
//                    return deviceResponse;})
//                .timeout(4, TimeUnit.SECONDS)
////                .takeWhile(aLong -> aLong<1)
//                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("//////////","onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Object aLong) {
//                        Log.e("//////////","onNext: " + aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("//////////", "/onError/////" + e + e.getClass());
//                        if (e instanceof SocketTimeoutException || e instanceof TimeoutException){
//                            results.postValue(AppConstants.SOCKET_TIME_OUT);
//                        }
//                        results.postValue(e.toString());
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("//////////","onComplete");
//                    }
//                });
//        toDeviceChangeAccessPointObservable(new ChangeAccessPointRequest(hname, pwd, ssid, style),ip).subscribe(toDeviceChangeAccessPointObserver(results));
//        Observable.zip(
//                toDeviceChangeAccessPointObservable(new ChangeAccessPointRequest(device.getName1(), password, ssid, device.getStyle()), device.getIp())
//                        .doOnNext(deviceBaseResponse -> {
//                            Log.e(TAG, "Do on next for changing accessPoint: " + deviceBaseResponse);
//                        }),
//                editDeviceObservable(new EditDeviceRequest(device.getId(), device.getGroupId(), device.getName1(), device.getType(), ssid)).subscribeOn(Schedulers.io()).doOnNext(deviceResponse -> {
//                    Log.e(TAG, "000000000 DeviceResponse: " + deviceResponse);
//                }), (deviceBaseResponse, deviceResponse) -> {
//                    Log.e(TAG, "ChangeAccessPointResponse: " + deviceBaseResponse);
//                    Log.e(TAG, "DeviceResponse: " + deviceResponse);
//                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR)){
//                        if (deviceResponse.getData().getMessage() != null)
//                            return deviceResponse.getData().getMessage();
//                        else return AppConstants.ERROR;
//                    }
//                    if (deviceResponse.getData().getMessage()!=null && deviceResponse.getData().getMessage().toLowerCase().contains(AppConstants.FORBIDDEN)){
//                        return AppConstants.FORBIDDEN;
//                    }
//                    if (deviceBaseResponse.getCmd().contains(AppConstants.CHANGE_NAME_FALSE)){
//                        return AppConstants.CHANGE_NAME_FALSE;
//                    }
//                    return AppConstants.OPERATION_DONE;
//                }
//        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.e(TAG, "OnSubscribe whole zip change accessPoint");
//            }
//
//            @Override
//            public void onNext(String s) {
//                Log.e(TAG, "OnNext Change AccessPoint: " + s);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "onError Change AccessPoint: " + e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "onComplete Change AccessPoint: ");
//            }
//        });

        return results;
    }
    private Observable<Response<String>> toDeviceChangeAccessPointObservable(Device device, ChangeAccessPointRequest changeAccessPointRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return apiServiceScalar
                .changeAccessPoint(AppConstants.sha1(changeAccessPointRequest.ToString(), device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), changeAccessPointRequest)
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
                    Log.e("rerererererere", "Toggle Device Response: " + settingsResponseResponse);
                    Log.e("rerererererere", "Auth: " + settingsResponseResponse.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + settingsResponseResponse.body().ToString());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(settingsResponseResponse.body().ToString(), device.getSecret()));
                    if (settingsResponseResponse.headers().get("auth") != null){
                        Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(settingsResponseResponse.body().ToString(), device.getSecret()).equals(settingsResponseResponse.headers().get("auth"))
                                || AppConstants.sha1(settingsResponseResponse.body().wrongToString(), device.getSecret()).equals(settingsResponseResponse.headers().get("auth"))){
                            Log.e("rererererere", "HMACs Are Equal");
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(settingsResponseResponse.body().getStword(),device.getSecret()).split("#")[1])+1));
                            device.setHeader(Encryptor.decrypt(settingsResponseResponse.body().getStword(),device.getSecret()).split("#")[0]);
                            Log.e("TAGTAGTAG", "Should I go: " + settingsResponseResponse.body().getCmd());
                            if (settingsResponseResponse.body().getCmd().equals("wrong_stword"))
                                return true;
                            else{
                                Device deviceToUpdate = new Device(device);
                                deviceDatabase.updateDevice(deviceToUpdate);
                                result.postValue(AppConstants.END_SETTINGS);
                                return false;
                            }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
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
                        Log.e("//////////", "/onError/////" + e + e.getClass());
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

    public void writeToFile(String mycode) {
        try {
            String rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.nodeUpdateFolder/";
           // String hi="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus condimentum sagittis lacus, laoreet luctus ligula laoreet ut. Vestibulum ullamcorper accumsan velit vel vehicula. Proin tempor lacus arcu. Nunc at elit condimentum, semper nisi et, condimentum mi. In venenatis blandit nibh at sollicitudin. Vestibulum dapibus mauris at orci maximus pellentesque. Nullam id elementum ipsum. Suspendisse cursus lobortis viverra. Proin et erat at mauris tincidunt porttitor vitae ac dui.\n" +
            //       "Fusce tincidunt dictum tempor. Mauris nec tellus posuere odio hendrerit sodales. Cras sit amet dapibus velit. Cras risus turpis, vehicula sed lobortis non, volutpat ut leo. Integer at efficitur risus, nec volutpat turpis. Phasellus in arcu sed nunc varius eleifend ut nec dui. Donec pharetra arcu eget dui consectetur, et semper elit rutrum. Integer quis ornare nisl, et scelerisque enim. In libero ligula, porttitor non enim a, scelerisque molestie nibh.\n" +
              //      "Sed tristique auctor tellus id facilisis. Quisque laoreet auctor massa ut venenatis. Sed elementum quis neque non accumsan. Suspendisse eros justo, tempus dapibus facilisis eget, vehicula eu magna. Cras sodales mauris ac tincidunt pellentesque. Vestibulum hendrerit dictum lectus, quis tempor turpis. Morbi odio risus, ullamcorper ac quam vel, mattis dictum magna. Vivamus in dui diam. Nulla non tristique lectus, quis iaculis magna. Sed vel nisi a ante aliquet accumsan. Suspendisse dapibus lacus risus, at vulputate sapien faucibus non. Sed convallis nunc vel risus luctus maximus. Praesent a nulla tempus, varius enim non, aliquam tellus. Vestibulum non massa id diam aliquam suscipit non nec purus. Vivamus cursus dictum risus id vulputate. In commodo porta tellus, sed pharetra mauris vehicula vel.";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(rootPath + ".nodeUpdateFile.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);

            out.write(mycode.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readFromFile() {
        String pathRoot =Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/.nodeUpdateFolder/.nodeUpdateFile.txt";
        String code = "";
        try {
            File myFile = new File(pathRoot);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                code += aDataRow;
            }
            Log.e("it is code file",code);
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
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

    private DisposableObserver<DeviceBaseResponse> toDeviceReady4UpdateObserver(MutableLiveData<String> results){
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


}
