package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;

public class PhysicalVerificationRequest extends BaseRequest{
    private String output_status;

    public PhysicalVerificationRequest(String output_status) {
        this.setSrc(RayanApplication.getPref().getId());
        this.output_status= output_status;
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }

}
