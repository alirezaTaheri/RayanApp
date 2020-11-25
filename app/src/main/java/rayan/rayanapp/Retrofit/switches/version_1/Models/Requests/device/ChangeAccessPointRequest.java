package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.Util.AppConstants;

public class ChangeAccessPointRequest extends BaseRequest{
    private String hname,pwd,ssid,style, stword;

    public ChangeAccessPointRequest(String hname, String pwd, String ssid, String style, String stword) {
        this.hname = hname;
        this.pwd = pwd;
        this.ssid = ssid;
        this.style = style;
        this.setCmd(AppConstants.CHANGE_WIFI);
        this.stword = stword;
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
