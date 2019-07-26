package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.ScenarioDatabase;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Util.AppConstants;

public class ScenariosFragmentViewModel extends DevicesFragmentViewModel{

    private final String TAG = this.getClass().getCanonicalName();
    GroupDatabase groupDatabase;
    ScenarioDatabase scenarioDatabase;
    public ScenariosFragmentViewModel(@NonNull Application application) {
        super(application);
        groupDatabase = new GroupDatabase(application);
        scenarioDatabase = new ScenarioDatabase(application);
    }

    public LiveData<List<Scenario>> getAllScenariosLive(){
        return scenarioDatabase.getScenariosLive();
    }
    public Scenario getScenario(int id){
        return scenarioDatabase.getScenario(id);
    }

    public void sendMqttPin1(RayanApplication rayanApplication, Device device, boolean on){
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(true));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(on? AppConstants.ON_1:AppConstants.OFF_1,arguments).toString(), 0, false);
    }

    public void sendMqttPin2(RayanApplication rayanApplication, Device device, boolean on){
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(true));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(on? AppConstants.ON_2 : AppConstants.OFF_2 ,arguments).toString(), 0, false);
    }

    private void publishMqtt(String chipId, RayanApplication rayanApplication, Connection connection, String topic, String message, int qos, boolean retain){
        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
//            final ActionListener callback = new ActionListener(context,
//                    ActionListener.Action.PUBLISH, connection, actionArgs);
            IMqttActionListener iMqttActionListener = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(TAG, "onSuccess Publish Topic: " +topic+" Message: "+ message);
                    rayanApplication.getMqttMessagesController().messageSent(chipId);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "onFailure Publish message: " + topic);
                    Log.e(TAG, "onFailure Publish Exception: " + exception);
                    if (exception != null)
                        exception.printStackTrace();

                }
            };
            if (connection != null && connection.getClient() != null)
                connection.getClient().publish(topic, message.getBytes(), qos, retain, null, iMqttActionListener);

        } catch(Exception ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }

    public Flowable<List<Group>> getAllGroupsFlowable(){
        return groupDatabase.getAllGroupsFlowable();
    }

    public void deleteScenarioWithId(int id){
        scenarioDatabase.deleteScenarioWithId(id);
    }
}
