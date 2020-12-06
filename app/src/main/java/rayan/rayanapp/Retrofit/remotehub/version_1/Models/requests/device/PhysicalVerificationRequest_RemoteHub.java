package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.BaseRequest;

public class PhysicalVerificationRequest_RemoteHub extends BaseRequest{

    public PhysicalVerificationRequest_RemoteHub() {
        this.setSrc(RayanApplication.getPref().getId());
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }

}
