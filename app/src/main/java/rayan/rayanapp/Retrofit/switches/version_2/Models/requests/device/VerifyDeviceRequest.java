package rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Random;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class VerifyDeviceRequest extends BaseRequest{
    private String auth,STWORD;
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
        String s = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        Log.e("><><><", "Gson: " + s);
        return s;
    }

    @Override
    public String toString() {
        return "VerifyDeviceRequest{" +
                "auth='" + auth + '\'' +
                ", STWORD='" + STWORD + '\'' +
                ", sizeOfAuth=" + sizeOfAuth +
                '}';
    }

    public String getAuth() {
        return auth;
    }

    public String getSTWORD() {
        return STWORD;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
    }
}
