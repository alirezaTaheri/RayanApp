package rayan.rayanapp.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.Models.Responses.device.TlmsDoneResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.YesResponse;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class SendMessageToDevice {
    private final String TAG = getClass().getSimpleName();
    private SendUDPMessage sendUDPMessage = new SendUDPMessage();
    private ApiService apiService;
    private DeviceDatabase deviceDatabase;
    public SendMessageToDevice(Context context) {
        apiService = ApiUtils.getApiService();
        deviceDatabase = new DeviceDatabase(context);
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

    Observable<Long> counterObservable = Observable.interval(0,700,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    Observable<Long> timerObservable = Observable.timer(AppConstants.MQTT_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());

    private void sendMqttPin1(RayanApplication rayanApplication, Device device, int position, ToggleDeviceAnimationProgress fragment, boolean animation, boolean lastCommandRetain){
        if (lastCommandRetain)
            Observable.interval(0, AppConstants.ATTACH_MQTT_REQ_TO_TOPIC_TIMEOUT, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (lastCommand.get(device.getChipId() + "_1") != null)
                        lastCommand.get(device.getChipId() + "_1").dispose();
                    lastCommand.put(device.getChipId() + "_1", d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 2");
                }

                @Override
                public void onNext(Long aLong) {
                    if (aLong > 0) {
                        Log.e(TAG, "Sending last Command...");
                        JSONObject lastMessage = null;
                        try {
                            lastMessage = rayanApplication.getMqttMessagesController().getLastMessageOfDevice(device.getChipId());
                            List<String> arguments = new ArrayList<>();
                            arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
                            arguments.add(Boolean.toString(animation));
                            if (lastMessage != null){
                                if (!lastMessage.has("lc")){
                                    //without lc
                                    lastMessage.put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }
                                else if (lastMessage.getJSONObject("lc").getString("cmd").contains("1")){
                                    //with lc1 pin 1
                                    if (lastMessage.getJSONObject("lc").has("lc")){
                                        Object lc2 = lastMessage.getJSONObject("lc").get("lc");
                                        lastMessage.put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                        lastMessage.getJSONObject("lc").put("lc", lc2);
                                    }else {
                                        lastMessage.put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                    }
                                }
                                else if (((JSONObject)lastMessage.get("lc")).has("lc") && lastMessage.getJSONObject("lc").getJSONObject("lc").getString("cmd").contains("1")){
                                    //with lc2 pin 1
                                    lastMessage.getJSONObject("lc").put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }
                                else if (!lastMessage.getJSONObject("lc").has("lc")){
                                    //with Just one LC
                                    ((JSONObject)lastMessage.get("lc")).put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }

                            }
//                            if (lastMessage != null) {
//                                if (!lastMessage.has("lc") || !((JSONObject)lastMessage.get("lc")).has("lc") && ((String)(lastMessage).get("cmd")).contains("1"))
//                                    lastMessage.put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
//                                else {
//                                    JSONObject lc2 = (JSONObject) ((JSONObject)lastMessage.get("lc")).get("lc");
//                                    lastMessage.put("lc", rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
//                                    ((JSONObject)lastMessage.get("lc")).put("lc",lc2);
//                                }
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Lastmessage is : " + lastMessage);
                        if (lastMessage != null && !rayanApplication.getMqttMessagesController().isReceivedResponse(device.getChipId())){
//                            Toast.makeText(rayanApplication, "Command Attached", Toast.LENGTH_SHORT).show();
                            publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), lastMessage.toString(), 0, true);
                        }
                        lastCommand.get(device.getChipId() + "_1").dispose();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "OnError Timer executed for Mqtt Backup pin 2: " + e);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    Log.e(TAG, "onComplete  Timer executed for Mqtt Backup pin 2");
                }
            });
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(animation));
        Log.e(TAG, "Need MQTT-Backup? : " + (!animation));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString(), 0, false);
            timerObservable.subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (animation)
                        if (device.getPin1().equals(AppConstants.ON_STATUS))
                            fragment.getDeviceAnimator().turningOffPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        else fragment.getDeviceAnimator().turningOnPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());

