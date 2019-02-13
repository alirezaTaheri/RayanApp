package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.RegisterUserRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;

public class RegisterUserViewModel extends DevicesFragmentViewModel {
    private final String TAG = RegisterUserViewModel.class.getSimpleName();
    public RegisterUserViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<BaseResponse> registerUser(String username, String password, String email) {
        MutableLiveData<BaseResponse> results = new MutableLiveData();
        registerUserObservable(new RegisterUserRequest(username, password, email)).subscribe(registerUserObserver(results));
        return results;
    }

    private Observable<BaseResponse> registerUserObservable(RegisterUserRequest registerUserRequest) {
        return ApiUtils.getApiService().registerUser(RayanApplication.getPref().getToken(), registerUserRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse>  registerUserObserver(final MutableLiveData<BaseResponse> results) {
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
