package rayan.rayanapp.Util.RemoteHub.Verification;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;

public class Utils {

    public String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        return new String( org.apache.commons.net.util.Base64.encodeBase64(bytes) );
    }

    String getRandomAuth() {
        return generateRandom();
    }

    private String generateRandom(){
        final Random random=new Random();
        int sizeOfAuth = 16;
        final StringBuilder sb=new StringBuilder(sizeOfAuth);
        String ALLOWED_CHARACTERS = "0123456789asdfghjklqwertyuiopzxcvbnm";
        for(int i = 0; i< sizeOfAuth; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    Observable<String> sendUDPObservable(String ip, String msg){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                new SendUDPMessage().sendUdpMessage(ip, msg);
                Log.e("Sending UDP", msg + " | To: " + ip);
                emitter.onNext(AppConstants.SUCCESSFUL);
                emitter.onComplete();
            }
        });
    }
}
