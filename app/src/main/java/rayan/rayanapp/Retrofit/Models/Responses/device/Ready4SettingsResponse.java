package rayan.rayanapp.Retrofit.Models.Responses.device;

import com.google.gson.JsonObject;

public class Ready4SettingsResponse extends DeviceBaseResponse{
    private String STWORD, port1,port2,result,error;

    public String getSTWORD() {
        return STWORD;
    }

    public Ready4SettingsResponse(String cmd, String stword) {
        super(cmd);
        this.STWORD = stword;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
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

    //    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("cmd", getCmd());
//        jsonObject.addProperty("src", getSrc());
//        jsonObject.addProperty("STWORD", STWORD);
//        return jsonObject.toString();
//    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("STWORD", STWORD);
        return jsonObject.toString();
    }

    @Override
    public String toString() {
        return "Ready4SettingsResponse{" +
                "STWORD='" + STWORD + '\'' +
                ", port1='" + port1 + '\'' +
                ", port2='" + port2 + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }

    //    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }
}
