package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;

public class DevicesManagementActivityViewModel extends DevicesFragmentViewModel {
    private final String TAG = DevicesManagementActivityViewModel.class.getSimpleName();
    SendUDPMessage sendUDPMessage = new SendUDPMessage();
    public DevicesManagementActivityViewModel(@NonNull Application application) {
        super(application);

    }
    @SuppressLint("CheckResult")
    public void setReadyForSettings(String ip){
        Observable.intervalRange(0, 3, 0,500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("//////////", "onSubscribe" + d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        sendUDPMessage.sendUdpMessage(ip, AppConstants.SETTINGS);
                        Log.e("//////////", "////onNext//" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("//////////", "/onError/////" + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("//////////", "//onComplete////" );
                    }
                });
    }

}

