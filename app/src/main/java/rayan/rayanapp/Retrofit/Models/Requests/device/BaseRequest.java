package rayan.rayanapp.Retrofit.Models.Requests.device;

public class BaseRequest {
    private String cmd,src;
    public BaseRequest(String cmd) {
        this.cmd = cmd;
    }

    public BaseRequest() {
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
