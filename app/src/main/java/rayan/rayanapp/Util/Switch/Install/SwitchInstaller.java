package rayan.rayanapp.Util.Switch.Install;

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
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteHubToGroupRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.CreateTopicRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.DeleteRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteHubTopicRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device.PrimaryConfigRequest_RemoteHub_v1;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device.RemoteHubBaseResponse;
import rayan.rayanapp.Retrofit.switches.base.BaseResponse_Device;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.CreateTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.EditDeviceTopicRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.SetPrimaryConfigRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device.DeviceBaseResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.SingleLiveEvent;

public class SwitchInstaller extends Observables {

    private final String TAG = "SwitchInstaller";
    private Disposable installFlowDisposable;

    public Pair<Disposable, SingleLiveEvent<BaseResponse_Device>> install(WifiManager wifiManager,
                                                                          AddNewDeviceActivity activity,
                                                                          DeviceDatabase deviceDatabase,
                                                                          RegisterDeviceRequest registerDeviceRequest,
                                                                          String ip) {
        switch (AddNewDeviceActivity.getNewDevice().getVersion()) {
            case "1.0":
                return installVersion_1_0(deviceDatabase,
                        wifiManager,
                        activity,
                        registerDeviceRequest,
                        ip);
            case "2.0":
                return installVersion_2_0(deviceDatabase,
                        wifiManager,
                        activity,
                        AddNewDeviceActivity.getNewDevice(),
                        registerDeviceRequest,
                        ip);
            default:
                Log.e(TAG, "Unknown Version Found: " + AddNewDeviceActivity.getNewDevice().getVersion() + " For Device: " + AddNewDeviceActivity.getNewDevice());
                break;
        }
        return null;
    }

