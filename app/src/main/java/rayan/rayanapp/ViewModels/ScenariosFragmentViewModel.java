package rayan.rayanapp.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;

import org.apache.commons.net.util.Base64;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Helper.MessageTransmissionDecider;
import rayan.rayanapp.Helper.RayanUtils;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.Persistance.database.ScenarioDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device.ToggleDeviceWithLastCommand;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.ToggleDeviceResponse;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Util.AppConstants;
import retrofit2.Response;

public class ScenariosFragmentViewModel extends DevicesFragmentViewModel{

    private final String TAG = this.getClass().getCanonicalName();
    GroupDatabase groupDatabase;
    ScenarioDatabase scenarioDatabase;
    ApiService apiService;
    ApiService apiServiceScalar;
    HashMap<String , Disposable> mqttBackup = new HashMap<>();
    HashMap<String , Disposable> lastCommand = new HashMap<>();

    public ScenariosFragmentViewModel(@NonNull Application application) {
        super(application);
        groupDatabase = new GroupDatabase(application);
        scenarioDatabase = new ScenarioDatabase(application);
        apiService = ApiUtils.getApiService();
        apiServiceScalar = ApiUtils.getApiServiceScalar();
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
                    rayanApplication.getScenariosMqttMessagesController().messageSent(chipId);
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

    public LiveData<List<Scenario>> getAllScenariosLive(){
        return scenarioDatabase.getScenariosLive();
    }

    public Scenario getScenario(int id){
        return scenarioDatabase.getScenario(id);
    }

    public void sendMqttPin1(RayanApplication rayanApplication, Device device, boolean on, boolean lastCommandRetain){
        if (lastCommandRetain)
            Observable.interval(0, AppConstants.ATTACH_MQTT_REQ_TO_TOPIC_TIMEOUT, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1) != null)
                        lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1).dispose();
                    lastCommand.put(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1, d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 2");
                }

