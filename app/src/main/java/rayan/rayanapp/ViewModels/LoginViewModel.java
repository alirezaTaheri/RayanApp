package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.api.Data;
import rayan.rayanapp.Util.AppConstants;

public class LoginViewModel extends ViewModel {
    private final String TAG = LoginViewModel.class.getSimpleName();

//<<<<<<< HEAD
    public final MutableLiveData<BaseResponse> loginResponse = new MutableLiveData<>();
//=======
//    private MutableLiveData<BaseResponse> loginResponse = new MutableLiveData<>();
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
    public boolean isConnected(Context context){
        ConnectivityManager CManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        return NInfo != null && NInfo.isConnectedOrConnecting();
    }

    @SuppressLint("CheckResult")
    public LiveData<BaseResponse> login(String username, String password, DialogPresenter dp){
        internetProvided().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean)
                    loginObservable(username, password).subscribe(loginObserver(loginResponse));
                else {
                    dp.showDialog(AppConstants.DIALOG_PROVIDE_INTERNET, new ArrayList<>());
                    Log.e(TAG, "Internet is not connected");
                }
            }
        });

        return loginResponse;
    }

    private Observable<BaseResponse> loginObservable(String username, String password){
        return ApiUtils.getApiService()
                .login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> loginObserver(final MutableLiveData<BaseResponse> loginResponse){
        return new DisposableObserver<BaseResponse>() {
            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);
//<<<<<<< HEAD
                    loginResponse.setValue(baseResponse);
//=======
//                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
//                loginResponse.setValue(baseResponse);
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                if (e instanceof SocketTimeoutException){
                    BaseResponse b = new BaseResponse();
                    Data d = new Data();
                    d.setMessage(AppConstants.SOCKET_TIME_OUT);
                    b.setData(d);
                    loginResponse.postValue(b);
                }
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }
    public LiveData<BaseResponse> getLoginResponse(){
        return loginResponse;
    }

    public Single<Boolean> internetProvided(){
        return Single.fromCallable(() -> {
            try {
                Socket sock = new Socket();
                SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
                sock.connect(address, 1000);
                sock.close();
                return true;
            }catch (IOException e){
                return false;
            }
        }).timeout(3500, TimeUnit.MILLISECONDS);
    }
}