    private Pair<Disposable, SingleLiveEvent<BaseResponse_Device>> installVersion_1_0(DeviceDatabase deviceDatabase,
                                                                         WifiManager wifiManager,
                                                                         AddNewDeviceActivity activity,
                                                                         RegisterDeviceRequest registerDeviceRequest,
                                                                         String ip) {
        SingleLiveEvent<BaseResponse_Device> result = new SingleLiveEvent<>();
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .registerDevice(RayanApplication.getPref().getToken(), registerDeviceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(deviceResponse -> {
                    Log.e(TAG, "Device Registration Passed\nRemoving Device from previous group...");
                    Device d = deviceResponse.getData().getDevice();
                    AddNewDeviceActivity.getNewDevice().setId(d.getId());
                    AddNewDeviceActivity.getNewDevice().setUsername(d.getUsername());
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage() != null && deviceResponse.getData().getMessage().equals(AppConstants.DUPLICATE_USER))
                        AddNewDeviceActivity.getNewDevice().setPassword(d.getDevicePassword());
                    else
                        AddNewDeviceActivity.getNewDevice().setPassword(d.getPassword());
                    Device existingDevice = deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id());
                    if (existingDevice != null) {
                        Log.e(TAG, "Deleting Device From previous Group: " + existingDevice);
                        AddNewDeviceActivity.getNewDevice().setPreGroupId(existingDevice.getGroupId());

                        return deleteUserObservable(new DeleteDeviceRequest(existingDevice.getId(), existingDevice.getGroupId()));
                    } else {
                        Log.e(TAG, "There is no such device to delete from group ");
                        return Observable.just(1);
                    }
                })
                .flatMap(deviceResponse -> {
                    Log.e(TAG, "Device Registration Passed\nAdding Device To new Group...");
                    return addDeviceToGroupObservable(new AddDeviceToGroupRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId()));
                })
                .flatMap(baseResponse -> editDeviceObservable(new EditDeviceRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId(), AddNewDeviceActivity.getNewDevice().getName(), AddNewDeviceActivity.getNewDevice().getType(), AddNewDeviceActivity.getNewDevice().getSsid())))
                .flatMap(deviceResponse -> {
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage().equals(AppConstants.FORBIDDEN)) {
                        Toast.makeText(activity, "شما قادر به نصب این دستگاه نیستید", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Action is forbidden... Operation will fail");
                        return null;
                    }
                    Log.e(TAG, "Device Specification successfully Changed...");
                    return createTopicObservable(new CreateTopicRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId(), AddNewDeviceActivity.getNewDevice().getChip_id(), AppConstants.MQTT_HOST));
                })
                .flatMap(deviceBaseResponse -> {
                    Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
                    AddNewDeviceActivity.getNewDevice().setTopic(deviceBaseResponse.getData().getDevice().getTopic());
                    byte[] data = AddNewDeviceActivity.getNewDevice().getName().getBytes();
                    String baseName = Base64.encodeToString(data, Base64.DEFAULT);
                    AddNewDeviceActivity.getNewDevice().setName(baseName);
                    String secret;
                    if (deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id()) != null) {
                        secret = deviceDatabase.getDevice(AddNewDeviceActivity.getNewDevice().getChip_id()).getSecret();
                        Log.e(TAG, secret + "A device with this chip id is already saved on device and password will be: " + (AddNewDeviceActivity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW) ? AppConstants.DEVICE_PRIMARY_PASSWORD : secret));
                        return connectToDeviceObservable(activity, wifiManager, AddNewDeviceActivity.getNewDevice().getStatus().equals(NewDevice.NodeStatus.NEW) ? AppConstants.DEVICE_PRIMARY_PASSWORD : secret);
                    }
                    Log.e(TAG, "this device never registerd in this device before password will be:  " + AppConstants.DEVICE_PRIMARY_PASSWORD);
                    return connectToDeviceObservable(activity, wifiManager, AppConstants.DEVICE_PRIMARY_PASSWORD);
                })
                .flatMap(s -> {
                    Log.e(TAG, "Ok Now we will wait for n seconds" + s);
                    return Observable.timer(8, TimeUnit.SECONDS);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(deviceResponse -> {
                    Toast.makeText(activity, "ارسال اطلاعات به دستگاه", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ok Response of connection is here: " + deviceResponse);
                    return toDeviceFirstConfigObservableV1(new SetPrimaryConfigRequest(AddNewDeviceActivity.getNewDevice().getSsid(), AddNewDeviceActivity.getNewDevice().getPwd(), AddNewDeviceActivity.getNewDevice().getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), AddNewDeviceActivity.getNewDevice().getTopic().getTopic(), AddNewDeviceActivity.getNewDevice().getUsername(), AddNewDeviceActivity.getNewDevice().getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, AddNewDeviceActivity.getNewDevice().getGroup().getSecret()), ip);
                })
                .subscribe(new Observer<SetPrimaryConfigResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        installFlowDisposable = d;
                    }

                    @Override
                    public void onNext(SetPrimaryConfigResponse setPrimaryConfigResponse) {
                        Log.e(TAG, "OnNext::::" + setPrimaryConfigResponse);
                        result.postValue(new rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.DeviceBaseResponse(setPrimaryConfigResponse.getCmd()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Flow of install onError::::" + e);
                        rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.SetPrimaryConfigResponse errorResponse = new SetPrimaryConfigResponse();
                        if (e instanceof SocketTimeoutException) {
                            errorResponse.setCmd(AppConstants.SOCKET_TIME_OUT);
                        } else if (e instanceof UnknownHostException) {
                            errorResponse.setCmd(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        } else if (e instanceof ConnectException)
                            errorResponse.setCmd(AppConstants.CONNECT_EXCEPTION);
                        else {
                            errorResponse.setCmd(AppConstants.UNKNOWN_EXCEPTION);
                        }
                        e.printStackTrace();
                        result.postValue(new rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.DeviceBaseResponse(errorResponse.getCmd()));
                        installFlowDisposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::");
                    }
                });
        return new Pair<>(installFlowDisposable, result);
    }

    private Pair<Disposable, SingleLiveEvent<BaseResponse_Device>> installVersion_2_0(DeviceDatabase deviceDatabase,
                                                                                        WifiManager wifiManager,
                                                                                        AddNewDeviceActivity activity,
                                                                                        NewDevice newDevice,
                                                                                        RegisterDeviceRequest registerDeviceRequest,
                                                                                        String ip) {
        SingleLiveEvent<BaseResponse_Device> result = new SingleLiveEvent<>();
        newDevice.setUsername(newDevice.getChip_id());
        AddRemoteHubRequest addRemoteHubRequest = new AddRemoteHubRequest(newDevice);
        Log.e(TAG, "Installing RemoteHub V1: " + newDevice);
        ApiService apiService = ApiUtils.getApiService();
        apiService
                .addRemoteHub(RayanApplication.getPref().getToken(), addRemoteHubRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(response -> {
                    Log.e(TAG, "Device Registration Passed\nRemoving Device from previous group..." + response);
                    RemoteHub d = response.getData().getRemoteHub();
                    newDevice.setId(d.getId());
                    newDevice.setUsername(d.getUsername());
                    newDevice.setPassword(d.getPassword());
                    Device existingRemoteHub = deviceDatabase.getDevice(newDevice.getChip_id());
                    if (existingRemoteHub != null) {
                        Log.e(TAG, "Deleting Device From previous Group: " + existingRemoteHub);
                        newDevice.setPreGroupId(existingRemoteHub.getGroupId());

                        return deleteUserObservable(new DeleteDeviceRequest(existingRemoteHub.getId(), existingRemoteHub.getGroupId()));
                    } else {
                        Log.e(TAG, "There is no such device to delete from group ");
                        return Observable.just(1);
                    }
                })
                .flatMap(deviceResponse -> {
                    Log.e(TAG, "Device Registration Passed\nAdding Device To new Group...");
                    return addDeviceToGroupObservable(new AddDeviceToGroupRequest(newDevice.getId(), newDevice.getGroup().getId()));
                })
                .flatMap(baseResponse -> editDeviceTopicObservable(new EditDeviceTopicRequest(newDevice.getId(), newDevice.getGroup().getId(), newDevice.getName(), newDevice.getType(), newDevice.getSsid())))
                .flatMap(deviceResponse -> {
                    if (deviceResponse.getStatus().getDescription().equals(AppConstants.ERROR) && deviceResponse.getData().getMessage().equals(AppConstants.FORBIDDEN)) {
                        Toast.makeText(activity, "شما قادر به نصب این دستگاه نیستید", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Action is forbidden... Operation will fail");
                        return null;
                    }
                    Log.e(TAG, "Device Specification successfully Changed...");
                    return createTopicObservable(new CreateTopicRequest(newDevice.getId(), newDevice.getGroup().getId(), newDevice.getChip_id(), AppConstants.MQTT_HOST));
                })
                .flatMap(baseResponse -> editDeviceTopicObservable(new EditDeviceTopicRequest(newDevice.getName(),
                        newDevice.getVersion(), newDevice.getSsid(), null,
                        newDevice.getId())))
                .flatMap(deviceBaseResponse -> {
                    Log.e(TAG, "Topic Creation Passed\nConnecting to device...");
                    newDevice.setTopic(deviceBaseResponse.getData().getDevice().getTopic());
                    byte[] data = newDevice.getName().getBytes();
                    String baseName = Base64.encodeToString(data, Base64.DEFAULT);
                    newDevice.setName(baseName);
                    String secret;
                    Log.e(TAG, "New Device Chip ID: " + newDevice.getChip_id());
                    Log.e(TAG, "Found Device is: " + deviceDatabase.getDevice(newDevice.getChip_id()));
                    Device oldRemoteHub = deviceDatabase.getDevice(newDevice.getChip_id());
                    Log.e(TAG, "OLD RemoteHub is: " + oldRemoteHub);
                    if (oldRemoteHub != null) {
                        secret = oldRemoteHub.getSecret();
                        Log.e(TAG, secret + "A device with this chip id is already saved on device and password will be: " + (newDevice.getStatus().equals(NewDevice.NodeStatus.NEW) ? AppConstants.DEVICE_PRIMARY_PASSWORD : secret));
                        return connectToDeviceObservable(activity, wifiManager, newDevice.getStatus().equals(NewDevice.NodeStatus.NEW) ? AppConstants.DEVICE_PRIMARY_PASSWORD : secret);
                    }
                    Log.e(TAG, "this device never registerd in this device before password will be:  " + AppConstants.DEVICE_PRIMARY_PASSWORD);
                    return connectToDeviceObservable(activity, wifiManager, AppConstants.DEVICE_PRIMARY_PASSWORD);
                })
                .flatMap(s -> {
                    Log.e(TAG, "Ok Now we will wait for n seconds" + s);
                    return Observable.timer(8, TimeUnit.SECONDS);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(deviceResponse -> {
                    Toast.makeText(activity, "ارسال اطلاعات به دستگاه", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ok Response of connection is here: " + deviceResponse + newDevice);
                    return toDeviceFirstConfigObservable(new rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.SetPrimaryConfigRequest(newDevice.getSsid(), newDevice.getPwd(), newDevice.getName(), AppConstants.MQTT_HOST, String.valueOf(AppConstants.MQTT_PORT_SSL), String.valueOf(AppConstants.MQTT_PORT_TCP), newDevice.getTopic().getTopic(), newDevice.getUsername(), newDevice.getPassword(), AppConstants.DEVICE_CONNECTED_STYLE, newDevice.getGroup().getSecret()), ip);
                })
                .subscribe(new Observer<DeviceBaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        installFlowDisposable = d;
                    }

                    @Override
                    public void onNext(DeviceBaseResponse deviceBaseResponse) {
                        Log.e(TAG, "OnNext::::" + deviceBaseResponse);
                        result.postValue(deviceBaseResponse);
                        installFlowDisposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Flow of install onError:" + e);
                        DeviceBaseResponse errorResponse = new DeviceBaseResponse();
                        if (e instanceof SocketTimeoutException) {
                            errorResponse.setCmd(AppConstants.SOCKET_TIME_OUT);
                        } else if (e instanceof UnknownHostException) {
                            errorResponse.setCmd(AppConstants.UNKNOWN_HOST_EXCEPTION);
                        } else if (e instanceof ConnectException)
                            errorResponse.setCmd(AppConstants.CONNECT_EXCEPTION);
                        else {
                            errorResponse.setCmd(AppConstants.UNKNOWN_EXCEPTION);
                        }
                        e.printStackTrace();
                        result.postValue(errorResponse);
                        installFlowDisposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete::::");
                    }
                });
        return new Pair<>(installFlowDisposable, result);
    }
}