                @Override
                public void onNext(Long aLong) {
                    if (aLong > 0) {
                        Log.e(TAG, "Sending last Command...");
                        JSONObject lastMessage = null;
                        try {
                            lastMessage = rayanApplication.getScenariosMqttMessagesController().getLastMessageOfDevice(device.getChipId());
                            List<String> arguments = new ArrayList<>();
                            arguments.add(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
                            arguments.add(Boolean.toString(lastCommandRetain));
                            if (lastMessage != null){
                                if (!lastMessage.has("lc")){
                                    //without lc
                                    lastMessage.put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.OFF_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }
                                else if (lastMessage.getJSONObject("lc").getString("cmd").contains("1")){
                                    //with lc1 pin 1
                                    if (lastMessage.getJSONObject("lc").has("lc")){
                                        Object lc2 = lastMessage.getJSONObject("lc").get("lc");
                                        lastMessage.put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.OFF_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                        lastMessage.getJSONObject("lc").put("lc", lc2);
                                    }else {
                                        lastMessage.put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.OFF_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                    }
                                }
                                else if (((JSONObject)lastMessage.get("lc")).has("lc") && lastMessage.getJSONObject("lc").getJSONObject("lc").getString("cmd").contains("1")){
                                    //with lc2 pin 1
                                    lastMessage.getJSONObject("lc").put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.OFF_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }
                                else if (!lastMessage.getJSONObject("lc").has("lc")){
                                    //with Just one LC
                                    ((JSONObject)lastMessage.get("lc")).put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.OFF_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
                                }

                            }
//                            if (lastMessage != null) {
//                                if (!lastMessage.has("lc") || !((JSONObject)lastMessage.get("lc")).has("lc") && ((String)(lastMessage).get("cmd")).contains("1"))
//                                    lastMessage.put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
//                                else {
//                                    JSONObject lc2 = (JSONObject) ((JSONObject)lastMessage.get("lc")).get("lc");
//                                    lastMessage.put("lc", rayanApplication.getJSON(device.getPin1().equals(AppConstants.ON_STATUS) ? AppConstants.OFF_1 : AppConstants.ON_1, arguments));
//                                    ((JSONObject)lastMessage.get("lc")).put("lc",lc2);
//                                }
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Lastmessage is : " + lastMessage);
                        if (lastMessage != null && !rayanApplication.getScenariosMqttMessagesController().isReceivedResponse(device.getChipId())){
//                            Toast.makeText(rayanApplication, "Command Attached", Toast.LENGTH_SHORT).show();
                            publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), lastMessage.toString(), 0, true);
                        }
                        lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1).dispose();
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
        arguments.add(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(lastCommandRetain));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJSON(on? AppConstants.ON_1:AppConstants.OFF_1,arguments).toString(), 0, false);
    }

    public void sendMqttPin1Pin2(RayanApplication rayanApplication, Device device, JsonObject message, boolean lastCommandRetain){
        if (lastCommandRetain)
            Observable.interval(0, AppConstants.ATTACH_MQTT_REQ_TO_TOPIC_TIMEOUT, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1_PIN2) != null)
                        lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
                    lastCommand.put(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1_PIN2, d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 1 Pin2");
                }

                @Override
                public void onNext(Long aLong) {
                    if (aLong > 0) {
                        Log.e(TAG, "Sending last Command...");
                        JSONObject lastMessage = null;
                        try {
                            lastMessage = rayanApplication.getScenariosMqttMessagesController().getLastMessageOfDevice(device.getChipId());
                            List<String> arguments = new ArrayList<>();
                            arguments.add(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
                            arguments.add(Boolean.toString(lastCommandRetain));
                            if (lastMessage != null){
                                    if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                                        lastMessage.putOpt("lc", rayanApplication.getJSON(AppConstants.ON_1_ON_2, arguments));
                                    else if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                                        lastMessage.putOpt("lc", rayanApplication.getJSON(AppConstants.ON_1_OFF_2, arguments));
                                    else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                                        lastMessage.putOpt("lc", rayanApplication.getJSON(AppConstants.OFF_1_ON_2, arguments));
                                    else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                                        lastMessage.putOpt("lc", rayanApplication.getJSON(AppConstants.OFF_1_OFF_2, arguments));
                                    }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Lastmessage is : " + lastMessage);
                        if (lastMessage != null && !rayanApplication.getScenariosMqttMessagesController().isReceivedResponse(device.getChipId())){
//                            Toast.makeText(rayanApplication, "Command Attached", Toast.LENGTH_SHORT).show();
                            publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), lastMessage.toString(), 0, true);
                        }
                        lastCommand.get(device.getChipId() + AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
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
        arguments.add(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
        arguments.add(Boolean.toString(true));
        publishMqtt(device.getChipId(), rayanApplication, MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), message.toString(), 0, false);
        //rayanApplication.getJSON(on? AppConstants.ON_2 : AppConstants.OFF_2 ,arguments)
    }

    @SuppressLint("CheckResult")
    private Observable<Response<String>> toDeviceHttpPin1(Device device, boolean on) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return apiServiceScalar.togglePin1(sha1(new ToggleDevice(on? AppConstants.ON_1 : AppConstants.OFF_1,Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())).ToString(), device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), new ToggleDevice(on? AppConstants.ON_1 : AppConstants.OFF_1,Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret())))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }
    @SuppressLint("CheckResult")
    private Observable<Response<String>> toDeviceHttpPin1Pin2(Device device){
        try {
            String stword = Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret());
//        return apiService.togglePin1Pin2(AppConstants.getDeviceAddress(device.getIp()), new ToggleDeviceWithLastCommand(AppConstants.ON_1)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                return apiServiceScalar.togglePin1Pin2(AppConstants.sha1(new ToggleDeviceWithLastCommand(AppConstants.ON_1, stword, AppConstants.ON_2).ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()), new ToggleDeviceWithLastCommand(AppConstants.ON_1, stword, AppConstants.ON_2)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            else if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                return apiServiceScalar.togglePin1Pin2(AppConstants.sha1(new ToggleDeviceWithLastCommand(AppConstants.ON_1, stword, AppConstants.OFF_2).ToString(), device.getSecret()), AppConstants.getDeviceAddress(device.getIp()), new ToggleDeviceWithLastCommand(AppConstants.ON_1, stword, AppConstants.OFF_2)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                return apiServiceScalar.togglePin1Pin2(AppConstants.sha1(new ToggleDeviceWithLastCommand(AppConstants.OFF_1, stword, AppConstants.ON_2).ToString(),device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), new ToggleDeviceWithLastCommand(AppConstants.OFF_1, stword, AppConstants.ON_2)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                return apiServiceScalar.togglePin1Pin2(AppConstants.sha1(new ToggleDeviceWithLastCommand(AppConstants.OFF_1, stword, AppConstants.OFF_2).ToString(), device.getSecret()),AppConstants.getDeviceAddress(device.getIp()), new ToggleDeviceWithLastCommand(AppConstants.OFF_1, stword, AppConstants.OFF_2)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendHttpPin1(Device device, boolean on, boolean withBackup, boolean lastCommandRetain){
        Log.e("******", "\nsendToThisDevice " + device);
        if (withBackup && ((RayanApplication)getApplication()).getMtd().getListOfAvailableRouts(device.getChipId()).contains(MessageTransmissionDecider.PROTOCOL.MQTT))
            Observable.interval(0,700, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null)
                        mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
                    mqttBackup.put(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1, d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 1");
                }

                @Override
                public void onNext(Long aLong) {
                    Log.e(TAG, " onNext  Timer executed for Mqtt Backup pin 1: " + aLong);
                    if (aLong>0){
//                    Toast.makeText(rayanApplication, "MQTT-BACKUP", Toast.LENGTH_SHORT).show();
                        sendMqttPin1((RayanApplication) getApplication(), device, device.getPin1().equals(AppConstants.OFF_STATUS), lastCommandRetain);
                        mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
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
        Observable.just(device)
                .flatMap(new Function<Device, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(Device device) throws Exception {
                        return toDeviceHttpPin1(device, on);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response ->{
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                        Log.e("rererererere", "Auth Is Not NUll");
                        if (sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                            Log.e("rererererere", "HMACs Are Equal");
                            ToggleDeviceResponse toggleDeviceResponse = RayanUtils.convertToObject(ToggleDeviceResponse.class, response.body());
                            if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
                                mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[1])+1));
                            device.setHeader(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[0]);
                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse.getCmd());
                            if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
                                return true;
                            else if (toggleDeviceResponse.getCmd().equals(AppConstants.DEVICE_TOGGLE)){
                                Device deviceToUpdate = new Device(device);
                                deviceToUpdate.setPin1(toggleDeviceResponse.getPin1());
                                deviceToUpdate.setPin2(toggleDeviceResponse.getPin2());
                                deviceDatabase.updateDevice(deviceToUpdate);
                                return false;
                            }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> toggleDeviceResponseResponse) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
                    }
                });

