package rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device;

public class UpdateDeviceRequest extends BaseRequest{
    private String code;

    public UpdateDeviceRequest(String cmd, String code) {
        this.code = code;
        super.setCmd(cmd);
    }
}
