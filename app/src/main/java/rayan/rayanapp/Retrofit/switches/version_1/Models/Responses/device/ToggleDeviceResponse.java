package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

import com.google.gson.JsonObject;

public class ToggleDeviceResponse extends DeviceBaseResponse{
    private String port1, port2, STWORD, msg, result,error;

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", cmd);
        jsonObject.addProperty("src", src);
        if (port1 != null)
        jsonObject.addProperty("port1", port1);
        if (port2 != null)
        jsonObject.addProperty("port2", port2);
        jsonObject.addProperty("STWORD", STWORD);
        return jsonObject.toString();
    }

    @Override
    public String toString() {
        return "ToggleDeviceResponse{" +
                "port1='" + port1 + '\'' +
                ", port2='" + port2 + '\'' +
                ", STWORD='" + STWORD + '\'' +
                ", msg='" + msg + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", result='" + cmd + '\'' +
                '}';
    }
}
