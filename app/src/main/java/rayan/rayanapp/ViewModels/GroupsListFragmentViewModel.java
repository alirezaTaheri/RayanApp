package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.Models.Requests.api.DeleteGroupRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;

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


    public Observable<BaseResponse> addDeviceToGroupObservable(AddDeviceToGroupRequest addDeviceToGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addDeviceToGroup(RayanApplication.getPref().getToken(), addDeviceToGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<BaseResponse> addDeviceToGroup(AddDeviceToGroupRequest addDeviceToGroupRequest){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        addDeviceToGroupObservable(addDeviceToGroupRequest).subscribe();
        return results;
    }

    private Observer<BaseResponse> addDeviceToGroupObserver(MutableLiveData<BaseResponse> results){
        return new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.e(TAG,"OnNext adding dobare..."+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Error adding dobare..."+e);
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed adding dobare...");
            }
        };
    }

    public Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<BaseResponse> deleteUser(DeleteUserRequest deleteUserRequest){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        deleteUserObservable(deleteUserRequest).subscribe(deleteUserObserver(results));
        return results;
    }

    private Observer<BaseResponse> deleteUserObserver(MutableLiveData<BaseResponse> results){
        return new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.e(TAG,"OnNext adding dobare..."+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Error adding dobare..."+e);
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed adding dobare...");
            }
        };
    }
}
