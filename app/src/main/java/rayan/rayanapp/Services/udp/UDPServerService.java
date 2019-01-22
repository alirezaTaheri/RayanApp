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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.Models.Topic;
import rayan.rayanapp.Util.AppConstants;

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
                    String cmd = jsonMessage.getString("cmd");
                    String src = jsonMessage.getString("src");
                    String pin1, pin2, name, ssid, style, type;
                    byte[] decodedName;
                    Device device;
                    switch (cmd) {
                        case "tgl":
                            pin1 = jsonMessage.getString("pin1");
                            pin2 = jsonMessage.getString("pin2");
                            device = deviceDatabase.getDevice(src);
                            if (device == null)
                                Log.e("nnnnnnn", "nnnnull Couldn't find " + src);
                            else{
                                device.setPin1(pin1);
                                device.setPin2(pin2);
                                device.setIp(senderIP);
                                deviceDatabase.updateDevice(device);
                            }
                            break;
                        case "TLMSDONE":
                            pin1 = jsonMessage.getString("pin1");
                            pin2 = jsonMessage.getString("pin2");
                            name = jsonMessage.getString("name");
                            decodedName = Base64.decode(name, Base64.DEFAULT);
                            device = deviceDatabase.getDevice(src);
                            if (device != null){
                                device.setIp(senderIP);
                                device.setName1(new String(decodedName, "UTF-8"));
                                device.setPin1(pin1);
                                device.setPin2(pin2);
                                deviceDatabase.updateDevice(device);
                            }
                            else{
                                sendUDPMessage.sendUdpMessage(senderIP, AppConstants.TO_DEVICE_NODE);
                            }
                            break;
                        case "YES":
                            src = jsonMessage.getString("src");
                            name = jsonMessage.getString("name");
                            ssid = jsonMessage.getString("ssid");
                            style = jsonMessage.getString("style");
                            type = jsonMessage.getString("type");
                            decodedName = Base64.decode(name, Base64.DEFAULT);
                            device = deviceDatabase.getDevice(src);
                            if (device != null){
                                device.setIp(senderIP);
                                device.setName1(new String(decodedName, "UTF-8"));
                                device.setSsid(ssid);
                                device.setStyle(style);
                                deviceDatabase.updateDevice(device);
                            }
                            else{
                                device = new Device(src, new String(decodedName, "UTF-8"), "", type, "", new Topic(), "");
                                device.setSsid(ssid);
                                device.setStyle(style);
                                device.setIp(senderIP);
                            }
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
        return START_STICKY;
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

