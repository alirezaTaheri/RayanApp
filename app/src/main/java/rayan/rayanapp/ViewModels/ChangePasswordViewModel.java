package rayan.rayanapp.ViewModels;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.ChangePasswordRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;


public class ChangePasswordViewModel extends DevicesFragmentViewModel {
    private final String TAG = ChangePasswordViewModel.class.getSimpleName();

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<BaseResponse> changePassword(String currentPass, String newPass, String newPassReapet) {
        MutableLiveData<BaseResponse> results = new MutableLiveData();
        changePasswordObservable(new ChangePasswordRequest(currentPass, newPass, newPassReapet)).subscribe(changePasswordObserver(results));
        return results;
    }

    private Observable<BaseResponse> changePasswordObservable(ChangePasswordRequest changePasswordRequest) {
        return ApiUtils.getApiService().changePassword(RayanApplication.getPref().getToken(), changePasswordRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> changePasswordObserver(final MutableLiveData<BaseResponse> results) {
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