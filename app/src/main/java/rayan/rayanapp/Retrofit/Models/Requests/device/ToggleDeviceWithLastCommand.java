package rayan.rayanapp.Retrofit.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rayan.rayanapp.App.RayanApplication;

public class ToggleDeviceWithLastCommand extends BaseRequest{
    private String stword;
    private JsonObject lc;

    public ToggleDeviceWithLastCommand(String cmd, String stword, String lc_cmd) {
        this.setSrc(RayanApplication.getPref().getId());
        this.stword = stword;
        lc = new JsonObject();
        lc.addProperty("cmd", lc_cmd);
        this.setCmd(cmd);
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}