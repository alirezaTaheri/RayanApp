package rayan.rayanapp.Retrofit.Models.Requests.device;

import com.google.gson.Gson;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class FactoryResetRequest extends BaseRequest{
    private String stword;

    public FactoryResetRequest(String stword) {
        this.stword = stword;
        this.setCmd(AppConstants.FACTORY_RESET);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "Ready4SettingsRequest{" +
                "stword='" + stword + '\'' +
                '}';
    }

    public String ToString(){
        return new Gson().toJson(this, FactoryResetRequest.class);
    }
}
