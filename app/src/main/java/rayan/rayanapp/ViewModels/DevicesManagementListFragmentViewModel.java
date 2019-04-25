package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.Ready4SettingsRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.Ready4SettingsResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.YesResponse;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;

public class DevicesManagementListFragmentViewModel extends DevicesFragmentViewModel {

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
                        sendUDPMessage.sendUdpMessage(device.getIp(), ((RayanApplication)getApplication()).getJson(AppConstants.SETTINGS, null).toString());
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

    public Observable<Ready4SettingsResponse> setReady4SettingsObservable(Device device){
        return ApiUtils.getApiService().settings(AppConstants.getDeviceAddress(device.getIp()),new Ready4SettingsRequest(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret())))
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }
    @SuppressLint("CheckResult")
    public MutableLiveData<String> setReadyForSettingsHttp(Device device){
        Log.e("//////////","Sending ready to device: " + device);
        MutableLiveData<String> result = new MutableLiveData<>();
        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
                Observable.just(1).flatMap(integer -> {
                    return setReady4SettingsObservable(device);
                })
                        .repeatWhen(completed -> {
                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
                            return completed.delay(200, TimeUnit.MILLISECONDS);
                        })
                        .takeWhile(toggleDeviceResponse -> {
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[0])+1));
                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
                            if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
                                return true;
                            else if (toggleDeviceResponse.getCmd().equals(AppConstants.SETTINGS)){
                                result.postValue(AppConstants.SETTINGS);
                                deviceDatabase.updateDevice(device);
                                return false;
                            }
                            else {
                                Log.e(this.getClass().getSimpleName(), "So What received? " + toggleDeviceResponse.getCmd());
                                result.postValue(toggleDeviceResponse.getCmd());
                            }
                            return false;
                        }),
                (changeNameResponse, deviceResponse) -> {
                    return changeNameResponse;})
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(aLong -> aLong<1)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("//////////","onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("//////////","onNext: " + aLong);
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
                        Log.e("//////////","onComplete");
                    }
                });
//        .subscribe(new Observer<DeviceBaseResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.e("//////////","onSubscribe");
//            }
//
//            @Override
//            public void onNext(DeviceBaseResponse deviceBaseResponse) {
//                Log.e("//////////","onNext: " + deviceBaseResponse);
//                result.postValue(deviceBaseResponse.getCmd());
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e("//////////", "/onError/////" + e + e.getClass());
//                if (e instanceof SocketTimeoutException){
//                    result.postValue(AppConstants.SOCKET_TIME_OUT);
//                }
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e("//////////","onComplete");
//            }
//        });
        return result;
    }

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }

}
