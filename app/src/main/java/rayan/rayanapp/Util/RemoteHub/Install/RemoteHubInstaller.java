package rayan.rayanapp.Util.RemoteHub.Install;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubToGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.CreateTopicRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.DeleteRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubTopicRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PhysicalVerificationRequest_RemoteHub;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PrimaryConfigRequest_RemoteHub_v1;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;

public class RemoteHubInstaller extends Observables{

    private final String TAG = "RemoteHubInstaller";
    private Disposable installFlowDisposable,sendConfigDisposable;

    public Pair<Disposable,SingleLiveEvent<RemoteHubBaseResponse>> install(
                        RemoteHubDatabase remoteHubDatabase,
                        WifiManager wifiManager,
                        AddNewDeviceActivity activity,
                        NewDevice newDevice,
                        String ip){
        switch (newDevice.getVersion()){
            case "1.0":
                return install_1_0(remoteHubDatabase,
                        wifiManager,
                        activity,
                        newDevice,
                        ip);
                default:
                    Log.e(TAG, "Unknown Version Found: " + newDevice.getVersion()+" For NewDevice: "+newDevice);
                    break;
        }
        return null;
    }

    public Pair<Disposable,SingleLiveEvent<String>> resend_config(
            NewDevice newDevice,
            String ip){
        switch (newDevice.getVersion()){
            case "1.0":
                return resend_config_1_0(
                        newDevice,
                        ip);
            default:
                Log.e(TAG, "Unknown Version Found: " + newDevice.getVersion()+" For NewDevice: "+newDevice);
                break;
        }
        return null;
    }

    private Pair<Disposable,SingleLiveEvent<RemoteHubBaseResponse>> install_1_0(RemoteHubDatabase remoteHubDatabase,
                                                                                                WifiManager wifiManager,
                                                                                                AddNewDeviceActivity activity,
                                                                                                NewDevice newDevice,
                                                                                                String ip){
        SingleLiveEvent<RemoteHubBaseResponse> result = new SingleLiveEvent<>();
        newDevice.setUsername(newDevice.getChip_id());
        AddRemoteHubRequest addRemoteHubRequest = new AddRemoteHubRequest(newDevice);
        Log.e(TAG, "Installing RemoteHub V1: " + newDevice);
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .addRemoteHub(RayanApplication.getPref().getToken(),addRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(response-> {
                    Log.e(TAG, "Device Registration Passed\nRemoving Device from previous group..."+response);
                    RemoteHub d = response.getData().getRemoteHub();
                    newDevice.setId(d.getId());
                    newDevice.setUsername(d.getUsername());
                    newDevice.setPassword(d.getPassword());
                    RemoteHub existingRemoteHub = remoteHubDatabase.getRemoteHub(newDevice.getChip_id());
                    if (existingRemoteHub != null){
                        Log.e(TAG, "Deleting Device From previous Group: " + existingRemoteHub);
                        newDevice.setPreGroupId(existingRemoteHub.getGroupId());

                        return deleteRemoteHubObservable(new DeleteRemoteHubRequest(existingRemoteHub.getId(), existingRemoteHub.getGroupId()));
                    }
                    else {
                        Log.e(TAG, "There is no such device to delete from group ");
                        return Observable.just(1);
                    }
                })
                .flatMap(deviceResponse ->{
                    Log.e(TAG, "Device Registration Passed\nAdding Device To new Group...");
                    return addRemoteHubToGroupObservable(new AddRemoteHubToGroupRequest(newDevice.getId(), newDevice.getGroup().getId()));
                })
                .flatMap(baseResponse -> editRemoteHubTopicObservable(new EditRemoteHubTopicRequest(newDevice.getId(), newDevice.getGroup().getId(), newDevice.getName(), newDevice.getType(), newDevice.getSsid())))
                .flatMap(deviceResponse -> {
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage().equals(AppConstants.FORBIDDEN)){
                        Toast.makeText(activity, "شما قادر به نصب این دستگاه نیستید", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Action is forbidden... Operation will fail");
                        return null;
                    }
                    Log.e(TAG, "Device Specification successfully Changed...");
                    return createTopicRemoteHubObservable(new CreateTopicRemoteHubRequest(newDevice.getId(), newDevice.getGroup().getId(), newDevice.getChip_id(), AppConstants.MQTT_HOST));
                })
                .flatMap(baseResponse -> editRemoteHubObservable(new EditRemoteHubRequest(newDevice.getName(),
                        newDevice.getVersion(),newDevice.getSsid(),null,
                        newDevice.getId())))
                .flatMap(deviceBaseResponse ->{
                    Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
                    newDevice.setTopic(deviceBaseResponse.getData().getRemoteHub().getTopic());
                    byte[] data = newDevice.getName().getBytes();
                    String baseName = Base64.encodeToString(data, Base64.DEFAULT);
                    newDevice.setName(baseName);
                    String secret;
                    Log.e(TAG, "New Device Chip ID: " + newDevice.getChip_id());
                    Log.e(TAG, "Found Device is: " + remoteHubDatabase.getRemoteHub(newDevice.getChip_id()));
                    RemoteHub oldRemoteHub = remoteHubDatabase.getRemoteHub(newDevice.getChip_id());
                    Log.e(TAG, "OLD RemoteHub is: " + oldRemoteHub);
                    if (oldRemoteHub != null){
                        secret = oldRemoteHub.getSecret();
                        Log.e(TAG, secret + "A device with this chip id is already saved on device and password will be: " + (newDevice.getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret));
                        return connectToDeviceObservable(getCurrentSSID(wifiManager),activity, wifiManager, newDevice.getStatus().equals(NewDevice.NodeStatus.NEW)? AppConstants.DEVICE_PRIMARY_PASSWORD : secret);
                    }
                    Log.e(TAG, "this device never registerd in this device before password will be:  " + AppConstants.DEVICE_PRIMARY_PASSWORD);
                    return connectToDeviceObservable(getCurrentSSID(wifiManager),activity, wifiManager, AppConstants.DEVICE_PRIMARY_PASSWORD);
                })
                .flatMap(s -> {
                    Log.e(TAG,"Ok Now we will wait for n seconds"+s);
                    return Observable.timer(8, TimeUnit.SECONDS);})
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(deviceResponse -> {
                    Toast.makeText(activity, "ارسال اطلاعات به دستگاه", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ok Response of connection is here: "+deviceResponse + newDevice);
                    return toDeviceFirstConfigObservable(new PrimaryConfigRequest_RemoteHub_v1(newDevice.getSsid(), newDevice.getPwd(), newDevice.getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), String.valueOf(AppConstants.MQTT_PORT_TCP), newDevice.getTopic().getTopic(), newDevice.getUsername(), newDevice.getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, newDevice.getGroup().getSecret()), ip);
                })
                .subscribe(new Observer<RemoteHubBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        installFlowDisposable = d;
                    }

                    @Override
                    public void onNext(RemoteHubBaseResponse deviceBaseResponse) {
                        Log.e(TAG, "OnNext::::" + deviceBaseResponse);
                        result.postValue(deviceBaseResponse);
                        installFlowDisposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Flow of install onError:" + e);
                        RemoteHubBaseResponse errorResponse = new RemoteHubBaseResponse();
                        if (e instanceof SocketTimeoutException){
                            errorResponse.setResult(AppConstants.SOCKET_TIME_OUT);
                        }
                        else if (e instanceof UnknownHostException){
                            errorResponse.setResult(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        }
                        else if (e instanceof ConnectException)
                            errorResponse.setResult(AppConstants.CONNECT_EXCEPTION);
                        else{
                            errorResponse.setResult(AppConstants.UNKNOWN_EXCEPTION);
                        }
                        e.printStackTrace();
                        result.postValue(errorResponse);
                        installFlowDisposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::" );
                    }
                });
        return new Pair<>(installFlowDisposable, result);
    }

