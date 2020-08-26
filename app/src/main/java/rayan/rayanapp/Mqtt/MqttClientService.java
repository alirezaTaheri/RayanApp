package rayan.rayanapp.Mqtt;

import android.app.Application;
import android.util.Log;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Util.AppConstants;

public class MqttClientService {
    public static MqttClient mqttClient;
    private static final String TAG = "MqttClientService";

    public static synchronized MqttClient getMqttClientInstance(Application application){
//        Log.e(TAG, "Initializing MqttClient");
        if (mqttClient == null){
            Log.e(TAG, "Making The Client...");
            mqttClient = new MqttClient.Builder()
                    .setRayanApplication((RayanApplication) application)
                    .setURL(AppConstants.MQTT_HOST)
                    .build();
            return mqttClient;
        }
//        Log.e(TAG, "Client is already created");
        //mqttClient.connectToBroker();
        return mqttClient;
    }
    public static synchronized MqttClient getMqttClientInstanceOnly(Application application){
        if (mqttClient == null){
            mqttClient = new MqttClient.Builder()
                    .setRayanApplication((RayanApplication) application)
                    .setURL(AppConstants.MQTT_HOST)
                    .build();
            return mqttClient;
        }
        return mqttClient;
    }



    public static void initializeAndConnectToBroker(Application application){
                mqttClient = getMqttClientInstance(application);
                mqttClient.connectToBroker();
    }
    public static void connectToBroker(Application application){
                mqttClient.connectToBroker();
    }

    private MqttClientService(){
        if (mqttClient != null){
            throw new RuntimeException("You should use getInstance to get a single object");
        }
    }

    public static void removeClient(){
        mqttClient = null;
    }
}
