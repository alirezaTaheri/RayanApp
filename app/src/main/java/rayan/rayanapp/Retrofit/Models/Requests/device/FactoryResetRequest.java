package rayan.rayanapp.Retrofit.Models.Requests.device;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class FactoryResetRequest extends BaseRequest{
    private String stword;

    public FactoryResetRequest(String stword) {
        this.stword = stword;
        this.setCmd(AppConstants.FACTORY_RESET);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "Ready4SettingsRequest{" +
                "stword='" + stword + '\'' +
                '}';
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
