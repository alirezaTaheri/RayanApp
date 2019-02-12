package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.ChangePasswordRequest;
import rayan.rayanapp.Retrofit.Models.Requests.ForgetPasswordRequest;
import rayan.rayanapp.Retrofit.Models.Responses.BaseResponse;

public class ForgetPasswordViewModel extends DevicesFragmentViewModel {
    private final String TAG = ForgetPasswordViewModel.class.getSimpleName();
    public ForgetPasswordViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<BaseResponse> forgetPassword(String phone, String email) {
        MutableLiveData<BaseResponse> results = new MutableLiveData();
        forgetPasswordObservable(new ForgetPasswordRequest(phone, email)).subscribe(forgetPasswordObserver(results));
        return results;
    }

    private Observable<BaseResponse> forgetPasswordObservable(ForgetPasswordRequest forgetPasswordRequest) {
        return ApiUtils.getApiService().forgetPassword(RayanApplication.getPref().getToken(), forgetPasswordRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> forgetPasswordObserver(final MutableLiveData<BaseResponse> results) {
        return new DisposableObserver<BaseResponse>() {
            public void onNext(@NonNull BaseResponse baseResponse) {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("OnNext ");
                stringBuilder.append(baseResponse);
                Log.e(TAG, stringBuilder.toString());
                results.postValue(baseResponse);
            }

            public void onError(@NonNull Throwable e) {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error");
                stringBuilder.append(e);
                Log.d(TAG, stringBuilder.toString());
                e.printStackTrace();
                if (e.toString().contains("Unauthorized")) {
                    login();
                }
            }

            public void onComplete() {
                Log.d(TAG, "Completed");
            }
        };
    }
}
