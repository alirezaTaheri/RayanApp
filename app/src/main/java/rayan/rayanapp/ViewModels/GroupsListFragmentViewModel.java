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

}
