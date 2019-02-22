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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;

public class LoginViewModel extends ViewModel {
    private final String TAG = LoginViewModel.class.getSimpleName();
    private final MutableLiveData<BaseResponse> loginResponse = new MutableLiveData<>();
    public boolean isConnected(Context context){
        ConnectivityManager CManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        return NInfo != null && NInfo.isConnectedOrConnecting();
    }

    public void login(String username, String password){
        loginObservable(username, password).subscribe(loginObserver());
    }

    private Observable<BaseResponse> loginObservable(String username, String password){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> loginObserver(){
        return new DisposableObserver<BaseResponse>() {
            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.d(TAG,"OnNext "+baseResponse);
                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
                    loginResponse.setValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }
    public void snackBarSetup(View view, String text){
        Snackbar mSnackBar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)mSnackBar.getView();
        layout.setPadding(0, 0, 0, 0);
        layout.setMinimumWidth(view.getWidth());
        TextView mainTextView = (mSnackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mSnackBar.show();
    }
    public LiveData<BaseResponse> getLoginResponse(){
        return loginResponse;
    }
}
