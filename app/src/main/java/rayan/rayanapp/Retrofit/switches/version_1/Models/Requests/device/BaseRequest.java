package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import rayan.rayanapp.App.RayanApplication;

public class BaseRequest {
    private String cmd,src;
    public BaseRequest(String cmd) {
        this.cmd = cmd;
        this.src = RayanApplication.getPref().getId();
    }

    public BaseRequest() {
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
