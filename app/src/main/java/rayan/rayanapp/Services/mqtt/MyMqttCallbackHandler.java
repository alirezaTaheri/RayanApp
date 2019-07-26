package rayan.rayanapp.Services.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import rayan.rayanapp.Persistance.database.DeviceDatabase;


public class MyMqttCallbackHandler implements MqttCallback {
  private final String TAG = MyMqttCallbackHandler.class.getSimpleName();
  private Context context;
  private DeviceDatabase deviceDatabase;
  private RayanApplication rayanApplication;
  public MyMqttCallbackHandler(Context context, RayanApplication rayanApplication){
      this.context = context;
      this.rayanApplication = rayanApplication;
      deviceDatabase = new DeviceDatabase(context);
      Log.e(TAG, "I am Coming" + this);
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
//      Toast.makeText(context, "MQTT Connection Lost", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "MqttCallBackHandlerConnectionLost: " + cause);
    if (cause != null) {
        cause.printStackTrace();
        Log.e(TAG , "Connection Lost: " + cause);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
      Log.e("-----------" ,"-----------------------: " + Thread.currentThread().getName());
      resolveMessage(topic, message);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
      Log.e(TAG, "Delivery Completed//: ");
  }

    public static JSONObject isJSONValid(String test) {
        try {
            return new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return null;
            }
        }
        return null;
    }
    public Maybe<Device> getDeviceAsync(String chipId){
      return deviceDatabase.getDeviceFlowable(chipId).observeOn(Schedulers.io());
    }

    @SuppressLint("CheckResult")
    public void resolveMessage(String topic, MqttMessage message){
      Observable.create(new ObservableOnSubscribe<Object>() {
          @Override
          public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
              Log.e(TAG, "MQTT Message Received//: " + message.toString()+"\tTopic: " + topic +" htisis: " +MyMqttCallbackHandler.this);
              JSONObject jsonMessage = isJSONValid(message.toString());
                  if (jsonMessage != null) {
                      try {
                          String cmd = jsonMessage.getString("cmd");
                          switch (cmd) {
                              case "tgl":
                                  String src = jsonMessage.getString("src");
                                  String sid = jsonMessage.getString("sid");
                                  String pin1 = jsonMessage.getString("pin1");
                                  String pin2 = jsonMessage.getString("pin2");
                                  rayanApplication.getMqttMessagesController().responseReceived(src, jsonMessage);
                                  getDeviceAsync(src).subscribe(device -> {
                                      if (device != null) {
//                          ((RayanApplication)context.getApplicationContext()).getDevicesAccessibilityBus().send(src);
                                          device.setPin1(pin1);
                                          device.setPin2(pin2);
                                          deviceDatabase.updateDevice(device);
                                      } else {
                                          Log.e(TAG, "Can't find device with chipId: " + src);
                                      }
                                  });
                                  break;
                          }
                      } catch (Exception e) {
                          Log.e(TAG, "There is an error after receiving message... " + e);
                          e.printStackTrace();
                      }
                  }
          }
      }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(o -> {

      });
    }

}