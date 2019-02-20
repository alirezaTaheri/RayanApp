package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
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
                        Log.e("//////////", "/onError/////" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("//////////", "//onComplete////" );
                        result.postValue(device.getChipId());
                    }
                });
        return result;
    }

    public void updateDevice(Device device){
        deviceDatabase.updateDevice(device);
    }
}
