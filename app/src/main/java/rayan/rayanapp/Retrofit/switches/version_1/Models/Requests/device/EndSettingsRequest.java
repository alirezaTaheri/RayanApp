package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class EndSettingsRequest extends BaseRequest{
    private String stword;

    public EndSettingsRequest(String stword) {
        this.stword = stword;
        this.setCmd(AppConstants.END_SETTINGS);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "EndSettingsRequest{" +
                "stword='" + stword + '\'' +
                '}';
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
