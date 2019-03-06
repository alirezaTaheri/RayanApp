package rayan.rayanapp.RxBus;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;

public class DevicesAccessibilityBus {
    Map<String, Disposable> disposables;
    Map<String, ValueAnimator> animatorMap;
    Map<String, Integer> idPos;
    DeviceDatabase deviceDatabase;
    ToggleDeviceAnimationProgress listener;
    int progressBarWidth = -1;
    public DevicesAccessibilityBus(Context context) {
        disposables = new HashMap<>();
        animatorMap = new HashMap<>();
        idPos = new HashMap<>();
        deviceDatabase = new DeviceDatabase(context);
    }

    private PublishSubject<Map<String, Disposable>> bus = PublishSubject.create();

    public void registerForAnimation(ToggleDeviceAnimationProgress animationProgress, int progressBarWidth){
        listener = animationProgress;
        this.progressBarWidth = progressBarWidth;
    }

    public void setWaiting(String chipId, Disposable disposable, int position) {
        idPos.put(chipId, position);
        disposables.put(chipId,disposable);
        if (animatorMap.get(chipId) != null)
            animatorMap.get(chipId).cancel();
        ValueAnimator v = ValueAnimator.ofInt(0,progressBarWidth);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                listener.toggleAnimationProgressChanged((int)animation.getAnimatedValue(), position);
            }
        });
        v.setDuration(4000);
        v.setInterpolator(new AccelerateDecelerateInterpolator());
//        v.setStartDelay(500);
        v.start();
        animatorMap.put(chipId, v);
        bus.onNext(disposables);
    }

    public void removeWaiting(String chipId) {
        if (disposables.get(chipId) != null)
            disposables.get(chipId).dispose();
        if (animatorMap.get(chipId) != null && idPos.get(chipId) != null)
            listener.stopToggleAnimation(animatorMap.get(chipId), idPos.get(chipId), (int)animatorMap.get(chipId).getAnimatedValue(), progressBarWidth);
        animatorMap.remove(chipId);
        disposables.remove(chipId);
        bus.onNext(disposables);
    }

    public Observable<Map<String, Disposable>> toObservable() {
        return bus;
    }

    public boolean isWaiting(String chipId){
        return disposables.get(chipId) != null;
    }

    public void setDeviceLocallyAccessibility(String chipId, boolean accessibility){
        Device d = deviceDatabase.getDevice(chipId);
        d.setLocallyAccessibility(accessibility);
        deviceDatabase.updateDevice(d);
    }

    public void setDeviceOnlineAccessibility(String chipId, boolean accessibility){
        Device d = deviceDatabase.getDevice(chipId);
        d.setOnlineAccessibility(accessibility);
        deviceDatabase.updateDevice(d);
    }
}
