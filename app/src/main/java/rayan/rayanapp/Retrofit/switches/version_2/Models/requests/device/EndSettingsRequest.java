package rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class EndSettingsRequest extends BaseRequest{
    private String STWORD;

    public EndSettingsRequest(String stword) {
        this.STWORD = stword;
        this.setCmd(AppConstants.END_SETTINGS);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "EndSettingsRequest{" +
                "STWORD='" + STWORD + '\'' +
                '}';
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
