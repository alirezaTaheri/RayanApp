package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

import rayan.rayanapp.Retrofit.switches.base.BaseResponse_Device;

public class DeviceBaseResponse extends BaseResponse_Device {
    protected String src;
    protected String result;

    public DeviceBaseResponse() {
    }

    public DeviceBaseResponse(String cmd) {
        this.result = cmd;
    }

    public String getSrc() {
        return src;
    }

    public void setCmd(String cmd) {
        this.result = cmd;
    }

    public String getCmd() {
        return result;
    }

    @Override
    public String toString() {
        return "DeviceBaseResponse{" +
                "nid='" + src + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    @Override
    public String getOutput() {
        return result;
    }
}
