package rayan.rayanapp.Retrofit.Models.Responses.device;

import com.google.gson.JsonObject;

public class FactoryResetResponse extends DeviceBaseResponse{
    private String STWORD, result,error;

    public String getSTWORD() {
        return STWORD;
    }


    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("STWORD", STWORD);
        return jsonObject.toString();
    }
//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }

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

    @Override
    public String toString() {
        return "FactoryResetResponse{" +
                "STWORD='" + STWORD + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
