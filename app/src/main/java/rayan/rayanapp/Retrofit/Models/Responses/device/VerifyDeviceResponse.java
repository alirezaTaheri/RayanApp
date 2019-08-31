package rayan.rayanapp.Retrofit.Models.Responses.device;

import com.google.gson.JsonObject;

public class VerifyDeviceResponse extends DeviceBaseResponse{
    private String pin1,pin2,stword,name,auth;


    public String getStword() {
        return stword;
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
        return "VerifyDeviceResponse{" +
                "pin1='" + pin1 + '\'' +
                ", pin2='" + pin2 + '\'' +
                ", stword='" + stword + '\'' +
                ", name='" + name + '\'' +
                ", auth='" + auth + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getAuth() {
        return auth;
    }
}
