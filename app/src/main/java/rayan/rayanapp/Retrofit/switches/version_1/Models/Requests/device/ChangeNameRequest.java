package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;

public class ChangeNameRequest extends BaseRequest{
    private String name;
    private String stword;

    public ChangeNameRequest(String name, String stword) {
        this.name = name;
        setSrc(RayanApplication.getPref().getId());
        super.setCmd("CHANGE_HNAME");
        this.stword = stword;
    }

    public String ToString() {
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
