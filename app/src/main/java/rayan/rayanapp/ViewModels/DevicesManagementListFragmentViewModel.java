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
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.Ready4SettingsRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.Ready4SettingsResponse;
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

    public Observable<Response<Ready4SettingsResponse>> setReady4SettingsObservable(Device device) {
        Log.e(TAG, "device: " + device);
        try {
            return ApiUtils.getApiService().settings(AppConstants.sha1(new Ready4SettingsRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())).ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()),new Ready4SettingsRequest(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())))
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
                    .flatMap(new Function<Device, Observable<Response<Ready4SettingsResponse>>>() {
                        @Override
                        public Observable<Response<Ready4SettingsResponse>> apply(Device ready4SettingsResponse) throws Exception {
                            Log.d(TAG, "apply() called with: upstream = [" + ready4SettingsResponse + "]");
                            Log.d(TAG, "StatusWord: " + ready4SettingsResponse.getStatusWord());
                            return setReady4SettingsObservable(device);
                        }
                    })
                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                    .takeWhile(ready4SettingsResponse -> {
                        Log.e("rerererererere", "Toggle Device Response: " + ready4SettingsResponse);
//                        Log.e("TAGTAGTAG", "Should I go: " + ready4SettingsResponse.body());
//                        Log.e("TAGTAGTAG", "raw: " + ready4SettingsResponse.raw());
//                        Log.e("TAGTAGTAG", "raw.body: " + ready4SettingsResponse.raw().body());
//                        Log.e("TAGTAGTAG", "raw.netResponse: " + ready4SettingsResponse.raw().networkResponse());
//                        Log.e("TAGTAGTAG", "raw.body.string: " + ready4SettingsResponse.raw().body().string());
//                        Log.e("TAGTAGTAG", "toString: " + ready4SettingsResponse.raw().toString());
//                        Log.e("TAGTAGTAG", "message: " + ready4SettingsResponse.raw().message());
//                        Log.e("TAGTAGTAG", "body: " + ready4SettingsResponse.raw().body());
//                        Log.e("TAGTAGTAG", "source " + ready4SettingsResponse.raw().body().source());
//                        Log.e("TAGTAGTAG", "contentLength: " + ready4SettingsResponse.raw().body().contentLength());
//                        Log.e("TAGTAGTAG", "charStream: " + ready4SettingsResponse.raw().body().charStream());
//                        Log.e("TAGTAGTAG", "contentType: " + ready4SettingsResponse.raw().body().contentType());
//                        Log.e("TAGTAGTAG", "Should I go: " + ready4SettingsResponse.raw().body().bytes());
//                        Log.e("TAGTAGTAG", "string: " + ready4SettingsResponse.raw().body().string());
                        Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                        Log.e("rerererererere", "Body Expected To Be: " + ready4SettingsResponse.body().ToString());
                        Log.e("rerererererere", "Auth: " + ready4SettingsResponse.headers().get("auth"));
                        Log.e("rerererererere", "HMAC of Body: " + AppConstants.sha1(ready4SettingsResponse.body().ToString(), device.getSecret()));
                    if (ready4SettingsResponse.headers().get("auth") != null){
                        Log.e("rererererere", "Auth Is Not NUll");
                        if (AppConstants.sha1(ready4SettingsResponse.body().ToString(), device.getSecret()).equals(ready4SettingsResponse.headers().get("auth"))){
                        Log.e("rererererere", "HMACs Are Equal");
                        device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(ready4SettingsResponse.body().getStword(),device.getSecret()).split("#")[1])+1));
                        device.setHeader(Encryptor.decrypt(ready4SettingsResponse.body().getStword(),device.getSecret()).split("#")[0]);
                        if (ready4SettingsResponse.body().getCmd().equals("wrong_stword"))
                            return true;
                        else if (ready4SettingsResponse.body().getCmd().equals(AppConstants.SETTINGS)){
                            result.postValue(AppConstants.SETTINGS);
                            deviceDatabase.updateDevice(device);
                            return false;
                        }
                        else {
                            Log.e(this.getClass().getSimpleName(), "So What received? " + ready4SettingsResponse.body().getCmd());
                            result.postValue(ready4SettingsResponse.body().getCmd());
                            return false;
                        }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                    })
                    .timeout(4, TimeUnit.SECONDS)
                    .subscribe(new Observer<Response<Ready4SettingsResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
                        }

                        @Override
                        public void onNext(Response<Ready4SettingsResponse> ready4SettingsResponse) {
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

}
