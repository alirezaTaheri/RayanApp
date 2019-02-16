package rayan.rayanapp.RxBus;

import android.net.wifi.ScanResult;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class WifiScanResultsBus {
    public WifiScanResultsBus() {
    }

    private PublishSubject<List<ScanResult>> bus = PublishSubject.create();

    public void send(List<ScanResult> o) {
        bus.onNext(o);
    }

    public Observable<List<ScanResult>> toObservable() {
        return bus;
    }
}
