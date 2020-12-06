package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;

public class ToggleDevice extends BaseRequest{
    private String STWORD;
    private int port1, port2;
    public ToggleDevice(int port1, int port2, String STWORD) {
        this.setSrc(RayanApplication.getPref().getId());
        this.STWORD = STWORD;
        this.port1 = port1;
        this.port2 = port2;
    }

//    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("STWORD", STWORD);
//        jsonObject.addProperty("result", getResult());
//        jsonObject.addProperty("src", getSrc());
//        return jsonObject.toString();
//    }


    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }

    @Override
    public String toString() {
        return "ToggleDevice{" +
                "STWORD='" + STWORD + '\'' +
                ", port1=" + port1 +
                ", port2=" + port2 +
                '}';
    }

    //    public ToggleDevice(String result, String STWORD,String lc) {
//        this.setResult(result);
//        this.setSrc(RayanApplication.getPref().getId());
//        this.STWORD = STWORD;
//        this.lc = new ToggleDevice(lc, RayanApplication.getPref().getId(), 0);
//    }
//    public ToggleDevice(String lc, String src, int a ) {
//        this.setResult(lc);
//        this.setSrc(src);
//    }
}
