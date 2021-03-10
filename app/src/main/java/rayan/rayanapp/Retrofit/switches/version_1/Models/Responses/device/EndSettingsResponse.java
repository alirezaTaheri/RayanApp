package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

import com.google.gson.JsonObject;

public class EndSettingsResponse extends DeviceBaseResponse{
    private String stword;

    public String getStword() {
        return stword;
    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("stword", stword);
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        return jsonObject.toString();
    }

    public String wrongToString(){
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
