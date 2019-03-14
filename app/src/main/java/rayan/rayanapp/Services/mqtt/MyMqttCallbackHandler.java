package rayan.rayanapp.Services.mqtt;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import rayan.rayanapp.Persistance.database.DeviceDatabase;


public class MyMqttCallbackHandler implements MqttCallback {
  private final String TAG = MyMqttCallbackHandler.class.getSimpleName();
  private Context context;
  private DeviceDatabase deviceDatabase;

  public MyMqttCallbackHandler(Context context){
      this.context = context;
      deviceDatabase = new DeviceDatabase(context);
  }

//  public static synchronized MqttCallbackHandler getInstance(){
//      MqttCallbackHandler mqttCallbackHandler;
//      mqttCallbackHandler = new MqttCallbackHandler();
//      return mqttCallbackHandler;
//  }

  @Override
  public void connectionLost(Throwable cause) {
      Connection connection = MainActivityViewModel.connection.getValue();
      connection.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
      MainActivityViewModel.connection.postValue(connection);
      Toast.makeText(context, "MQTT Connection Lost", Toast.LENGTH_SHORT).show();
      Log.e(TAG , "Connection Lost: " + cause);
    if (cause != null) {
        Log.e(TAG, "MqttCallBackHandler" + "ConnectionLost");
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
//      Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
      Log.e(TAG, "MQTT Message Received//: " + message.toString()+"\tTopic: " + topic);
      JSONObject jsonMessage = new JSONObject(message.toString());
      String cmd = jsonMessage.getString("cmd");
      switch (cmd) {
          case "tgl":
              String src = jsonMessage.getString("src");
              String sid = jsonMessage.getString("sid");
              String pin1 = jsonMessage.getString("pin1");
              String pin2 = jsonMessage.getString("pin2");
              Device device = deviceDatabase.getDevice(src);
              if (device != null){
                  ((RayanApplication)context.getApplicationContext()).getDevicesAccessibilityBus().send(src);
                  device.setPin1(pin1);
                  device.setPin2(pin2);
                  deviceDatabase.updateDevice(device);
              }
              else {
                  Log.e(TAG, "Can't find device with chipId: " + src);
              }
              break;
      }
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
      Log.e(TAG, "Delivery Completed//: ");
  }

    public static boolean isJSONValid(String test) {
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