//        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
//                Observable.just(1).flatMap(a -> {
//                    Log.e("TAGTAGTAGTAG", "Sending stword is: " + device.getStatusWord());
//                    return toDeviceHttpPin1(device, on);
//                })
//                        .repeatWhen(completed -> {
//                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
//                            return completed.delay(200, TimeUnit.MILLISECONDS);
//                        })
//                        .takeWhile(toggleDeviceResponse -> {
//                            if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
//                                mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
//                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.body().getStword(),device.getSecret()).split("#")[1])+1));
//                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
//                            if (toggleDeviceResponse.body().getCmd().equals("wrong_stword"))
//                                return true;
//                            else{
//                                Device deviceToUpdate = new Device(device);
//                                deviceToUpdate.setPin1(toggleDeviceResponse.body().getPin1());
//                                deviceToUpdate.setPin2(toggleDeviceResponse.body().getPin2());
//                                Log.e("******",
//                                        "\ndataBaseDevice: " + deviceDatabase.getDevice(device.getChipId())+
//                                                "\nreplacing this Device: " + device +
//                                                "\nreplacing this Device: " + deviceToUpdate
//                                );
//                                deviceDatabase.updateDevice(deviceToUpdate);
//                                return false;
//                            }
//                        })
//                ,
//                (changeNameResponse, deviceResponse) -> {
//                    return changeNameResponse;})
//                .timeout(4, TimeUnit.SECONDS)
//                .takeWhile(aLong ->{
//                    return aLong<1;
//                })
//                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onNext: " + aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
//                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
//                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).isDisposed())
//                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1).dispose();
//                    }
//                });
    }

    Disposable d;
    private void sendHttpPin1Pin2(Device device, boolean withBackup, boolean backupHasRetainLc){
        Log.e("******", "\nsendToThisDevice " + device);
        if (withBackup && ((RayanApplication)getApplication()).getMtd().getListOfAvailableRouts(device.getChipId()).contains(MessageTransmissionDecider.PROTOCOL.MQTT))
            Observable.interval(0,700, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null)
                        mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
                    mqttBackup.put(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2, d);
                    Log.e(TAG, "onSubscribe Timer executed for Mqtt Backup pin 1");
                }

                @Override
                public void onNext(Long aLong) {
                    Log.e(TAG, " onNext  Timer executed for Mqtt Backup pin 1_ PIN 2: " + aLong);
                    if (aLong>0){
                        List<String> args = new ArrayList<>();
                        args.add(Encryptor.encrypt(device.getHeader().concat("#").concat(device.getStatusWord()).concat("#"), device.getSecret()));
                        if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                            sendMqttPin1Pin2((RayanApplication) getApplication(),device, ((RayanApplication)getApplication()).getJson(AppConstants.ON_1_ON_2, args), backupHasRetainLc);
                        else if (device.getPin1().equals(AppConstants.ON_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                            sendMqttPin1Pin2((RayanApplication) getApplication(),device, ((RayanApplication)getApplication()).getJson(AppConstants.ON_1_OFF_2, args), backupHasRetainLc);
                        else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.ON_STATUS))
                            sendMqttPin1Pin2((RayanApplication) getApplication(),device, ((RayanApplication)getApplication()).getJson(AppConstants.OFF_1_ON_2, args), backupHasRetainLc);
                        else if (device.getPin1().equals(AppConstants.OFF_STATUS) && device.getPin2().equals(AppConstants.OFF_STATUS))
                            sendMqttPin1Pin2((RayanApplication) getApplication(),device, ((RayanApplication)getApplication()).getJson(AppConstants.OFF_1_OFF_2, args), backupHasRetainLc);
                        mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
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

        Observable.just(device)
                .flatMap(new Function<Device, Observable<Response<String>>>() {
                    @Override
                    public Observable<Response<String>> apply(Device device) throws Exception {
                        return toDeviceHttpPin1Pin2(device);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .repeatWhen(throwableObservable -> throwableObservable.delay(200, TimeUnit.MILLISECONDS))
                .takeWhile(response ->{
                    Log.e("rerererererere", "Toggle Device Response: " + response);
                    Log.e("rerererererere", "Auth: " + response.headers().get("auth"));
                    Log.e("rerererererere", "Body Expected To Be: " + response.body());
                    Log.e("rerererererere", "Sending Status Word Is: " + device.getStatusWord());
                    Log.e("rerererererere", "HMAC of Body: " + sha1(response.body(), device.getSecret()));
                    if (response.headers().get("auth") != null){
                        Log.e("rererererere", "Auth Is Not NUll");
                        if (sha1(response.body(), device.getSecret()).equals(response.headers().get("auth"))){
                            Log.e("rererererere", "HMACs Are Equal");
                            ToggleDeviceResponse toggleDeviceResponse = RayanUtils.convertToObject(ToggleDeviceResponse.class, response.body());
                            if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
                                mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[1])+1));
                            device.setHeader(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[0]);
                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse.getCmd());
                            if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
                                return true;
                            else if (toggleDeviceResponse.getCmd().equals(AppConstants.DEVICE_TOGGLE)){
                                Device deviceToUpdate = new Device(device);
                                deviceToUpdate.setPin1(toggleDeviceResponse.getPin1());
                                deviceToUpdate.setPin2(toggleDeviceResponse.getPin2());
                                deviceDatabase.updateDevice(deviceToUpdate);
                                return false;
                            }
                        }else Log.e("rererererere", "HMACs Are NOT Equal");
                    }else Log.e("rererererere", "Auth IS NULL");
                    return false;
                }).timeout(4, TimeUnit.SECONDS)
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        ScenariosFragmentViewModel.this.d = d;
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onSubscribe");
                    }

                    @Override
                    public void onNext(Response<String> toggleDeviceResponseResponse) {
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onNext: " + toggleDeviceResponseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
                        d.dispose();
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        d.dispose();
                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
                    }
                });
