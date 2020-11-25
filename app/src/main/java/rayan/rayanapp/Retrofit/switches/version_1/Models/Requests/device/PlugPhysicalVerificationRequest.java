package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

public class PlugPhysicalVerificationRequest extends BaseRequest{
    private String status;

    public PlugPhysicalVerificationRequest(String status) {
        this.status = status;
        super.setCmd("phv");
    }
}
