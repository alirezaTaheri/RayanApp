package rayan.rayanapp.Util.Switch.Install;

import android.app.Activity;
import android.net.wifi.WifiInfo;
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
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PrimaryConfigRequest_RemoteHub_v1;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.DeviceResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.DeviceBaseResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.AppConstants;

class Observables {

    Observable<rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.SetPrimaryConfigResponse> toDeviceFirstConfigObservableV1(rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.SetPrimaryConfigRequest setPrimaryConfigRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfigV1(AppConstants.getDeviceAddress(ip, AppConstants.PRIMARY_CONFIG), setPrimaryConfigRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    Observable<SetPrimaryConfigResponse> toDeviceFirstConfigObservableV2(SetPrimaryConfigRequest setPrimaryConfigRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfigV2(AppConstants.getDeviceAddress(ip, AppConstants.PRIMARY_CONFIG), setPrimaryConfigRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> connectToDeviceObservable(Activity activity, WifiManager wifiManager, String password){
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        if (getCurrentSSID(wifiManager).equals(AddNewDeviceActivity.getNewDevice().getAccessPointName()))
            return Observable.create(subscriber -> subscriber.onNext(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName()));
//        WifiHandler.connectToSSID(activity.getApplicationContext(),((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), password);
        WifiUtils.enableLog(true);
        WifiUtils.withContext(activity)
                .connectWith(((AddNewDeviceActivity)activity).getNewDevice().getAccessPointName(), password)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.e("isSuccessful" , "isisisisi: ");
                        Toast.makeText(activity, "به دستگاه متصل شد", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@androidx.annotation.NonNull ConnectionErrorCode errorCode) {
                        Log.e("NOTsUCCESSFULL" , "Error isisisisi: " + errorCode);
                        ((AddNewDeviceActivity) activity).getNewDevice().setFailed(true);
                        Toast.makeText(activity, "خطا در اتصال به دستگاه", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
        return ((RayanApplication)activity.getApplication()).getNetworkBus().toObservable();
    }

    Observable<BaseResponse> deleteUserObservable(DeleteDeviceRequest deleteDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteDeviceFromGroup(RayanApplication.getPref().getToken(), deleteDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    Observable<BaseResponse> addDeviceToGroupObservable(AddDeviceToGroupRequest addDeviceToGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addDeviceToGroup(RayanApplication.getPref().getToken(), addDeviceToGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<DeviceResponse> createTopicObservable(CreateTopicRequest createTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .createTopic(RayanApplication.getPref().getToken(), createTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<DeviceResponse> editDeviceObservable(EditDeviceRequest editDeviceRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDevice(RayanApplication.getPref().getToken(), editDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<DeviceResponse> editDeviceTopicObservable(EditDeviceTopicRequest editDeviceTopicRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .editDeviceTopic(RayanApplication.getPref().getToken(), editDeviceTopicRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Observable<SetPrimaryConfigResponse> toDeviceFirstConfigObservable(SetPrimaryConfigRequest configRequest, String ip){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .sendFirstConfigV2(AppConstants.getDeviceAddress(ip, AppConstants.PRIMARY_CONFIG), configRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    String getCurrentSSID(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }
}
