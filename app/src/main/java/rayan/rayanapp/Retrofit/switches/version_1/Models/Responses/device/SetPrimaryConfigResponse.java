package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

public class SetPrimaryConfigResponse extends DeviceBaseResponse{
    private String count,result;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getCount() {
        count = result.split(":")[1].trim();
        return count;
    }
}