    private Pair<Disposable,SingleLiveEvent<String>> resend_config_1_0(NewDevice newDevice, String ip){
        SingleLiveEvent<String> result = new SingleLiveEvent<>();
        toDeviceFirstConfigObservable(new PrimaryConfigRequest_RemoteHub_v1(newDevice.getSsid(), newDevice.getPwd(), newDevice.getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), String.valueOf(AppConstants.MQTT_PORT_TCP), newDevice.getTopic().getTopic(), newDevice.getUsername(), newDevice.getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, newDevice.getGroup().getSecret()), ip)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RemoteHubBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        sendConfigDisposable = d;
                    }

                    @Override
                    public void onNext(RemoteHubBaseResponse response) {
                        String res = response.getResult()==null?"":response.getResult();
                        String error = response.getError()==null?"":response.getError();
                        result.postValue(res+error);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error Resending Config: " + e + " Message: " + e.getMessage());
                        if (e instanceof SocketTimeoutException) result.postValue(AppConstants.SOCKET_TIME_OUT);
                        else if (e instanceof ConnectException) result.postValue(AppConstants.CONNECT_EXCEPTION);
                        else if (e instanceof UnknownHostException) result.postValue(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        else result.postValue(AppConstants.UNKNOWN_EXCEPTION);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return new Pair<>(sendConfigDisposable, result);
    }

    public SingleLiveEvent<String> to_remoteHub_physical_verification(){
        SingleLiveEvent<String> result = new SingleLiveEvent<>();
        ApiService apiService = ApiUtils.getApiService();
        to_remoteHub_physical_verification_observable(apiService, new PhysicalVerificationRequest_RemoteHub()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<RemoteHubBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RemoteHubBaseResponse remoteHubBaseResponse) {
                        String res = remoteHubBaseResponse.getResult()==null?"":remoteHubBaseResponse.getResult();
                        String error = remoteHubBaseResponse.getError()==null?"":remoteHubBaseResponse.getError();
                        result.postValue(res+error);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error Verifying: " + e + " Message: " + e.getMessage());
                        if (e instanceof SocketTimeoutException) result.postValue(AppConstants.SOCKET_TIME_OUT);
                        else if (e instanceof ConnectException) result.postValue(AppConstants.CONNECT_EXCEPTION);
                        else if (e instanceof UnknownHostException) result.postValue(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        else result.postValue(AppConstants.UNKNOWN_EXCEPTION);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        return result;
    }
    private String getCurrentSSID(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }
}
