package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device;

public class ChangeAccessPointResponse extends DeviceBaseResponse{
    private String name, STWORD,result,error;

    public String getName() {
        return name;
    }

    public String getSTWORD() {
        return STWORD;
    }

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    //    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("result", getResult());
//        jsonObject.addProperty("src", getSrc());
//        jsonObject.addProperty("STWORD", STWORD);
//        return jsonObject.toString();
//    }

//    public String ToString(){
//        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
//        Log.e("><><><", "Gson: " + s);
//        return s;
//    }


    @Override
    public String toString() {
        return "ChangeAccessPointResponse{" +
                "name='" + name + '\'' +
                ", STWORD='" + STWORD + '\'' +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", src='" + src + '\'' +
                ", result='" + cmd + '\'' +
                '}';
    }
}
