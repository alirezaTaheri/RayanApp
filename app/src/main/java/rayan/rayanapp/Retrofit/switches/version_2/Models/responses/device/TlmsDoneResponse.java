package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

public class TlmsDoneResponse extends DeviceBaseResponse {
    protected String name, stword, pin1, pin2;

    public String getName() {
        return name;
    }

    public String getStword() {
        return stword;
    }

    public String getPin1() {
        return pin1;
    }

    public String getPin2() {
        return pin2;
    }

    @Override
    public String toString() {
        return "TlmsDoneResponse{" +
                "name='" + name + '\'' +
                ", stword='" + stword + '\'' +
                ", pin1='" + pin1 + '\'' +
                ", pin2='" + pin2 + '\'' +
                '}';
    }
}
