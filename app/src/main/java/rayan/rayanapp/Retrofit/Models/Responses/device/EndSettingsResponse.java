package rayan.rayanapp.Retrofit.Models.Responses.device;

import com.google.gson.JsonObject;

public class EndSettingsResponse extends DeviceBaseResponse{
    private String STWORD, result, error;

    public String getSTWORD() {
        return STWORD;
    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("STWORD", STWORD);
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        return jsonObject.toString();
    }

    public String wrongToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("STWORD", STWORD);
        return jsonObject.toString();
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
    //    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }


    @Override
    public String toString() {
        return "EndSettingsResponse{" +
                "STWORD='" + STWORD + '\'' +
                ", result='" + result + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
