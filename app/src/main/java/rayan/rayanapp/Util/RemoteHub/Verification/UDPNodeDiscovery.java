package rayan.rayanapp.Util.RemoteHub.Verification;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubData;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;

public class UDPNodeDiscovery extends Utils{

    private final String TAG = "UDPNodeDiscovery";
    private List<String> authList = new ArrayList<>();

    public void discover(){
        for (int i = 0;i<AppConstants.SUPPORTED_TYPES.length;i++){
            switch (AppConstants.SUPPORTED_TYPES[i]){
                case AppConstants.BaseDeviceType_REMOTE_HUB:
                    discover_remoteHub_v1_0(3);
                    break;
                case AppConstants.BaseDeviceType_SWITCH_1:
                    break;
                case AppConstants.BaseDeviceType_SWITCH_2:
                    break;
                case AppConstants.BaseDeviceType_TOUCH_2:
                    break;
                case AppConstants.BaseDeviceType_PLUG:
                    break;
            }
        }
    }

    private int remoteHub_send_count = 0;
    @SuppressLint("CheckResult")
    private void discover_remoteHub_v1_0(long repeat){
        String auth = getRandomAuth();
        authList.add(auth);
        Log.e(TAG, "Discovery For RemoteHub V1.0 | Sending Auth: " + auth);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConstants.AUTH_DISCOVER, auth);
        jsonObject.addProperty(AppConstants.CMD, AppConstants.NODE_DISCOVER);
        sendUDPObservable(RayanApplication.getPref().getLocalBroadcastAddress(), jsonObject.toString())
                .flatMap(s -> Observable.just(1).delay(1000*++remoteHub_send_count, TimeUnit.MILLISECONDS))
                .repeat(repeat).subscribeOn(Schedulers.io()).subscribe(s -> {});
    }

    private void switch_v1_0(){
    }
    private void switch_v2_0(){
    }

    public void resolve(BaseDevice baseDevice, JSONObject msg, String ip, RemoteHubDatabase remoteHubDatabase, DeviceDatabase deviceDatabase){
        Log.e(TAG, "Resolving Initiated");
        switch(baseDevice.getDeviceType()){
            case AppConstants.BaseDeviceType_REMOTE_HUB:
                boolean result = resolve_remoteHub_v1_0((RemoteHub) baseDevice, msg,ip, remoteHubDatabase);
                Log.e(TAG, "Resolving Result: " + result);
                break;
                default:
                    Log.e(TAG, "Base Device Type is Unknown");
        }
    }

    private boolean resolve_remoteHub_v1_0(RemoteHub remoteHub, JSONObject msg, String ip, RemoteHubDatabase remoteHubDatabase){
        try {
            String auth = Encryptor.decrypt(msg.getString(AppConstants.AUTH_DISCOVER),remoteHub.getSecret());
            if (authList.contains(auth)){
                Log.e(TAG, "Auth is Correct");
                remoteHub.setIp(ip);
                remoteHub.setVerified(true);
                remoteHub.setSsid(msg.getString(AppConstants.SSID));
                remoteHub.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(msg.getString(AppConstants.STWORD),remoteHub.getSecret()).split("#")[1])+1));
                remoteHub.setHeader(Encryptor.decrypt(msg.getString(AppConstants.STWORD_HEADER),remoteHub.getSecret()).split("#")[0]);
                remoteHubDatabase.updateRemoteHub(remoteHub);
                return true;
            }else Log.e(TAG, "Auth is not what Expected to be");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
