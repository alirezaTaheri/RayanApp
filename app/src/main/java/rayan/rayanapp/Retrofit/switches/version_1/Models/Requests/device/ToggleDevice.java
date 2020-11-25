package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;

public class ToggleDevice extends BaseRequest{
    private String stword;
    public ToggleDevice(String cmd, String stword) {
        this.setSrc(RayanApplication.getPref().getId());
        this.stword = stword;
        this.setCmd(cmd);
    }

//    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("stword", stword);
//        jsonObject.addProperty("cmd", getCmd());
//        jsonObject.addProperty("src", getSrc());
//        return jsonObject.toString();
//    }


    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }

    //    public ToggleDevice(String cmd, String stword,String lc) {
//        this.setCmd(cmd);
//        this.setSrc(RayanApplication.getPref().getId());
//        this.stword = stword;
//        this.lc = new ToggleDevice(lc, RayanApplication.getPref().getId(), 0);
//    }
//    public ToggleDevice(String lc, String src, int a ) {
//        this.setCmd(lc);
//        this.setSrc(src);
//    }
}
