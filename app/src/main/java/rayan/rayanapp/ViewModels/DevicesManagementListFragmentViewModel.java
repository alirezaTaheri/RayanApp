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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;
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

    @SuppressLint("CheckResult")
    public MutableLiveData<String> setReadyForSettingsHttp(Device device){
        Log.e("//////////","Sending ready to device: " + device);
        MutableLiveData<String> result = new MutableLiveData<>();
        ApiUtils.getApiService().settings(getDeviceAddress(device.getIp()),new BaseRequest(AppConstants.SETTINGS))
        .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
        .subscribe(new Observer<DeviceBaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("//////////","onSubscribe");
            }

            @Override
            public void onNext(DeviceBaseResponse deviceBaseResponse) {
                Log.e("//////////","onNext: " + deviceBaseResponse);
                result.postValue(deviceBaseResponse.getCmd());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("//////////", "/onError/////" + e + e.getClass());
                if (e instanceof SocketTimeoutException){
                    result.postValue(AppConstants.SOCKET_TIME_OUT);
                }
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.e("//////////","onComplete");
            }
        });
        return result;
    }

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }


    public String getDeviceAddress(String ip){
//        return "http://"+ip+":"+AppConstants.HTTP_TO_DEVICE_PORT;
        return "http://"+"192.168.1.105"+":"+"80"+"/test.php";
    }
}
