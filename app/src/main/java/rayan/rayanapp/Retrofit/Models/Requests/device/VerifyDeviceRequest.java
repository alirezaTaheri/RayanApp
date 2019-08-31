package rayan.rayanapp.Retrofit.Models.Requests.device;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import org.json.JSONObject;

import java.util.Random;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class VerifyDeviceRequest extends BaseRequest{
    private String auth;
    @Expose(serialize = false, deserialize = false)
    private transient String ALLOWED_CHARACTERS = "0123456789asdfghjklqwertyuiopzxcvbnm";
    transient private int sizeOfAuth = 16;
    public VerifyDeviceRequest() {
        this.setSrc(RayanApplication.getPref().getId());
        this.setCmd(AppConstants.TO_DEVICE_VERIFY);
        this.auth = generateRandom();
    }

    private String generateRandom(){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfAuth);
        for(int i=0;i<sizeOfAuth;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public String ToString(){
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("auth",auth);
//        jsonObject.addProperty("cmd", getCmd());
//        jsonObject.addProperty("src", getSrc());
//        return jsonObject.toString();
        Gson gson = new Gson();
        return gson.toJson(this, VerifyDeviceRequest.class);
    }

    @Override
    public String toString() {
        return "VerifyDeviceRequest{" +
                "auth='" + auth + '\'' +
                ", ALLOWED_CHARACTERS='" + ALLOWED_CHARACTERS + '\'' +
                ", sizeOfAuth=" + sizeOfAuth +
                '}';
    }

    public String getAuth() {
        return auth;
    }
}
