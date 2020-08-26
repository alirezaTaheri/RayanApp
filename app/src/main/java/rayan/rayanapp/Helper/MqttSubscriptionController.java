package rayan.rayanapp.Helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rayan.rayanapp.Mqtt.MqttClientService;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.RemoteDatabase;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;

public class MqttSubscriptionController {
    public List<String> dbTopics = new ArrayList<>();
    public List<String> newArrivedTopics = new ArrayList<>();
    public boolean mqttConnected = false;
    public List<String> subscribedTopics = new ArrayList<>();
    private final String TAG = "MqttSubConLevellll";
    DeviceDatabase deviceDatabase;
    RemoteHubDatabase remoteHubDatabase;
    RemoteDatabase remoteDatabase;
    Application application;
    public MqttSubscriptionController(Context context) {
        deviceDatabase = new DeviceDatabase(context);
        remoteHubDatabase = new RemoteHubDatabase(context);
        remoteDatabase = new RemoteDatabase(context);
        Log.e(TAG, "created...");
        application = (Application) context.getApplicationContext();
    }

    public void init(){
        Log.e(TAG, "initializing...");
        List<String> allTopics = deviceDatabase.getAllTopics();
        allTopics.addAll(remoteHubDatabase.getAllTopics());
        allTopics.addAll(remoteDatabase.getAllTopics());
        setDbTopics(allTopics);
    }

    public void setDbTopics(List<String> dbTopics){
        this.dbTopics = dbTopics;
        Log.e(TAG, "setDbTopic...");
        check();
    }

    public void setNewArrivedTopics(List<String> newArrivedTopics) {
        Log.e(TAG, "setNewArrivedTopics...");
        this.newArrivedTopics = newArrivedTopics;
//        for (int a = 0; a<subscribedTopics.size();a++) {
//            boolean exists = false;
//            for (int b = 0; b < newArrivedTopics.size(); b++){
//                if (newArrivedTopics.get(b).equals(subscribedTopics.get(a))) exists = true;
//            }
//            if (!exists) subscribedTopics.remove(a);
//        }

        check();
    }

    public void setMqttConnected(boolean mqttConnected){
        Log.e(TAG, "setMqttConnected..."+mqttConnected);
        subscribedTopics.clear();
        this.mqttConnected = mqttConnected;
        if (mqttConnected){
            check();
        }
        else {
            check();
        }
    }

    public void check(){
        try {
            if (mqttConnected) {
                if (dbTopics.size() >0) {
                    Log.e(TAG, "check...dbTopics " + dbTopics.size());
                    for (int a = 0; a < dbTopics.size(); a++) {
                        if (!subscribedTopics.contains(dbTopics.get(a))) {
                            int finalA = a;
                            MqttClientService.getMqttClientInstance(application).getConnection().getClient().subscribe(dbTopics.get(a), 0).setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
//                                    Log.e(TAG, "Subscribed Successfully to: " + dbTopics.get(finalA));
                                    subscribedTopics.add(dbTopics.get(finalA));
                                    if (finalA == dbTopics.size() - 1)
                                        check2();
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                    Log.e(TAG, "Subscribed Failed to: " + dbTopics.get(finalA));
                                    if (finalA == dbTopics.size() - 1)
                                        check2();
                                }
                            });
                        }
                    }
                }else check2();

            }else subscribedTopics = new ArrayList<>();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void check2(){
        try {
            if (mqttConnected) {
                Log.e(TAG, "check2...newArrivedTopics " + newArrivedTopics.size());
                for (int a = 0; a < newArrivedTopics.size(); a++) {
                    if (!subscribedTopics.contains(newArrivedTopics.get(a))) {
                        int finalA = a;
                        MqttClientService.getMqttClientInstance(application).getConnection().getClient().subscribe(newArrivedTopics.get(a), 0).setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
//                                Log.e(TAG, "Fast Subscribed Successfully to: " + newArrivedTopics.get(finalA));
                                subscribedTopics.add(newArrivedTopics.get(finalA));
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                Log.e(TAG, "Fast Subscribed Successfully to: " + newArrivedTopics.get(finalA));
                            }
                        });
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
