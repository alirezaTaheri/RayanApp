package rayan.rayanapp.Util.Switch;

import android.net.wifi.WifiManager;
import android.util.Log;

import io.reactivex.disposables.Disposable;
import rayan.rayanapp.Activities.AddNewDeviceActivity;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.RegisterDeviceRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.SetPrimaryConfigResponse;
import rayan.rayanapp.Util.SingleLiveEvent;

public class SwitchInstaller {

    private final String TAG = "SwitchInstaller";
    private Disposable installFlowDisposable;

    public SingleLiveEvent<SetPrimaryConfigResponse> install(Device device,
                                                             DeviceDatabase deviceDatabase,
                                                             WifiManager wifiManager,
                                                             AddNewDeviceActivity activity,
                                                             RegisterDeviceRequest registerDeviceRequest,
                                                             NewDevice newDevice,
                                                             String ip){
        switch (device.getVersion()){
            case "1.0":
                return installVersion_1_0(deviceDatabase,
                        wifiManager,
                        activity,
                        registerDeviceRequest,
                        newDevice,
                        ip);
            case "2.0":
                return installVersion_2_0(deviceDatabase,
                    wifiManager,
                    activity,
                    registerDeviceRequest,
                    newDevice,
                    ip);
            default:
                Log.e(TAG, "Unknown Version Found: " + device.getVersion()+" For Device: "+device);
                break;
        }
        return null;
    }

    private SingleLiveEvent<SetPrimaryConfigResponse> installVersion_1_0(DeviceDatabase remoteHubDatabase,
                                                                         WifiManager wifiManager,
                                                                         AddNewDeviceActivity activity,
                                                                         RegisterDeviceRequest registerDeviceRequest,
                                                                         NewDevice newDevice,
                                                                         String ip){
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        return result;
    }

    private SingleLiveEvent<SetPrimaryConfigResponse> installVersion_2_0(DeviceDatabase remoteHubDatabase,
                                                                         WifiManager wifiManager,
                                                                         AddNewDeviceActivity activity,
                                                                         RegisterDeviceRequest registerDeviceRequest,
                                                                         NewDevice newDevice,
                                                                         String ip){
        SingleLiveEvent<SetPrimaryConfigResponse> result = new SingleLiveEvent<>();
        return result;
    }
}
