package rayan.rayanapp.Retrofit.Models.Responses.device;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.internal.http2.Header;

public class ToggleDeviceResponse extends DeviceBaseResponse{
    private String sid, pin1, pin2,stword, msg;


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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", cmd);
        jsonObject.addProperty("src", src);
        if (pin1 != null)
        jsonObject.addProperty("pin1", pin1);
        if (pin2 != null)
        jsonObject.addProperty("pin2", pin2);
        jsonObject.addProperty("stword", stword);
        if (sid != null)
        jsonObject.addProperty("sid", sid);
        return jsonObject.toString();
    }
    @Override
    public String toString() {
        return "ToggleDeviceResponse{" +
                "sid='" + sid + '\'' +
                ", pin1='" + pin1 + '\'' +
                ", pin2='" + pin2 + '\'' +
                ", stword='" + stword + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
