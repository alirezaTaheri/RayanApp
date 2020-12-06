package rayan.rayanapp.Util.RemoteHub.Verification;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;

public class UDPNodeDiscovery extends Utils{

    private final String TAG = "UDPNodeDiscovery";
    private List<String> authList = new ArrayList<>();

    private void discover(){
        for (int i = 0;i<AppConstants.SUPPORTED_TYPES.length;i++){
            switch (AppConstants.SUPPORTED_TYPES[i]){
                case AppConstants.BaseDeviceType_REMOTE_HUB:
                    remoteHub_v1_0(3);
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

    @SuppressLint("CheckResult")
    private void remoteHub_v1_0(long retry){
        String auth = getRandomAuth();
        authList.add(auth);
        Log.e(TAG, "Discovery For RemoteHub V1.0 | Sending Auth: " + auth);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConstants.AUTH_DISCOVER, auth);
        jsonObject.addProperty(AppConstants.CMD, AppConstants.NODE_DISCOVER);
        sendUDPObservable(RayanApplication.getPref().getLocalBroadcastAddress(), jsonObject.getAsString())
        .retry(retry).subscribeOn(Schedulers.io()).subscribe(s -> {});
    }

    private void switch_v1_0(){
    }
    private void switch_v2_0(){
    }

    private void resolve(BaseDevice baseDevice){

    }
}
