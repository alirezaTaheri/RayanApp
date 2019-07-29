package org.eclipse.paho.android.service;

import android.content.Intent;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MqttEventBus {
    public MqttEventBus() {
    }

    private PublishSubject<Intent> bus = PublishSubject.create();

    public void send(Intent intent) {
        bus.onNext(intent);
    }

    public Observable<Intent> toObservable() {
        return bus;
    }
}
