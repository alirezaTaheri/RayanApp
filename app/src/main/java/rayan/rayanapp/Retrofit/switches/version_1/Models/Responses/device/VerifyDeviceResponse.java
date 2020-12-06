package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

public class VerifyDeviceResponse extends DeviceBaseResponse{
    private String port1, port2, STWORD,name,auth,result,type,style,ssid,error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSTWORD() {
        return STWORD;
    }

    public String getPort1() {
        return port1;
    }

    public void setPort1(String port1) {
        this.port1 = port1;
    }

    public String getPort2() {
        return port2;
    }

    public void setPort2(String port2) {
        this.port2 = port2;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "VerifyDeviceResponse{" +
                "port1='" + port1 + '\'' +
                ", port2='" + port2 + '\'' +
                ", STWORD='" + STWORD + '\'' +
                ", name='" + name + '\'' +
                ", auth='" + auth + '\'' +
                ", result='" + result + '\'' +
                ", type='" + type + '\'' +
                ", style='" + style + '\'' +
                ", ssid='" + ssid + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", result='" + cmd + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getAuth() {
        return auth;
    }
}
