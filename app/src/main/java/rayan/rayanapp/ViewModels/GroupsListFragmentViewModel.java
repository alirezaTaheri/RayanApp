package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

public class GroupsListFragmentViewModel extends DevicesFragmentViewModel {

    private final String TAG = GroupsListFragmentViewModel.class.getSimpleName();
    private GroupDatabase groupDatabase;
    public GroupsListFragmentViewModel(@NonNull Application application) {
        super(application);
        groupDatabase = new GroupDatabase(application);
    }
    public LiveData<List<Group>> getAllGroupsLive(){
        return groupDatabase.getAllGroupsLive();
    }

    public List<Group> getAllGroups(){
        return groupDatabase.getAllGroups();
    }


    public LiveData<BaseResponse> deleteGroup(String groupId){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        deleteGroupObservable(new DeleteGroupRequest(groupId)).subscribe(deleteGroupObserver(results));
        return results;
    }

    private Observable<BaseResponse> deleteGroupObservable(DeleteGroupRequest deleteGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteGroup(RayanApplication.getPref().getToken(), deleteGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<BaseResponse> deleteGroupObserver(MutableLiveData<BaseResponse> results){
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
