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
import rayan.rayanapp.Retrofit.Models.Requests.EditUserRequest;
import rayan.rayanapp.Retrofit.Models.Responses.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Responses.UserInfo;

public class EditUserViewModel extends DevicesFragmentViewModel {
    private final String TAG = EditUserViewModel.class.getSimpleName();

    public EditUserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<BaseResponse> editUser(String name, String gender) {
        MutableLiveData<BaseResponse> results = new MutableLiveData();
        //inja alan pass ro pas midim.dar ayande gender pas dade khahad shod
        editUserObservable(new EditUserRequest(new UserInfo(name),gender)).subscribe(editUserObserver(results));
        return results;
    }

    private Observable<BaseResponse> editUserObservable(EditUserRequest EditUserRequest) {
        return ApiUtils.getApiService().editUser(RayanApplication.getPref().getToken(), EditUserRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> editUserObserver(final MutableLiveData<BaseResponse> results) {
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