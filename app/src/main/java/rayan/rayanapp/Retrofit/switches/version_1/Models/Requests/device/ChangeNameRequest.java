package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;

public class ChangeNameRequest extends BaseRequest{
    private String node_name;
    private String STWORD;

    public ChangeNameRequest(String name, String stword) {
        this.node_name = name;
        setSrc(RayanApplication.getPref().getId());
        super.setCmd("CHANGE_HNAME");
        this.STWORD = stword;
    }

    public String ToString() {
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
