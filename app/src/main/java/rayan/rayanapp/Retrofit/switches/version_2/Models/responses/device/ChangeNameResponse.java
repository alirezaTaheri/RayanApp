package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

import com.google.gson.JsonObject;

public class ChangeNameResponse extends DeviceBaseResponse{
    private String name;
    private String STWORD, result, error;
    public String getName() {
        return name;
    }

    public String getSTWORD() {
        return STWORD;
    }

    public String ToString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", getCmd());
        jsonObject.addProperty("src", getSrc());
        jsonObject.addProperty("STWORD", STWORD);
        jsonObject.addProperty("name", name);
        return jsonObject.toString();
    }

//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }


    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "ChangeNameResponse{" +
                "name='" + name + '\'' +
                ", STWORD='" + STWORD + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", result='" + cmd + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