//                        fragment.startToggleAnimationPin1(device.getChipId(), position);
                    rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                    rayanApplication.getDevicesAccessibilityBus().setWaitingPin1(device.getChipId(), d);
                }

                @Override
                public void onNext(Long aLong) {
                    Log.e("/////////", "////////OnNext////: " + aLong);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("/////////", "////////onError/////: " +e);
                }

                @Override
                public void onComplete() {
                    Log.e("/////////", "////////OnComplete/////Pin1 Stopping animation:: ");
                    fragment.sendingMessageTimeoutPin1(device.getChipId(), position, device.getType());
//                    fragment.stopToggleAnimationPin1(device.getChipId());
                    rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                }
            });
    }

    private void sendMqttPin2(RayanApplication rayanApplication, Device device, int position, ToggleDeviceAnimationProgress fragment, boolean animation, boolean lastCommandRetain){
        if (lastCommandRetain)
            Observable.interval(0, AppConstants.ATTACH_MQTT_REQ_TO_TOPIC_TIMEOUT, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (lastCommand.get(device.getChipId() + "_2") != null)
                        lastCommand.get(device.getChipId() + "_2").dispose();
                    lastCommand.put(device.getChipId() + "_2", d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 2");
                }

                @Override
                public void onNext(Long aLong) {
                    if (aLong > 0) {
                        Log.e(TAG, "Sending last Command...");
                        JSONObject lastMessage = null;
                        try {
                            lastMessage = rayanApplication.getMqttMessagesController().getLastMessageOfDevice(device.getChipId());
                            List<String> arguments = new ArrayList<>();
                            arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
                            arguments.add(Boolean.toString(animation));
                            if (lastMessage != null){
                                if (!lastMessage.has("lc")){
                                    //without lc
                                    lastMessage.put("lc", rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_2 : AppConstants.ON_2, arguments));
                                }
                                else if (((String)((JSONObject)lastMessage.get("lc")).get("cmd")).contains("2")){
                                    //with lc1 pin 2
                                    if (((JSONObject)lastMessage.get("lc")).has("lc")){
                                        Object lc2 = ((JSONObject)lastMessage.get("lc")).get("lc");
                                        lastMessage.put("lc", rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_2 : AppConstants.ON_2, arguments));
                                        lastMessage.getJSONObject("lc").put("lc", lc2);
                                    }else {
                                        lastMessage.put("lc", rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_2 : AppConstants.ON_2, arguments));
                                    }
                                }
                                else if (((JSONObject)lastMessage.get("lc")).has("lc") && lastMessage.getJSONObject("lc").getJSONObject("lc").getString("cmd").contains("2")){
                                    //with lc2 pin 2
                                    ((JSONObject)lastMessage.get("lc")).put("lc", rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_2 : AppConstants.ON_2, arguments));
                                }
                                else if (!((JSONObject)lastMessage.get("lc")).has("lc")) {
                                    //with Just one LC
                                    ((JSONObject)lastMessage.get("lc")).put("lc", rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_2 : AppConstants.ON_2, arguments));
                                }

                            }
//                            if (lastMessage != null) {
//                                if (!((JSONObject)lastMessage.get("lc")).has("lc"))
//                                    lastMessage.put("lc", );
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Lastmessage is : " + lastMessage);
                        if (lastMessage != null && !rayanApplication.getMqttMessagesController().isReceivedResponse(device.getChipId())){
//                            Toast.makeText(rayanApplication, "Command Attached", Toast.LENGTH_SHORT).show();
                            publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), lastMessage.toString(), 0, true);
                        }
                        lastCommand.get(device.getChipId() + "_2").dispose();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "OnError Timer executed for Mqtt Backup pin 2: " + e);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    Log.e(TAG, "onComplete  Timer executed for Mqtt Backup pin 2");
                }
            });
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(animation));
        Log.e(TAG, "Need MQTT-Backup? : " + (!animation));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS)? AppConstants.OFF_2 : AppConstants.ON_2,arguments).toString(), 0, false);
        timerObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (animation)
