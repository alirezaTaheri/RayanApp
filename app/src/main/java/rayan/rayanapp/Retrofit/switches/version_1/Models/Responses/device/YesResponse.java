package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

public class YesResponse extends TlmsDoneResponse {
    private String type,ssid,style;

    public String getType() {
        return type;
    }

    public String getSsid() {
        return ssid;
    }

    public String getStyle() {
        return style;
    }

    @Override
    public String toString() {
        return "YesResponse{" +
                "type='" + type + '\'' +
                ", ssid='" + ssid + '\'' +
                ", style='" + style + '\'' +
                ", name='" + name + '\'' +
                ", stword='" + stword + '\'' +
                ", pin1='" + pin1 + '\'' +
                ", pin2='" + pin2 + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
