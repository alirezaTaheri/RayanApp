package rayan.rayanapp.Persistance.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.LocallyChange;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.LocallyChangesDAO;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ChangeNameRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ChangeNameResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.DeviceBaseResponse;

public class LocallyChangesDatabase {
    private AppDatabase appDatabase;
    private LocallyChangesDAO locallyChangesDAO;
    public LocallyChangesDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        locallyChangesDAO = appDatabase.getLocallyChangesDAO();
    }

    public List<LocallyChange> getAllRequest(){
        return locallyChangesDAO.getAll();
    }

    public List<LocallyChange> getAllRequest(LocallyChange.Type type){
        return locallyChangesDAO.getAll(type.toString());
    }

    public void removeRequest(LocallyChange id){
        locallyChangesDAO.deleteItem(id);
    }

    public void addRequest(LocallyChange locallyChange){
        locallyChangesDAO.add(locallyChange);
    }

    @SuppressLint("CheckResult")
    public void submitRequests(){
        List<Observable<ChangeNameResponse>> o = getRequestsObservable(getAllRequest());
        Log.e("rrrrrrrrr" ,"rrrrrrrrrr: " + o);
        Log.e("rrrrrrrrr" ,"rrrrrrrrrr: " + o.size());
        Observable.zip(o, new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] objects) throws Exception {

                return new Object();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                Log.e("//////////" ,"/////////: " + o);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("//////////" ,"//////////" + e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }
    public void submitRequests(LocallyChange.Type type){

    }

    List<Observable<DeviceBaseResponse>> d ;
    public void requestSuccess(LocallyChange locallyChange){
        removeRequest(locallyChange);
        d.add(Observable.create(e -> {
            e.onNext(new ChangeNameResponse());
        }));
    }

    public List<Observable<ChangeNameResponse>> getRequestsObservable(List<LocallyChange> locallyChanges){
        List<Observable<ChangeNameResponse>> observables = new ArrayList<>();
        for (int a = 0; a<locallyChanges.size(); a++){
            switch (locallyChanges.get(a).getType()){
                case "NAME_API":
                    observables.add(ApiUtils.getApiService().changeName("http://192.168.1.105/test.php",
                            new GsonBuilder().create().fromJson(locallyChanges.get(a).getJsonRequest(), ChangeNameRequest.class)));
                    break;
                case "NAME_DEVICE":
//                    observables.add(ApiUtils.getApiService().changeName(locallyChanges.get(a).getIp(),
//                            new GsonBuilder().create().fromJson(locallyChanges.get(a).getJsonRequest(), ChangeNameRequest.class)));
                    break;
                case "POSITION":
                    break;
                case "FAVOURITE":
                    break;
            }
        }
    return observables;
    }
}
