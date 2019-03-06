package rayan.rayanapp.Retrofit.Models.Requests.device;

public class PlugPhysicalVerificationRequest extends BaseRequest{
    private String status;

    public PlugPhysicalVerificationRequest(String status) {
        this.status = status;
        super.setCmd("phv");
    }
}
