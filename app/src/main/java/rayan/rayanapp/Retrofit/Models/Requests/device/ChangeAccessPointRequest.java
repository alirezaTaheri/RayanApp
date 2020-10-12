package rayan.rayanapp.Retrofit.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.Util.AppConstants;

public class ChangeAccessPointRequest extends BaseRequest{
    private String hname, ap_pass, ap_ssid,style, STWORD;

    public ChangeAccessPointRequest(String hname, String pwd, String ssid, String style, String stword) {
        this.hname = hname;
        this.ap_pass = pwd;
        this.ap_ssid = ssid;
        this.style = style;
        this.setCmd(AppConstants.CHANGE_WIFI);
        this.STWORD = stword;
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
