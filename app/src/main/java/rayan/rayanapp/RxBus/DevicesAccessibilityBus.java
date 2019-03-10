package rayan.rayanapp.RxBus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
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
    Map<String, Disposable> disposables;
    DeviceDatabase deviceDatabase;
    ToggleDeviceAnimationProgress listener;
    int progressBarWidth = -1;
    public DevicesAccessibilityBus(Context context) {
        disposables = new HashMap<>();
        deviceDatabase = new DeviceDatabase(context);
    }

    private PublishSubject<Map<String, Disposable>> bus = PublishSubject.create();

//    public void registerForAnimation(ToggleDeviceAnimationProgress animationProgress, int progressBarWidth){
//        listener = animationProgress;
//        this.progressBarWidth = progressBarWidth;
//    }
//
//    public void setWaiting(String chipId, Disposable disposable, int position, String onVsOff, int pin) {
//
//    }
//
//    public void removeWaiting(String chipId) {
//    }
//
//    public Observable<Map<String, Disposable>> toObservable() {
//        return bus;
//    }
//
//    public boolean isWaiting(String chipId){
//        return disposables.get(chipId) != null;
//    }

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
