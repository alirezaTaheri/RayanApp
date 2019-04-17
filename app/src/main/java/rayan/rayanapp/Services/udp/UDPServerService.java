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

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Helper.AES;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.SendMessageToDevice;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Util.AppConstants;
import se.simbio.encryption.Encryption;

public class UDPServerService extends Service {
    private AsyncTask<Void, Void, Void> async;
    private boolean Server_active = true;
    public boolean isRunnig = false;
    private AppDatabase dataBase;
    private DeviceDatabase deviceDatabase;
    SendUDPMessage sendUDPMessage;
    private int port;

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
                if (isJSONValid(message)){
                    JSONObject jsonMessage = new JSONObject(message);
                    ((RayanApplication)getApplication()).getBus().send(jsonMessage);
                    String cmd = jsonMessage.getString("cmd");
                    String src = jsonMessage.getString("src");
                    String pin1, pin2, name, ssid, style, type,statusWord;
                    byte[]  decodedName;
                    Device device;
                    switch (cmd) {
                        case "tgl":
                            pin1 = jsonMessage.getString("pin1");
                            pin2 = jsonMessage.getString("pin2");
                            device = deviceDatabase.getDevice(src);
                            if (device == null)
                                Log.e(TAG, "Couldn't find this Device: " + src);
                            else{
                                ((RayanApplication)getApplication()).getDevicesAccessibilityBus().send(src);
                                device.setLocallyAccessibility(true);
                                device.setPin1(pin1);
                                device.setPin2(pin2);
                                device.setIp(senderIP);
                                deviceDatabase.updateDevice(device);
                            }
                            break;
                        case "en":
                            String a = jsonMessage.getString("text");
//                            AES.setKey("q7tt0yk18nrjrqur");
//                            Log.e("Encrypting","Encrypted is: " + Encryptor.encrypt(a,"q7tt0yk18nrjrqur"));
//                            Encryption encryption = Encryption.getDefault("8nro4q0emv8k1uv5", "", new byte[16]);
//                            Log.e("Encrypting","Encrypted is: " + encryption.encryptOrNull(a));
                            Log.e("Encrypting","Encrypted is: " + Encryptor.encrypt(a,jsonMessage.getString("k")));
//                            Log.e("Encrypting","Library   is: " + AESCrypt.encrypt("8nro4q0emv8k1uv5",a));
//                            Log.e("Encrypting","Encrypted2222 is: " + AES.encrypt(a));

//                            Log.e("Encrypting","Encrypted22 is: " + AES.encrypt(a,"q7tt0yk18nrjrqur"));

                            break;
//                        case "tos":
//                            byte[] de = Base64.decode(jsonMessage.getString("text"), Base64.DEFAULT);
//                            Log.e("bbbbbbbbbb", "bbbbbbbbb: " + new String(de,"UTF-8"));
//                            break;
//                        case "toba":
//                            Log.e("bbbbbbbbbb", "bbbbbbbbb: " + Base64.encodeToString(jsonMessage.getString("text").getBytes(), Base64.DEFAULT));
//                            break;
                        case "de":
                            String b = jsonMessage.getString("text");
                            Log.e("Decrypting","Decrypted is: " + Encryptor.decrypt(b,"8nro4q0emv8k1uv5"));
//                            Log.e("Decrypting","Decrypted22 is: " + AESCrypt.decrypt("8nro4q0emv8k1uv5",b));
                            break;
                        case "TLMSDONE":
                            if (jsonMessage.has("stword")) {
                                pin1 = jsonMessage.getString("pin1");
                                pin2 = jsonMessage.getString("pin2");
                                name = jsonMessage.getString("name");
                                statusWord = jsonMessage.getString("stword");
                                decodedName = Base64.decode(name, Base64.DEFAULT);
                                device = deviceDatabase.getDevice(src);
                                Log.d(TAG, "TLMSDONETLMSDONE: " + device);
                                if (device != null) {
                                    ((RayanApplication) getApplication()).getDevicesAccessibilityBus().send(src);
                                    device.setLocallyAccessibility(true);
                                    device.setIp(senderIP);
                                    device.setName1(new String(decodedName, "UTF-8"));
                                    device.setPin1(pin1);
                                    device.setPin2(pin2);
                                    Log.e(getClass().getSimpleName(), "Received Stword: " + statusWord);
                                    Log.e(getClass().getSimpleName(), "Next Stword: " + (Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]) + 1));

                                    device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(statusWord, device.getSecret()).split("#")[0]) + 1));
                                    Log.e(getClass().getSimpleName(), "New Stword With ending:" + device.getStatusWord());
                                    deviceDatabase.updateDevice(device);
                                } else {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("cmd", AppConstants.TO_DEVICE_NODE);
                                    jsonObject.addProperty("src", RayanApplication.getPref().getId());
                                    sendUDPMessage.sendUdpMessage(senderIP, jsonObject.toString());
                                }
                            } else Log.e(TAG, "There is a Unverified Device");
                            break;
                        case "YES":
                            src = jsonMessage.getString("src");
                            name = jsonMessage.getString("name");
                            ssid = jsonMessage.getString("ssid");
                            style = jsonMessage.getString("style");
                            type = jsonMessage.getString("type");
                            decodedName = Base64.decode(name, Base64.DEFAULT);
                            device = deviceDatabase.getDevice(src);
                            Log.d(TAG, "YESYESYES"+ src);
                            if (device != null){
                                ((RayanApplication)getApplication()).getDevicesAccessibilityBus().send(src);
                                device.setLocallyAccessibility(true);
                                device.setIp(senderIP);
                                device.setName1(new String(decodedName, "UTF-8"));
                                device.setSsid(ssid);
                                device.setStyle(style);
                                deviceDatabase.updateDevice(device);
                            }
//                            else{
//                                device = new Device(src, new String(decodedName, "UTF-8"), "", type, "", new Topic(), "","");
//                                device.setSsid(ssid);
//                                device.setStyle(style);
//                                device.setIp(senderIP);
//                                deviceDatabase.addDevice(device);
//                            }
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("cmd",AppConstants.TO_DEVICE_TLMS);
                            jsonObject.addProperty("src",RayanApplication.getPref().getId());
                            sendUDPMessage.sendUdpMessage(senderIP, jsonObject.toString());
                            break;
                        case "wrong_stword":

                            break;

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
        sendUDPMessage = new SendUDPMessage();
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
}