//        Observable.zip(Observable.interval(0, AppConstants.HTTP_MESSAGING_TIMEOUT, TimeUnit.MILLISECONDS),
//                Observable.just(1).flatMap(a -> {
//                    Log.e("TAGTAGTAGTAG", "Sending stword is: " + device.getStatusWord());
//                    return toDeviceHttpPin1Pin2(device);
//                })
//                        .repeatWhen(completed -> {
//                            Log.e("TAGTAGTAG", "repeatwhen: " + completed);
//                            return completed.delay(200, TimeUnit.MILLISECONDS);
//                        })
//                        .takeWhile(toggleDeviceResponse -> {
//                            if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
//                                mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
//                            device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(toggleDeviceResponse.getStword(),device.getSecret()).split("#")[1])+1));
//                            Log.e("TAGTAGTAG", "Should I go: " + toggleDeviceResponse);
////                            if (toggleDeviceResponse.getMsg() != null)
//                            if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
//                                return true;
//                            else{
//                                Device deviceToUpdate = new Device(device);
//                                deviceToUpdate.setPin1(toggleDeviceResponse.getPin1());
//                                deviceToUpdate.setPin2(toggleDeviceResponse.getPin2());
//                                Log.e("******",
//                                        "\ndataBaseDevice: " + deviceDatabase.getDevice(device.getChipId())+
//                                                "\nreplacing this Device: " + device +
//                                                "\nreplacing this Device: " + deviceToUpdate
//                                );
//                                deviceDatabase.updateDevice(deviceToUpdate);
//                                return false;
//                            }
//                        })
//                ,
//                (changeNameResponse, deviceResponse) -> {
//                    return changeNameResponse;})
//                .timeout(4, TimeUnit.SECONDS)
//                .takeWhile(aLong ->{
//                    return aLong<1;
//                })
//                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        ScenariosFragmentViewModel.this.d = d;
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz: onNext: " + aLong);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
//                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
//                        d.dispose();
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onError: " + e);
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        d.dispose();
//                        Log.e("zzzzzzzzzzzz","zzzzzzzzzzz:onComplete ");
//                        if (mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2) != null && !mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).isDisposed())
//                            mqttBackup.get(device.getChipId()+AppConstants.NAMING_PREFIX_PIN1_PIN2).dispose();
//                    }
//                });
    }

    public Flowable<List<Group>> getAllGroupsFlowable(){
        return groupDatabase.getAllGroupsFlowable();
    }

    public void deleteScenarioWithId(int id){
        scenarioDatabase.deleteScenarioWithId(id);
    }

    public String getMessageRoute(Device device){
        return ((RayanApplication)getApplication()).getMtd().requestForSendMessage(device);
    }

    public void sendMessageToDevicePin1(Device device,boolean on){
        String mr = getMessageRoute(device);
        Log.e(TAG, "pin 1 message root for this device is: " + mr);
        switch (mr){
            case AppConstants.MESSAGE_ROUTE_HTTP:
                sendHttpPin1(device, on, true, true);
//                sendMqttPin1((RayanApplication)getApplication(), device, on);
            case AppConstants.MESSAGE_ROUTE_MQTT:
                sendMqttPin1((RayanApplication)getApplication(), device, on, true);
                break;
                default:
        }
    }

    public void sendMessageToDevicePin1Pin2(Device device, JsonObject message){
        String mr = getMessageRoute(device);
        Log.e(TAG, "pin 1 & pin 2 message route for: " +device.getChipId()+" is: " + mr + message);
        switch (mr){
            case AppConstants.MESSAGE_ROUTE_HTTP:
                sendHttpPin1Pin2(device, true, true);
                break;
            case AppConstants.MESSAGE_ROUTE_MQTT:
                sendMqttPin1Pin2((RayanApplication)getApplication(), device, message, true);
                break;
            default:
        }
    }

    public static String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        return new String( Base64.encodeBase64(bytes) );
    }
}
