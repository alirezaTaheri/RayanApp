package rayan.rayanapp.RxBus;

import android.util.Log;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class NetworkConnectionBus {
    public NetworkConnectionBus() {
    }

    private PublishSubject<String> bus = PublishSubject.create();

    public void send(String o) {
        Log.e("NetworkConnectionBus","A change in network is reported..."+o);
        bus.onNext(o);
    }

    public Observable<String> toObservable() {
        return bus;
    }
}