//                    fragment.startToggleAnimationPin2(device.getChipId(), position);
                    if (device.getPin2().equals(AppConstants.ON_STATUS))
                        fragment.getDeviceAnimator().turningOffPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                    else fragment.getDeviceAnimator().turningOnPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                rayanApplication.getDevicesAccessibilityBus().setWaitingPin2(device.getChipId(), d);
            }

            @Override
            public void onNext(Long aLong) {
                Log.e("/////////", "////////OnNext////: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("/////////", "////////onError/////: " +e);
            }

            @Override
            public void onComplete() {
                Log.e("/////////", "////////OnComplete/////Pin2 Stopping animation: ");
//                fragment.stopToggleAnimationPin2(device.getChipId());
                fragment.sendingMessageTimeoutPin2(device.getChipId(), position, device.getType());
                rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
            }
        });
    }

    private void sendUdpPin1(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position){
        List<String> arguments = new ArrayList<>();
        arguments.add(device.getStatusWord());
        counterObservable.takeWhile(aLong -> aLong<5 && rayanApplication.getDevicesAccessibilityBus().isWaiting(device.getChipId()))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        fragment.startToggleAnimationPin1(device.getChipId(), position);
                        if (device.getPin1().equals(AppConstants.ON_STATUS))
                            fragment.getDeviceAnimator().turningOffPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        else fragment.getDeviceAnimator().turningOnPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().setWaitingPin1(device.getChipId(), d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("Step1: ", "Step111 : " + String.valueOf(Integer.parseInt(device.getStatusWord().split("#")[0])+aLong));
                        Log.e("Step1: ", "Step222 : " + String.valueOf(Integer.parseInt(device.getStatusWord().split("#")[0])+aLong).concat("/0"));
                        Log.e("Step2: ", "Step333 : " + Encryptor.encrypt(String.valueOf(Integer.parseInt(device.getStatusWord().split("#")[0])+aLong).concat("#"), device.getSecret()));

                        arguments.set(0,Encryptor.encrypt(String.valueOf(Integer.parseInt(device.getStatusWord().split("#")[0])+aLong).concat("#"), device.getSecret()));
                        Log.e("/////////", "////////OnNext////: " + aLong + arguments.get(0));
                        sendUDPMessage.sendUdpMessage(device.getIp(),rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("/////////", "////////onError/////: " +e);
//                        fragment.stopToggleAnimationPin1(device.getChipId());
                        fragment.sendingMessageTimeoutPin1(device.getChipId(), position, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("/////////", "////////OnComplete/////: ");
//                        fragment.stopToggleAnimationPin1(device.getChipId());
                        fragment.sendingMessageTimeoutPin1(device.getChipId(), position, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                    }
                });
//        sendUDPMessage.sendUdpMessage(device.getIp(), rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString());
    }

    private void sendUdpPin2(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position){
        List<String> arguments = new ArrayList<>();
        arguments.add(device.getStatusWord());
        counterObservable.takeWhile(aLong -> aLong<5 && rayanApplication.getDevicesAccessibilityBus().isWaiting(device.getChipId()))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        fragment.startToggleAnimationPin2(device.getChipId(), position);
                        if (device.getPin2().equals(AppConstants.ON_STATUS))
                            fragment.getDeviceAnimator().turningOffPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        else fragment.getDeviceAnimator().turningOnPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().setWaitingPin2(device.getChipId(), d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        arguments.set(0,Encryptor.encrypt(String.valueOf(Integer.parseInt(device.getStatusWord().split("#")[0])+aLong).concat("#"), device.getSecret()));
                        Log.e("/////////", "////////OnNext////: " + aLong);
                        sendUDPMessage.sendUdpMessage(device.getIp(),rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS)? AppConstants.OFF_2 : AppConstants.ON_2,arguments).toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("/////////", "////////onError/////: " +e);
                        fragment.sendingMessageTimeoutPin2(device.getChipId(), position, device.getType());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("/////////", "////////OnComplete/////: ");
//                        fragment.stopToggleAnimationPin2(device.getChipId());
                        fragment.sendingMessageTimeoutPin2(device.getChipId(), position, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                    }
                });
