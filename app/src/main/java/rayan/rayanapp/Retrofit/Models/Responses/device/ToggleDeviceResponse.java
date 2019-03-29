package rayan.rayanapp.Retrofit.Models.Responses.device;

public class ToggleDeviceResponse extends DeviceBaseResponse{
    private String sid, pin1, pin2,stword;


    public String getSid() {
        return sid;
    }

    public String getStword() {
        return stword;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPin1() {
        return pin1;
    }

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public String getPin2() {
        return pin2;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    @Override
    public String toString() {
        return "ToggleDeviceResponse{" +
                "sid='" + sid + '\'' +
                ", pin1='" + pin1 + '\'' +
                ", cmd='" + getCmd() + '\'' +
                ", src='" + getSrc() + '\'' +
                ", pin2='" + pin2 + '\'' +
                ", stword='" + stword + '\'' +
                '}';
    }
}
