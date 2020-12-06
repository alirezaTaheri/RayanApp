package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class Ready4SettingsRequest extends BaseRequest{
    private String STWORD;

    public Ready4SettingsRequest(String stword) {
        this.STWORD = stword;
        this.setCmd(AppConstants.SETTINGS);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "Ready4SettingsRequest{" +
                "STWORD='" + STWORD + '\'' +
                '}';
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