//        sendUDPMessage.sendUdpMessage(device.getIp(), rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString());
    }

    private Observable<TlmsDoneResponse> getNewStatusWord(Device device){
        return apiService.tlms("http://192.168.1.103/test.php", new BaseRequest(AppConstants.TO_DEVICE_TLMS)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    @SuppressLint("CheckResult")
    public void sendHttpNodeToDevice(String ip){
        apiService.NODE(AppConstants.getDeviceAddress(ip),new BaseRequest(AppConstants.TO_DEVICE_NODE))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<YesResponse>() {
                     @Override
                     public void onSubscribe(Disposable d) {
                         Log.e("ggggggggg","gggggggggg: onSubscribe");
                     }

                     @Override
                     public void onNext(YesResponse yesResponse) {
                         Log.e("gggggggggg","ggggggggg: onNext: "+ yesResponse);
                         Device device = deviceDatabase.getDevice(yesResponse.getSrc());
                         Log.e("gggggggggg","ggggggggg: onNext:Device: "+ device);
                         if (device != null){
                             device.setPin1(yesResponse.getPin1());
                             device.setPin2(yesResponse.getPin2());
                             device.setSsid(yesResponse.getSsid());
                             device.setStyle(yesResponse.getStyle());
                             device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(yesResponse.getStword(),device.getSecret()).split("#")[0])+1));
                             device.setIp(ip);
                             deviceDatabase.updateDevice(device);
                         }
                     }

                     @Override
                     public void onError(Throwable e) {
                         Log.e("ggggggggg","ggggggggg: onError: "+e);
                         e.printStackTrace();
                     }

                     @Override
                     public void onComplete() {
                         Log.e("ggggggggggg","ggggggggggg: onComplete");
                     }
                 });
    }
    @SuppressLint("CheckResult")
    private Observable<ToggleDeviceResponse> toDeviceHttpPin1(Device device){
         return apiService.togglePin1(AppConstants.getDeviceAddress(device.getIp()), new ToggleDevice(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret())))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }
    @SuppressLint("CheckResult")
    private Observable<ToggleDeviceResponse> toDeviceHttpPin2(Device device){
         return apiService.togglePin1(AppConstants.getDeviceAddress(device.getIp()), new ToggleDevice(device.getPin2().equals(AppConstants.ON_STATUS)? AppConstants.OFF_2 : AppConstants.ON_2,Encryptor.encrypt(device.getStatusWord().concat("#"), device.getSecret())))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    HashMap<String , Disposable> mqttBackup = new HashMap<>();
    HashMap<String , Disposable> lastCommand = new HashMap<>();
    @SuppressLint("CheckResult")
    private void sendHttpPin1(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position, boolean withBackup){
        Log.e("******",
                        "\nsendToThisDevice " + device);
        if (withBackup && rayanApplication.getMtd().getListOfAvailableRouts(device.getChipId()).contains(MessageTransmissionDecider.PROTOCOL.MQTT))
        Observable.interval(0,700, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (mqttBackup.get(device.getChipId()+"_1") != null)
                    mqttBackup.get(device.getChipId()+"_1").dispose();
                mqttBackup.put(device.getChipId()+"_1", d);
                Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 1");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, " onNext  Timer executed for Mqtt Backup pin 1: " + aLong);
                if (aLong>0){
//                    Toast.makeText(rayanApplication, "MQTT-BACKUP", Toast.LENGTH_SHORT).show();
                    sendMqttPin1(rayanApplication, device, position, fragment, false, false);
                    mqttBackup.get(device.getChipId()+"_1").dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "OnError Timer executed for Mqtt Backup pin 1: " + e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete  Timer executed for Mqtt Backup pin 1");
            }
        });
        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
                Observable.just(1).flatMap(a -> {
                    Log.e("TAGTAGTAGTAG", "Sending stword is: " + device.getStatusWord());
                    return toDeviceHttpPin1(device);
                })
                .repeatWhen(completed -> {
                    Log.e("TAGTAGTAG", "repeatwhen: " + completed);
                    return completed.delay(200, TimeUnit.MILLISECONDS);
                })
                        .takeWhile(toggleDeviceResponse -> {
                            if (mqttBackup.get(device.getChipId()+"_1") != null && !mqttBackup.get(device.getChipId()+"_1").isDisposed())
                                mqttBackup.get(device.getChipId()+"_1").dispose();
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[0])+1));
                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
                    if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else{
                        Device deviceToUpdate = new Device(device);
                        deviceToUpdate.setPin1(toggleDeviceResponse.getPin1());
                        deviceToUpdate.setPin2(toggleDeviceResponse.getPin2());
                        Log.e("******",
                        "\ndataBaseDevice: " + deviceDatabase.getDevice(device.getChipId())+
                        "\nreplacing this Device: " + device +
                        "\nreplacing this Device: " + deviceToUpdate
                        );
                        deviceDatabase.updateDevice(deviceToUpdate);
//                        fragment.stopToggleAnimationPin1(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                        return false;
                    }
                })
                ,
                (changeNameResponse, deviceResponse) -> {
            return changeNameResponse;})
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(aLong ->{
                    return aLong<1;
                })
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onSubscribe");
//                        fragment.startToggleAnimationPin1(device.getChipId(), position);
                        if (device.getPin1().equals(AppConstants.ON_STATUS))
                            fragment.getDeviceAnimator().turningOffPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        else fragment.getDeviceAnimator().turningOnPin1(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().setWaitingPin1(device.getChipId(), d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onNext: " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mqttBackup.get(device.getChipId()+"_1") != null && !mqttBackup.get(device.getChipId()+"_1").isDisposed())
                            mqttBackup.get(device.getChipId()+"_1").dispose();
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
                        fragment.sendingMessageTimeoutPin1(device.getChipId(), position, device.getType());
//                        fragment.stopToggleAnimationPin1(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
                        if (mqttBackup.get(device.getChipId()+"_1") != null && !mqttBackup.get(device.getChipId()+"_1").isDisposed())
                            mqttBackup.get(device.getChipId()+"_1").dispose();
                        fragment.sendingMessageTimeoutPin1(device.getChipId(), position, device.getType());
//                        fragment.stopToggleAnimationPin1(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                    }
                });
    }
    private void sendHttpPin2(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position, boolean withBackup){
        if (withBackup && rayanApplication.getMtd().getListOfAvailableRouts(device.getChipId()).contains(MessageTransmissionDecider.PROTOCOL.MQTT))
            Observable.interval(0,700, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (mqttBackup.get(device.getChipId()+"_2") != null)
                        mqttBackup.get(device.getChipId()+"_2").dispose();
                    mqttBackup.put(device.getChipId()+"_2", d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 2");
                }

                @Override
                public void onNext(Long aLong) {
                    Log.e(TAG, " onNext  Timer executed for Mqtt Backup pin 2: " + aLong);
                    if (aLong>0){
//                        Toast.makeText(rayanApplication, "MQTT-BACKUP", Toast.LENGTH_SHORT).show();
                        sendMqttPin2(rayanApplication, device, position, fragment, false,false);
                        mqttBackup.get(device.getChipId()+"_2").dispose();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "OnError Timer executed for Mqtt Backup pin 2: " + e);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    Log.e(TAG, "onComplete  Timer executed for Mqtt Backup pin 2");
                }
            });
        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
                Observable.just(1).flatMap(a -> {
                    Log.e("TAGTAGTAGTAG", "Sending stword is: " + device.getStatusWord());
                    return toDeviceHttpPin2(device);
                })
                .repeatWhen(completed -> {
                    Log.e("TAGTAGTAG", "Should I go in repeatwhen: " + completed);
                    return completed.delay(200, TimeUnit.MILLISECONDS);
                })
                        .takeWhile(toggleDeviceResponse -> {
                            if (mqttBackup.get(device.getChipId()+"_2") != null && !mqttBackup.get(device.getChipId()+"_2").isDisposed())
                                mqttBackup.get(device.getChipId()+"_2").dispose();
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[0])+1));
                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
                    if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
                        return true;
                    else{
                        device.setPin1(toggleDeviceResponse.getPin1());
                        device.setPin2(toggleDeviceResponse.getPin2());
                        deviceDatabase.updateDevice(device);
//                        fragment.stopToggleAnimationPin2(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                        return false;
                    }
                })
                ,
                (changeNameResponse, deviceResponse) -> {
            return changeNameResponse;})
                .timeout(4, TimeUnit.SECONDS)
                .takeWhile(aLong -> aLong<1)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onSubscribe");
