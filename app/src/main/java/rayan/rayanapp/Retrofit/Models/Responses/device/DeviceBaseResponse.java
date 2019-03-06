package rayan.rayanapp.Retrofit.Models.Responses.device;

public class DeviceBaseResponse {
    private String src;
    private String cmd;

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
}
