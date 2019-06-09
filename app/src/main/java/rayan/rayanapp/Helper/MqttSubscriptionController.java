package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MqttSubscriptionController {
    public List<String> dbTopics = new ArrayList<>();
    public List<String> newArrivedTopics = new ArrayList<>();
    public boolean mqttConnected = false;
    public List<String> subscribedTopics = new ArrayList<>();
    private final String TAG = "MqttSubConLevellll";
    DeviceDatabase deviceDatabase;
    public MqttSubscriptionController(Context context) {
        deviceDatabase = new DeviceDatabase(context);
        Log.e(TAG, "created...");
    }

    public void init(){
        Log.e(TAG, "initializing...");
        setDbTopics(deviceDatabase.getAllTopics());
    }

    public void setDbTopics(List<String> dbTopics){
        this.dbTopics = dbTopics;
        Log.e(TAG, "setDbTopic...");
        check();
    }

    public void setNewArrivedTopics(List<String> newArrivedTopics) {
        Log.e(TAG, "setNewArrivedTopics...");
        this.newArrivedTopics = newArrivedTopics;
        check();
    }

    public void setMqttConnected(boolean mqttConnected){
        Log.e(TAG, "setMqttConnected..."+mqttConnected);
        this.mqttConnected = mqttConnected;
        check();
    }

    public void check(){
        try {
            if (mqttConnected) {
                if (dbTopics.size() >0) {
                    Log.e(TAG, "check...dbTopics " + dbTopics.size());
                    for (int a = 0; a < dbTopics.size(); a++) {
                        if (!subscribedTopics.contains(dbTopics.get(a))) {
                            int finalA = a;
                            MainActivityViewModel.connection.getValue().getClient().subscribe(dbTopics.get(a), 0).setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    subscribedTopics.add(dbTopics.get(finalA));
                                    if (finalA == dbTopics.size() - 1)
                                        check2();
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    if (finalA == dbTopics.size() - 1)
                                        check2();
                                }
                            });
                        }
                    }
                }else check2();

            }
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
                        MainActivityViewModel.connection.getValue().getClient().subscribe(newArrivedTopics.get(a), 0).setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                subscribedTopics.add(newArrivedTopics.get(finalA));
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
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
