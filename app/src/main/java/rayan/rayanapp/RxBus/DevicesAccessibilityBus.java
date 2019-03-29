package rayan.rayanapp.RxBus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Util.AppConstants;

public class DevicesAccessibilityBus {
    Map<String, Disposable> disposablesPin1;
    Map<String, Disposable> disposablesPin2;
    DeviceDatabase deviceDatabase;
    ToggleDeviceAnimationProgress listener;
    int progressBarWidth = -1;
    public DevicesAccessibilityBus(Context context) {
        disposablesPin1 = new HashMap<>();
        disposablesPin2 = new HashMap<>();
        deviceDatabase = new DeviceDatabase(context);
    }

    private PublishSubject<String> bus = PublishSubject.create();

    public void setWaitingPin1(String chipId, Disposable disposable) {
        disposablesPin1.put(chipId, disposable);
    }

    public void removeWaitingPin1(String chipId) {
        if (disposablesPin1.get(chipId) != null)
            disposablesPin1.get(chipId).dispose();
        disposablesPin1.remove(chipId);
    }
    public void setWaitingPin2(String chipId, Disposable disposable) {
        disposablesPin2.put(chipId, disposable);
    }

    public void removeWaitingPin2(String chipId) {
        if (disposablesPin2.get(chipId) != null)
            disposablesPin2.get(chipId).dispose();
        disposablesPin2.remove(chipId);
    }

    public Observable<String> toObservable() {
        return bus;
    }

    public void send(String o) {
        bus.onNext(o);
    }


    public boolean isWaiting(String chipId){
        return disposablesPin1.get(chipId) != null || disposablesPin2.get(chipId) != null;
    }
    public boolean isWaitingPin1(String chipId){
        return disposablesPin1.get(chipId) != null;
    }
    public boolean isWaitingPin2(String chipId){
        return disposablesPin2.get(chipId) != null;
    }

//    public void setDeviceLocallyAccessibility(String chipId, boolean accessibility){
//        Device d = deviceDatabase.getDevice(chipId);
//        d.setLocallyAccessibility(accessibility);
//        deviceDatabase.updateDevice(d);
//    }
//
//    public void setDeviceOnlineAccessibility(String chipId, boolean accessibility){
//        Device d = deviceDatabase.getDevice(chipId);
//        d.setOnlineAccessibility(accessibility);
//        deviceDatabase.updateDevice(d);
//    }
}
