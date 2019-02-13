package rayan.rayanapp.RxBus;

import org.json.JSONObject;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class UDPMessageRxBus {
    public UDPMessageRxBus() {
    }

    private PublishSubject<JSONObject> bus = PublishSubject.create();

    public void send(JSONObject o) {
        bus.onNext(o);
    }

    public Observable<JSONObject> toObservable() {
        return bus;
    }
}