//                        fragment.startToggleAnimationPin2(device.getChipId(), position);
                        if (device.getPin2().equals(AppConstants.ON_STATUS))
                            fragment.getDeviceAnimator().turningOffPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        else fragment.getDeviceAnimator().turningOnPin2(device.getChipId(), position, (ToggleDeviceAnimationProgress)fragment, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().setWaitingPin2(device.getChipId(), d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onNext: " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
                        if (mqttBackup.get(device.getChipId()+"_2") != null && !mqttBackup.get(device.getChipId()+"_2").isDisposed())
                            mqttBackup.get(device.getChipId()+"_2").dispose();
                        fragment.sendingMessageTimeoutPin2(device.getChipId(), position, device.getType());
//                        fragment.stopToggleAnimationPin2(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
                        if (mqttBackup.get(device.getChipId()+"_2") != null && !mqttBackup.get(device.getChipId()+"_2").isDisposed())
                            mqttBackup.get(device.getChipId()+"_2").dispose();
//                        fragment.stopToggleAnimationPin2(device.getChipId());
                        fragment.sendingMessageTimeoutPin2(device.getChipId(), position, device.getType());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                    }
                });
    }

    public void toggleDevicePin1(DialogPresenter dp, ToggleDeviceAnimationProgress fragment, Device device, int position, RayanApplication rayanApplication){
        String cr = rayanApplication.getMtd().requestForSendMessage(device);
        Log.e(TAG,"Be Chi Befrestam? : " + cr);
//        Toast.makeText(rayanApplication, cr, Toast.LENGTH_SHORT).show();
        switch (cr){
            case "UDP":
                sendUdpPin1(device, rayanApplication,fragment, position);
                break;
            case "MQTT":
                sendMqttPin1(rayanApplication,device, position, fragment, true, RayanApplication.getPref().getIsNodeSoundOn());
                break;
            case "STANDALONE":
                //Toast.makeText(rayanApplication, "دستگاه فقط از طریق اتصال مستقیم قابل دسترسی است", Toast.LENGTH_SHORT).show();
                break;
            case "HTTP":
                sendHttpPin1(device, rayanApplication, fragment, position, RayanApplication.getPref().getIsNodeSoundOn());
                break;
            case "NONE":
                Map<String,String> params = new HashMap<>();
                params.put("message", "دستگاه از هیچ طریقی قابل دسترسی نیست");
                dp.showDialog(AppConstants.DIALOG_ALERT, params);
//                //Toast.makeText(rayanApplication, "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void toggleDevicePin2(DialogPresenter dp, ToggleDeviceAnimationProgress fragment, Device device, int position, RayanApplication rayanApplication){
        String cr = rayanApplication.getMtd().requestForSendMessage(device);
        Log.e(TAG,"Be Chi Befrestam2? : " + cr);
//        Toast.makeText(rayanApplication, cr, Toast.LENGTH_SHORT).show();
        switch (cr){
            case "UDP":
                sendUdpPin2(device, rayanApplication,fragment, position);
                break;
            case "MQTT":
                sendMqttPin2(rayanApplication,device, position, fragment, true,RayanApplication.getPref().getIsNodeSoundOn());
                break;
            case "STANDALONE":
                //Toast.makeText(rayanApplication, "دستگاه فقط از طریق اتصال مستقیم قابل دسترسی است", Toast.LENGTH_SHORT).show();
                break;
            case "HTTP":
                sendHttpPin2(device, rayanApplication, fragment, position, RayanApplication.getPref().getIsNodeSoundOn());
                break;
            case "NONE":
                Map<String,String> params = new HashMap<>();
                params.put("message", "دستگاه از هیچ طریقی قابل دسترسی نیست");
                dp.showDialog(AppConstants.DIALOG_ALERT, params);
//                //Toast.makeText(rayanApplication, "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
