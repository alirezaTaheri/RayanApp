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

    private final String TAG = "RetryConnectMqtt";
    private boolean running;
    Disposable d;
    MainActivity mainActivity;
    public int retry = 0;
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
                        Log.e(TAG, "subscribed");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "onNext " + aLong);
                        if (aLong > 10 || RayanApplication.getPref().getProtocol().equals(AppConstants.MQTT))
                            stop();
                        else if (!RayanApplication.getPref().getProtocol().equals(AppConstants.MQTT)){
                            mainActivity.onRetryMqtt();
                            retry++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError " + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }
    public void stop(){
        d.dispose();
        retry = 0;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
