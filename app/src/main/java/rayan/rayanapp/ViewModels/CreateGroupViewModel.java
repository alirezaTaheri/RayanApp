package rayan.rayanapp.ViewModels;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;

public class CreateGroupViewModel extends DevicesFragmentViewModel {
    private final String TAG = CreateGroupFragment.class.getSimpleName();

    public CreateGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<BaseResponse> createGroup(String name, List<String> mobiles){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        createGroupObservable(new CreateGroupRequest(name, mobiles)).subscribe(createGroupObserver(results));
        return results;
    }

    private Observable<BaseResponse> createGroupObservable(CreateGroupRequest createGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createGroup(RayanApplication.getPref().getToken(), createGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> createGroupObserver(MutableLiveData<BaseResponse> results){
        return new DisposableObserver<BaseResponse>() {

            @Override
            public void onNext(@NonNull BaseResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                if (e.toString().contains("Unauthorized"))
                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }

}
