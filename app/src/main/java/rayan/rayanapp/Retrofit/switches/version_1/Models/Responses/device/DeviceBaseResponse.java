package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

import rayan.rayanapp.Retrofit.switches.base.BaseResponse_Device;

public class DeviceBaseResponse extends BaseResponse_Device {
    protected String src;
    protected String cmd;

    public DeviceBaseResponse() {
    }

    public DeviceBaseResponse(String cmd) {
        this.cmd = cmd;
    }

    public String getSrc() {
        return src;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd() {
        return cmd;
    }

    @Override
    public String toString() {
        return "DeviceBaseResponse{" +
                "nid='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }

    @Override
    public String getOutput() {
        return cmd;
    }
}
