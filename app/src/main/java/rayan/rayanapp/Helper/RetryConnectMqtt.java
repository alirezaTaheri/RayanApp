package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class RetryConnectMqtt {

    private boolean running;
    Disposable d;
    MainActivity mainActivity;
    public RetryConnectMqtt(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void start(){
        running = true;
        Observable.interval(0,2, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RetryConnectMqtt.this.d = d;
                        Log.e("???????????", "subscribed");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("???????????", "onNext " + aLong);
                        if (aLong > 10 || RayanApplication.getPref().getProtocol().equals(AppConstants.MQTT))
                            stop();
                        else if (!RayanApplication.getPref().getProtocol().equals(AppConstants.MQTT))mainActivity.onRetryMqtt();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("???????????", "onError " + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("???????????", "onComplete");
                    }
                });
    }
    public void stop(){
        d.dispose();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
