package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.App.RayanApplication;

public class ToggleDevice extends BaseRequest{
    private String stword;

    public ToggleDevice(String cmd, String stword) {
        this.setSrc(RayanApplication.getPref().getId());
        this.stword = stword;
        this.setCmd(cmd);
    }
}
