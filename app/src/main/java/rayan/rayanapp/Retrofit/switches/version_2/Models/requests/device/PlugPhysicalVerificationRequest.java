package rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device;

public class PlugPhysicalVerificationRequest extends BaseRequest{
    private int output_status;

    public PlugPhysicalVerificationRequest(int status) {
        this.output_status= status;
    }
}
