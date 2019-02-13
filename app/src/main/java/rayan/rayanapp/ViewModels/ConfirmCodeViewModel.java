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
import rayan.rayanapp.Retrofit.Models.Requests.ConfirmCodeRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;

public class ConfirmCodeViewModel extends DevicesFragmentViewModel {
    private final String TAG = ConfirmCodeViewModel.class.getSimpleName();
    public ConfirmCodeViewModel(@NonNull Application application) {
        super(application);
    }
    public LiveData<BaseResponse> confirmCode(String code) {
        MutableLiveData<BaseResponse> results = new MutableLiveData();
        confirmCodeObservable(new ConfirmCodeRequest(code)).subscribe(confirmCodeObserver(results));
        return results;
    }

    private Observable<BaseResponse> confirmCodeObservable(ConfirmCodeRequest confirmCodeRequest) {
        Log.e(TAG+ "request",RayanApplication.getPref().getToken()+"--------"+ confirmCodeRequest);
        return ApiUtils.getApiService().confirmCode(RayanApplication.getPref().getToken(), confirmCodeRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> confirmCodeObserver(final MutableLiveData<BaseResponse> results) {
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
