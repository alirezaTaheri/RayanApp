package rayan.rayanapp.Retrofit.Models.Requests.device;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
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
