package rayan.rayanapp.Services.udp;

/**
 * Created by alireza321 on 19/12/2018.
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.AES;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.SendMessageToDevice;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.VerifyDeviceRequest;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Retrofit.Models.Responses.device.VerifyDeviceResponse;
import rayan.rayanapp.Util.AppConstants;
import retrofit2.Response;
import se.simbio.encryption.Encryption;

public class UDPServerService extends Service {
    private AsyncTask<Void, Void, Void> async;
    private boolean Server_active = true;
    public boolean isRunnig = false;
    private AppDatabase dataBase;
    private DeviceDatabase deviceDatabase;
    private int port;
    private ApiService apiService;

    private final String TAG = UDPServerService.class.getSimpleName();

    @SuppressLint({"NewApi", "StaticFieldLeak"})
    public void runUdpServer() {
        port = AppConstants.UDP_RECEIVE_PORT;
        byte[] lMsg = new byte[4096];
        DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
        DatagramSocket ds = null;
        dataBase = AppDatabase.getInstance(getApplicationContext());
        deviceDatabase = new DeviceDatabase(this);
        try{
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            while(Server_active){
                ds.receive(dp);
                String message = new String(lMsg, 0, dp.getLength());
                isRunnig = ds.isConnected();
                Log.e(TAG,"UDP Message "+message + "Address of Received" + dp.getAddress().toString());
                String senderIP = dp.getAddress().toString();
                senderIP = senderIP.replace("/","");
                if (message.contains("\r\n")) {
                    Log.e(TAG, "Message Has Acceptable Structure");
                    String[] msg = message.split("\r\n");
                    String auth = msg[0].split(":")[1];
                    Log.e(TAG, "Auth Is: " + auth);
                    if (isJSONValid(msg[1])) {
                        JSONObject jsonMessage = new JSONObject(msg[1]);
                        String src = jsonMessage.getString("src");
                        Device device = deviceDatabase.getDevice(src);
                        if (device != null) {
                            Log.e(TAG, "Computed HMAC is: " + sha1(jsonMessage.toString(), device.getSecret()));
                            if (sha1(msg[1], device.getSecret()).equals(auth)) {
                                Log.e(TAG, "Hmacs Are Equal");
                                byte[] decodedName;
                                String cmd = jsonMessage.getString("cmd");
                                String pin1, pin2, name, ssid, style, type, statusWord;
                                ((RayanApplication) getApplication()).getBus().send(jsonMessage);
                                switch (cmd) {
                                    case "tgl":
                                        statusWord = jsonMessage.getString("stword");
                                        pin1 = jsonMessage.getString("pin1");
                                        pin2 = jsonMessage.getString("pin2");
                                        device = deviceDatabase.getDevice(src);
                                        if (device == null)
                                            Log.e(TAG, "Couldn't find Device with this ChipId: " + src);
                                        else if (statusWord != null && device.getStatusWord() != null && Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) > Integer.parseInt(device.getStatusWord())) {
                                            Log.e(TAG, "message Stword: " + Encryptor.decrypt(statusWord, device.getSecret()));
                                            Log.e(TAG, "Device Status word: " + device.getStatusWord());
                                            ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
                                            device.setPin1(pin1);
                                            device.setPin2(pin2);
                                            device.setIp(senderIP);
                                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                            device.setHeader(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]);
                                            deviceDatabase.updateDevice(device);
                                        }
                                        else if (statusWord != null && device.getStatusWord() == null) {
                                            Log.e(TAG, "Device status word was null but now it's not");
                                            Log.e(TAG, "message Stword: " + Encryptor.decrypt(statusWord, device.getSecret()));
                                            Log.e(TAG, "Device Status word: " + device.getStatusWord());
                                            ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
                                            device.setPin1(pin1);
                                            device.setPin2(pin2);
                                            device.setIp(senderIP);
                                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                            device.setHeader(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]);
                                            deviceDatabase.updateDevice(device);
                                        }
                                        else if (device.getStatusWord() != null && statusWord != null || device.getStatusWord() != null && statusWord != null && Integer.parseInt(Encryptor.decrypt(String.valueOf(Encryptor.decrypt(statusWord, device.getSecret().split("#")[0])), device.getSecret())) < Integer.parseInt(device.getStatusWord())) {
                                            Log.e(TAG, "Status word verification failed because of something");
                                            Log.e(TAG, "Going to Verify Device...");
                                            verifyDevice(device, senderIP);

                                        }else if (statusWord == null){
                                            Log.e(TAG, "Sent Status word is empty and we can not execute anything...");
                                        }
                                        break;
                                    case "TLMSDONE":
                                        Log.d(TAG, "TLMSDONE message Received");
                                        Log.d(TAG, "TLMSDONE");
                                        pin1 = jsonMessage.getString("pin1");
                                        pin2 = jsonMessage.getString("pin2");
                                        name = jsonMessage.getString("name");
                                        statusWord = null;
                                        if (jsonMessage.has("stword")) {
                                            statusWord = jsonMessage.getString("stword");
                                        } else Log.e(TAG, "There is no stword");
                                        decodedName = Base64.decode(name, Base64.DEFAULT);
                                        device = deviceDatabase.getDevice(src);
                                        Log.d(TAG, "TLMSDONE: " + device);
                                        if (device != null) {
                                            ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
                                            device.setLocallyAccessibility(true);
                                            device.setIp(senderIP);
                                            device.setName1(new String(decodedName, "UTF-8"));
                                            device.setPin1(pin1);
                                            device.setPin2(pin2);
                                            try {
                                                if (statusWord != null) {
                                                    Log.e(getClass().getSimpleName(), "Received Stword: " + statusWord + " Decoding with Key: " + device.getSecret());
                                                    Log.e("Decrypting", "Plain text Decrypted is: " + Encryptor.decrypt(statusWord, device.getSecret()));
                                                    Log.e(getClass().getSimpleName(), "Next Stword: " + (Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                                    device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                                    device.setHeader(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]);
                                                    Log.e(getClass().getSimpleName(), "New Stword With ending:" + device.getStatusWord());
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "Error in Decrypting: " + e);
                                                e.printStackTrace();
                                            }
                                            deviceDatabase.updateDevice(device);
                                        } else {
                                            Log.e(TAG, "An Unknown Device Detected...");
//                                    JsonObject jsonObject = new JsonObject();
//                                    jsonObject.addProperty("cmd", AppConstants.TO_DEVICE_NODE);
//                                    jsonObject.addProperty("src", RayanApplication.getPref().getId());
//                                    sendUDPMessage.sendUdpMessage(senderIP, jsonObject.toString());
                                        }
//                            } else Log.e(TAG, "There is a Unverified Device");
                                        break;
                                    case "YES":
                                        verifyDevice(device, senderIP);
//                                        src = jsonMessage.getString("src");
//                                        name = jsonMessage.getString("name");
//                                        ssid = jsonMessage.getString("ssid");
//                                        style = jsonMessage.getString("style");
//                                        type = jsonMessage.getString("type");
//                                        decodedName = Base64.decode(name, Base64.DEFAULT);
//                                        device = deviceDatabase.getDevice(src);
//                                        Log.d(TAG, "YESYESYES" + device);
//                                        if (device != null) {
//                                            ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
//                                            device.setLocallyAccessibility(true);
//                                            device.setIp(senderIP);
//                                            device.setName1(new String(decodedName, "UTF-8"));
//                                            device.setSsid(ssid);
//                                            device.setStyle(style);
//                                            deviceDatabase.updateDevice(device);
//                                        }
////                            else{
////                                device = new Device(src, new String(decodedName, "UTF-8"), "", type, "", new Topic(), "","");
////                                device.setSsid(ssid);
////                                device.setStyle(style);
////                                device.setIp(senderIP);
////                                deviceDatabase.addDevice(device);
////                            }
//                                        JsonObject jsonObject = new JsonObject();
//                                        jsonObject.addProperty("cmd", AppConstants.TO_DEVICE_TLMS);
//                                        jsonObject.addProperty("src", RayanApplication.getPref().getId());
//                                        sendUDPMessage.sendUdpMessage(senderIP, jsonObject.toString());
//                                        break;
                                    case "wrong_stword":

                                        break;

                                }
                            }else Log.e(TAG, "Auth Is Wrong");
                        }else{
                            Log.e(TAG, "Device is null");
                        }
                    }else Log.e(TAG, "Second Part Of Message Is Not Json");
                }else {
                    Log.e(TAG, "With No Auth");
                    if (isJSONValid(message)){
                        Log.e(TAG, "Valid Json Structure Detected");
                        JSONObject jsonObject = new JSONObject(message);
                        String src = jsonObject.getString("src");
                        Device device = deviceDatabase.getDevice(src);
                        if (device != null) {
                            Log.d(TAG, "One Device has Found: " + device);
                            switch (jsonObject.getString("cmd")) {
                                case "YES":
                                    verifyDevice(device, senderIP);
                                    break;
                                    case "hmac":
                                        Log.e(TAG, "HMAC Is: " + sha1(jsonObject.getString("text"), jsonObject.getString("k")));
                                        break;
                                case "en":
                                    String a = jsonObject.getString("text");
                                    Log.e("Encrypting", "Encrypted is: " + Encryptor.encrypt(a, jsonObject.getString("k")));
                                    break;
                                case "de":
                                    String b = jsonObject.getString("text");
                                    Log.e("Decrypting", "Decrypted is: " + Encryptor.decrypt(b, jsonObject.getString("k")));
                                    break;
                            }
                        }else {
                            Log.d(TAG, "No Device With ChipId: " + src);
                            switch (jsonObject.getString("cmd")){
                                case "hmac":
                                    Log.e(TAG, "HMAC Is: " + sha1(jsonObject.getString("text"), jsonObject.getString("k")));
                                    break;
                                case "en":
                                    String a = jsonObject.getString("text");
                                    Log.e("Encrypting", "Encrypted is: " + Encryptor.encrypt(a, jsonObject.getString("k")));
                                    break;
                                case "de":
                                    String b = jsonObject.getString("text");
                                    Log.e("Decrypting", "Decrypted is: " + Encryptor.decrypt(b, jsonObject.getString("k")));
                                    break;
                            }
                        }
                    }
                }
                //{"src":"5bf94a4ec7b255005bbed354", "cmd":"YES", "type":"switch_2", "ssid":"SomeSSID", "style":"connected", "name":"2KrYsdiq2LDYqtiw\n"}
                //{"src":"14337767", "cmd":"tgl","pin1":"on", "pin2":"off"}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e + "\nError Cause: " + e.getCause());
            if (ds!= null)
                ds.close();
        }
        finally {
            if (ds != null) {
                Log.e(TAG, "Closing UDPServerSocket");
                ds.close();
            }
        }
    }

    public void stop_UDP_Server()
    {
        Server_active = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UDPServerService() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stop_UDP_Server();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                runUdpServer();
            }
        };
        thread.start();
        apiService = ApiUtils.getApiService();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        return new String( org.apache.commons.net.util.Base64.encodeBase64(bytes) );
    }

    private Map<String, List<String>> tempVerification = new HashMap<>();
    public void verifyDevice(Device device, String ip){
        Log.d(TAG, "Verifying This Device: " + device);
        try {
        VerifyDeviceRequest verifyDeviceRequest = new VerifyDeviceRequest();
        List<String> currentList = tempVerification.get(device.getChipId());
        if (currentList == null){
            List<String> verificationList = new ArrayList<>();
            verificationList.add(verifyDeviceRequest.getAuth());
            tempVerification.put(device.getChipId(), verificationList);
        }else{
            currentList.add(verifyDeviceRequest.getAuth());
            tempVerification.put(device.getChipId(), currentList);
        }
        Log.e(TAG, "Putting Auth To Header: " + verifyDeviceRequest.ToString());
        Log.e(TAG, "Putting Auth To Header: " + sha1(verifyDeviceRequest.ToString(), device.getSecret()));
        apiService.verifyDevice(sha1(verifyDeviceRequest.ToString(), device.getSecret()), AppConstants.getDeviceAddress(ip), verifyDeviceRequest).subscribeOn(Schedulers.io())
        .subscribe(new Observer<Response<VerifyDeviceResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Response<VerifyDeviceResponse> verifyDeviceResponse) {
                Log.e(TAG, "onNext() called with: verifyDeviceResponse = [" + verifyDeviceResponse + "]");
                if (verifyDeviceResponse.body().getAuth() != null){
                    Log.e(TAG, "Auth Detected: "+ verifyDeviceResponse.body().getAuth());
                    try {
                        Log.e(TAG, "String Auth Was: " + tempVerification.get(device.getChipId()));
//                        String originalAuth = AppConstants.sha1(tempVerification.get(device.getChipId()), device.getSecret());
//                        Log.e(TAG, "Orginal Auth: " + originalAuth);
                        if (checkVerificationString(device.getChipId(), device.getSecret(), verifyDeviceResponse.body().getAuth())){
                            Log.e(TAG, "Auth is OK");
                            if (verifyDeviceResponse.body().getCmd().equals(AppConstants.FROM_DEVICE_VERIFY_DONE)) {
                                Log.e(TAG, "Verify_done received from device");
                                String pin1, pin2, name, statusWord, src;
                                byte[] decodedName;
                                src = verifyDeviceResponse.body().getSrc();
                                pin1 = verifyDeviceResponse.body().getPin1();
                                pin2 = verifyDeviceResponse.body().getPin2();
                                name = verifyDeviceResponse.body().getName();
                                statusWord = null;
                                if (verifyDeviceResponse.body().getStword() != null) {
                                    statusWord = verifyDeviceResponse.body().getStword();
                                } else Log.e(TAG, "There is no stword");
                                decodedName = Base64.decode(name, Base64.DEFAULT);
                                Log.d(TAG, "Verifying This Device: " + device);
                                if (device != null) {
                                    ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
                                    device.setLocallyAccessibility(true);
                                    device.setIp(ip);
                                    device.setName1(new String(decodedName, "UTF-8"));
                                    device.setPin1(pin1);
                                    device.setPin2(pin2);
                                    if (statusWord != null) {
                                        Log.e(getClass().getSimpleName(), "Received Stword: " + statusWord + " Decoding with Key: " + device.getSecret());
                                        Log.e("Decrypting", "Plain text Decrypted is: " + Encryptor.decrypt(statusWord, device.getSecret()));
                                        Log.e(getClass().getSimpleName(), "Next Stword: " + (Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                        device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[1]) + 1));
                                        device.setHeader(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]);
                                        Log.e(getClass().getSimpleName(), "New Stword With ending:" + device.getStatusWord());
                                    }
                                    deviceDatabase.updateDevice(device);
                                }
                            }else{
                                Log.e(TAG, "Not_verified received from device");
                            }
                        }else{
                            Log.e(TAG, "Verification Failed Because of Wrong AUTH");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e(TAG, "Verification Failed Because of empty auth");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError() called with: e = [" + e + "]");
            }

            @Override
            public void onComplete() {

            }
        });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public boolean checkVerificationString(String chipId, String secret, String auth) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        List<String> deviceAuthList = tempVerification.get(chipId);
        if (deviceAuthList != null)
        for (String expectedAuth: deviceAuthList)
            if (auth.equals(AppConstants.sha1(expectedAuth, secret)))
                return true;
        return false;
    }
}

