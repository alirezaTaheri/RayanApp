package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

public class PlugPhysicalVerificationRequest extends BaseRequest{
    private int output_status;

    public PlugPhysicalVerificationRequest(int status) {
        this.output_status= status;
    }
}
