package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.Ready4SettingsRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.Ready4SettingsResponse;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;
import retrofit2.Response;

public class DevicesManagementListFragmentViewModel extends DevicesFragmentViewModel {

    private final String TAG = this.getClass().getCanonicalName();
    private SendUDPMessage sendUDPMessage = new SendUDPMessage();
    public DevicesManagementListFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Device> getDevices(){
        return deviceDatabase.getAllDevices();
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<String> setReadyForSettings(Device device){
        MutableLiveData<String> result = new MutableLiveData<>();
        Observable.intervalRange(0, 4, 0,500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("//////////", "onSubscribe" + d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        sendUDPMessage.sendUdpMessage(device.getIp(), ((RayanApplication)getApplication()).getJSON(AppConstants.SETTINGS, null).toString());
                        Log.e("//////////", "////onNext//" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("//////////", "/onError/////" + e + (e instanceof SocketTimeoutException) + e.getClass());
                        if (e instanceof SocketTimeoutException){
                            Log.e("//////////", "/onError///ttttttttttttt//" + e);
                            result.postValue(AppConstants.SOCKET_TIME_OUT);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e("//////////", "//onComplete////" );
                        result.postValue(device.getChipId());
                    }
                });
        return result;
    }

    public Observable<Response<String>> setReady4SettingsObservable(Device device) {
        Log.e(TAG, "device: " + device);
        try {
            Ready4SettingsRequest request = new Ready4SettingsRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
            return ApiUtils.getApiServiceScalar().settings(AppConstants.sha1(request.ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp(), AppConstants.SETTINGS_ENTER),request)
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

    @SuppressLint("CheckResult")
    public MutableLiveData<String> setReadyForSettingsHttp(Device device) {
        Log.e("//////////","Sending ready to device: " + device);
        MutableLiveData<String> result = new MutableLiveData<>();
            Observable.just(device)
                    .flatMap(new Function<Device, Observable<Response<String>>>() {
                        @Override
                        public Observable<Response<String>> apply(Device ready4SettingsResponse) throws Exception {
                            Log.d(TAG, "apply() called with: upstream = [" + ready4SettingsResponse + "]");
                            Log.d(TAG, "StatusWord: " + ready4SettingsResponse.getStatusWord());
                            return setReady4SettingsObservable(device);
                        }
                    })
                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                    .takeWhile(response -> {
                        Log.e("rerererererere", "Toggle Device RawResponse: " + response);
                        Log.e("rerererererere", "Toggle Device RawResponse: " + response.body());
                        Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                        Log.e("rerererererere", "Body Expected To Be: " + response.body());
                        Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                        Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(response.body(), device.getSecret()));
//                    if (response.headers().get("auth") != null){
//                        Log.e("rererererere", "Auth Is Not NUll");
//                        if (AppConstants.sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
//                        Log.e("rererererere", "HMACs Are Equal");
                        Ready4SettingsResponse ready4SettingsResponse = RayanUtils.convertToObject(Ready4SettingsResponse.class, response.body());
                        device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(ready4SettingsResponse.getSTWORD(),device.getSecret()).split("#")[1])+1));
                        device.setHeader(Encryptor.decrypt(ready4SettingsResponse.getSTWORD(),device.getSecret()).split("#")[0]);
                        if (ready4SettingsResponse.getResult().equals(AppConstants.WRONG_STWORD))
                            return true;
                        else if (ready4SettingsResponse.getResult().equals(AppConstants.SUCCESSFUL)){
                            result.postValue(AppConstants.SETTINGS);
                            deviceDatabase.updateDevice(device);
                            return false;
                        }
                        else {
                            Log.e(this.getClass().getSimpleName(), "So What received? " + ready4SettingsResponse.getCmd());
                            result.postValue(ready4SettingsResponse.getCmd());
                            return false;
                        }
//                        }else Log.e("rererererere", "HMACs Are NOT Equal");
//                    }else Log.e("rererererere", "Auth IS NULL");
//                    return false;
                    })
                    .timeout(4, TimeUnit.SECONDS)
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                        }

                        @Override
                        public void onNext(Response<String> ready4SettingsResponse) {
                            Log.d(TAG, "onNext() called with: ready4SettingsResponse = [" + ready4SettingsResponse + "]");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError() called with: e = [" + e + "]");
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

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }
    public void updateRemoteHub(RemoteHub remoteHub){
        remoteHubDatabase.updateRemoteHub(remoteHub);
    }
    public void updateRemote(Remote remote){
        remoteDatabase.updateRemote(remote);
    }

}
