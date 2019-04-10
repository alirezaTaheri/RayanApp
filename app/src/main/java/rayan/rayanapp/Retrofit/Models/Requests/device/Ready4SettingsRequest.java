package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class Ready4SettingsRequest extends BaseRequest{
    private String stword;

    public Ready4SettingsRequest(String stword) {
        this.stword = stword;
        this.setCmd(AppConstants.SETTINGS);
        this.setSrc(RayanApplication.getPref().getId());
    }

    @Override
    public String toString() {
        return "Ready4SettingsRequest{" +
                "stword='" + stword + '\'' +
                '}';
    }
}
