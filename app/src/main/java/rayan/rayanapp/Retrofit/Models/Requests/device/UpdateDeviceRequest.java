package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class UpdateDeviceRequest extends BaseRequest{
    private String code;

    public UpdateDeviceRequest(String cmd, String code) {
        this.code = code;
        super.setCmd(cmd);
    }
}
