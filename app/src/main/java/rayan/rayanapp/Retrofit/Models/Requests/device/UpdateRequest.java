package rayan.rayanapp.Retrofit.Models.Requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class UpdateRequest extends BaseRequest{
    private String stword;

    public UpdateRequest(String stword) {
        this.stword = stword;
        this.setCmd(AppConstants.DEVICE_IS_READY_FOR_UPDATE);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "stword='" + stword + '\'' +
                '}';
    }

    public String ToString(){
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }
}
