package rayan.rayanapp.Retrofit.Models.Responses.device;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Ready4SettingsResponse extends DeviceBaseResponse{
    private String stword;

    public String getStword() {
        return stword;
    }

    public Ready4SettingsResponse(String cmd, String stword) {
        super(cmd);
        this.stword = stword;
    }

    @Override
    public String toString() {
        return "Ready4SettingsResponse{" +
                "stword='" + stword + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }


//    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("cmd", getCmd());
//        jsonObject.addProperty("src", getSrc());
//        jsonObject.addProperty("stword", stword);
//        return jsonObject.toString();
//    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("stword", stword);
        return jsonObject.toString();
    }
//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }
}
