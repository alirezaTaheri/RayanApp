package rayan.rayanapp.Helper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.LocallyChange;
import rayan.rayanapp.Persistance.database.LocallyChangesDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.DeviceResponse;

public class ControlRequests {
    LocallyChangesDatabase lcd;
    private final String TAG = this.getClass().getSimpleName();
    public ControlRequests(Context context) {
        lcd = new LocallyChangesDatabase(context);
    }
    public List<LocallyChange> getAllRequests(){
        return lcd.getAllRequest();
    }
    public List<LocallyChange> getAllRequests(LocallyChange.Type type){
        return lcd.getAllRequest(type);
    }

    public void addRequest(LocallyChange locallyChange){
        lcd.addRequest(locallyChange);
    }
     public void removeAll(){
        lcd.removeAll();
     }
    public void submitRequests(){
        List<LocallyChange> changes = getAllRequests();
        for (int a = 0; a<changes.size();a++){
            Log.e(TAG, "Applying this change: " + changes.get(a));
        }
    }

    public LiveData<DeviceResponse> editDevice(String id, String name, String type, String groupId, String ssid){
        final MutableLiveData<DeviceResponse> results = new MutableLiveData<>();
        editDeviceObservable(new EditDeviceTopicRequest(id, groupId, name, type, ssid)).subscribe(editDeviceObserver(results));
        return results;
    }
    private Observable<DeviceResponse> editDeviceObservable(EditDeviceTopicRequest editDeviceTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDeviceTopic(RayanApplication.getPref().getToken(), editDeviceTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private DisposableObserver<DeviceResponse> editDeviceObserver(MutableLiveData<DeviceResponse> results){
        return new DisposableObserver<DeviceResponse>() {

            @Override
            public void onNext(@NonNull DeviceResponse baseResponse) {
                Log.e(TAG,"OnNext "+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
//                if (e.toString().contains("Unauthorized"))
//                    login();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }
}
