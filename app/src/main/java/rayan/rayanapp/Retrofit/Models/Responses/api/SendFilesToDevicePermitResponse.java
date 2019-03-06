package rayan.rayanapp.Retrofit.Models.Responses.api;

public class SendFilesToDevicePermitResponse {

    String permit;

    public SendFilesToDevicePermitResponse(String permit) {
        this.permit = permit;
    }
    public String getPermit() {
        return permit;
    }
}
