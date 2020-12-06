package rayan.rayanapp.Util.RemoteHub.Install;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubToGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.CreateTopicRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.DeleteRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubTopicRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PhysicalVerificationRequest_RemoteHub;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PrimaryConfigRequest_RemoteHub_v1;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Util.AppConstants;

class Observables {

    Observable<BaseResponse> deleteRemoteHubObservable(DeleteRemoteHubRequest deleteRemoteHubRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteRemoteHubFromGroup(RayanApplication.getPref().getToken(), deleteRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<BaseResponse> addRemoteHubToGroupObservable(AddRemoteHubToGroupRequest addRemoteHubToGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addRemoteHubToGroup(RayanApplication.getPref().getToken(), addRemoteHubToGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<RemoteHubResponse> editRemoteHubTopicObservable(EditRemoteHubTopicRequest editRemoteHubTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editRemoteHubTopic(RayanApplication.getPref().getToken(), editRemoteHubTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<RemoteHubResponse> createTopicRemoteHubObservable(CreateTopicRemoteHubRequest createTopicRemoteHubRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createTopicRemoteHub(RayanApplication.getPref().getToken(), createTopicRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<RemoteHubResponse> editRemoteHubObservable(EditRemoteHubRequest editRemoteHubRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editRemoteHub(RayanApplication.getPref().getToken(), editRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> connectToDeviceObservable(String currentSSID,Activity activity, WifiManager wifiManager, String password){
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        if (currentSSID.equals(AddNewDeviceActivity.getNewDevice().getAccessPointName()))
            return Observable.create(subscriber -> subscriber.onNext(AddNewDeviceActivity.getNewDevice().getAccessPointName()));
//        WifiHandler.connectToSSID(activity.getApplicationContext(),((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), password);
        WifiUtils.enableLog(true);
        Log.e("bLaH", "connecting to: " + AddNewDeviceActivity.getNewDevice().getAccessPointName() + password);
        WifiUtils.withContext(activity)
                .connectWith(AddNewDeviceActivity.getNewDevice().getAccessPointName(), password)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("isSuccessful" , "isisisisi: ");
                        Toast.makeText(activity, "به دستگاه متصل شد", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@androidx.annotation.NonNull ConnectionErrorCode errorCode) {
                        Log.e("NOTsUCCESSFULL" , "Error isisisisi: " + errorCode);
                        AddNewDeviceActivity.getNewDevice().setFailed(true);
                        Toast.makeText(activity, "خطا در اتصال به دستگاه", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();
    }


    Observable<RemoteHubBaseResponse> toDeviceFirstConfigObservable(PrimaryConfigRequest_RemoteHub_v1 configRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfig_RemoteHub_v1(AppConstants.getDeviceAddress(ip, AppConstants.PRIMARY_CONFIG), configRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RemoteHubBaseResponse> to_remoteHub_physical_verification_observable(ApiService apiService, PhysicalVerificationRequest_RemoteHub request){
        return apiService.to_remoteHub_physical_verification(AppConstants.getDeviceAddress(AppConstants.NEW_DEVICE_IP,AppConstants.NEW_DEVICE_PHYSICAL_VERIFICATION), request);
    }
}
