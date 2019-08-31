package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class RetryConnectMqtt {

    private final String TAG = "RetryConnectMqtt";
    private boolean running;
    Disposable d;
    MainActivity mainActivity;
    public int count = 0;
    public RetryConnectMqtt(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void start(){
        running = true;
        d = Observable.interval(0, 2, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "Try To Connect...");
                        if (aLong > 5) rest();
                        count++;
                        mainActivity.connectToMqtt();
                    }
                });
    }

    public void rest(){
        count = 0;
        d.dispose();
        running = false;
        start();
//        mainActivity.mqttReallyDisconnected();
    }

    public void reset(){

    }
    public void stop(){
        count = 0;
        if (d!=null)
            d.dispose();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
