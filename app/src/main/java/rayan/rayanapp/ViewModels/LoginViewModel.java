package rayan.rayanapp.ViewModels;

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

import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
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

    public LiveData<BaseResponse> login(String username, String password){
        loginObservable(username, password).subscribe(loginObserver(loginResponse));
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
}
