package rayan.rayanapp.Retrofit.Models.Responses.device;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ChangeNameResponse extends DeviceBaseResponse{
    private String name;
    private String stword;
    public String getName() {
        return name;
    }

    public String getStword() {
        return stword;
    }


    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("stword", stword);
        jsonObject.addProperty("name", name);
        return jsonObject.toString();
    }

//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }
}
