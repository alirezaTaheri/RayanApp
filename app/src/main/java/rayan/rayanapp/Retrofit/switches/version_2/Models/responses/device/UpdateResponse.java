package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

import com.google.gson.JsonObject;

public class UpdateResponse extends DeviceBaseResponse{
    private String stword;

    public String getStword() {
        return stword;
    }


//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("stword", stword);
        return jsonObject.toString();
    }
}
