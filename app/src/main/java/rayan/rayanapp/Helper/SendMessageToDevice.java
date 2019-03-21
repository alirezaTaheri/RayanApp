package rayan.rayanapp.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ToggleDeviceAnimationProgress;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.Models.Requests.device.BaseRequest;
import rayan.rayanapp.Retrofit.Models.Requests.device.ToggleDevice;
import rayan.rayanapp.Retrofit.Models.Responses.device.TlmsDoneResponse;
import rayan.rayanapp.Retrofit.Models.Responses.device.ToggleDeviceResponse;
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

    private void publishMqtt(Connection connection, String topic, String message, int qos, boolean retain){
        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
//            final ActionListener callback = new ActionListener(context,
//                    ActionListener.Action.PUBLISH, connection, actionArgs);
            IMqttActionListener iMqttActionListener = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(TAG, "onSuccess Publish message" + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "onFailure Publish message");
                }
            };
            connection.getClient().publish(topic, message.getBytes(), qos, retain, null, iMqttActionListener);
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }

    Observable<Long> counterObservable = Observable.interval(0,700,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    Observable<Long> timerObservable = Observable.timer(4, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());

    private void sendMqttPin1(RayanApplication rayanApplication, Device device, int position, ToggleDeviceAnimationProgress fragment){
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord(), device.getSecret()));
        publishMqtt(MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString(), 0, false);
            timerObservable.subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    fragment.startToggleAnimationPin1(device.getChipId(), position);
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
                    fragment.stopToggleAnimationPin1(device.getChipId());
                    rayanApplication.getDevicesAccessibilityBus().removeWaitingPin1(device.getChipId());
                }
            });
    }

    private void sendMqttPin2(RayanApplication rayanApplication, Device device, int position, ToggleDeviceAnimationProgress fragment){
        List<String> arguments = new ArrayList<>();
        arguments.add(Encryptor.encrypt(device.getStatusWord(), device.getSecret()));
        publishMqtt(MainActivityViewModel.connection.getValue(), device.getTopic().getTopic(), rayanApplication.getJson(device.getPin2().equals(AppConstants.ON_STATUS)? AppConstants.OFF_2 : AppConstants.ON_2,arguments).toString(), 0, false);
        timerObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                fragment.startToggleAnimationPin2(device.getChipId(), position);
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
                fragment.stopToggleAnimationPin2(device.getChipId());
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
                        fragment.startToggleAnimationPin1(device.getChipId(), position);
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
                    }

                    @Override
                    public void onComplete() {
                        Log.e("/////////", "////////OnComplete/////: ");
                        fragment.stopToggleAnimationPin1(device.getChipId());
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
                        fragment.startToggleAnimationPin2(device.getChipId(), position);
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
                    }

                    @Override
                    public void onComplete() {
                        Log.e("/////////", "////////OnComplete/////: ");
                        fragment.stopToggleAnimationPin2(device.getChipId());
                        rayanApplication.getDevicesAccessibilityBus().removeWaitingPin2(device.getChipId());
                    }
                });
//        sendUDPMessage.sendUdpMessage(device.getIp(), rayanApplication.getJson(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,arguments).toString());
    }

    private Observable<TlmsDoneResponse> getNewStatusWord(Device device){
        return apiService.tlms("http://192.168.1.103/test.php", new BaseRequest(AppConstants.TO_DEVICE_TLMS)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    @SuppressLint("CheckResult")
    private Observable<ToggleDeviceResponse> sendHttpPin1(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position){
         return apiService.togglePin1("http://192.168.1.103/test.php", new ToggleDevice(device.getPin1().equals(AppConstants.ON_STATUS)? AppConstants.OFF_1 : AppConstants.ON_1,device.getStatusWord()))
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
//                .subscribe(new Observer<ToggleDeviceResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("/////////s", "subscribe");
//                    }
//
//                    @Override
//                    public void onNext(ToggleDeviceResponse toggleDeviceResponse) {
//                        Log.e("/////////s", "onNext");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("/////////s", "onError");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("/////////s", "onComplete");
//                    }
//                });

//                .concatMap(toggleDeviceResponse -> {
//                    if (toggleDeviceResponse.getCmd().equals("wrong_stword"))
//                        return getNewStatusWord(device);
//                    else if (toggleDeviceResponse.getCmd().equals("toggle")){
//                        device.setPin1(toggleDeviceResponse.getPin1());
//                        device.setPin2(toggleDeviceResponse.getPin2());
//                        device.setStatusWord(String.valueOf(Integer.parseInt(Encryptor.decrypt(device.getStatusWord(),device.getSecret()).split("#")[0])+1) + "#");
//                        deviceDatabase.updateDevice(device);
//                    }
//                    return null;
//                });
    }

    @SuppressLint("CheckResult")
    private void sendToInsurance(Device device, RayanApplication rayanApplication, ToggleDeviceAnimationProgress fragment, int position){

        sendHttpPin1(device, rayanApplication, fragment, position)
                .flatMap(toggleDeviceResponse -> {
                    Log.e("rrrrrrrrrrr", "response is: " + toggleDeviceResponse + String.valueOf(Integer.parseInt(toggleDeviceResponse.getStword())+1));
                    if (toggleDeviceResponse.getCmd().equals("wrong_stword")){
                        device.setStatusWord(String.valueOf(Integer.parseInt(toggleDeviceResponse.getStword())+1));
                        return sendHttpPin1(device, rayanApplication, fragment, position);
                    }
                    else {
                        device.setPin1(toggleDeviceResponse.getPin1());
                        device.setPin2(toggleDeviceResponse.getPin2());
                        device.setStatusWord(String.valueOf(Integer.parseInt(toggleDeviceResponse.getStword())+1));
                        deviceDatabase.updateDevice(device);
                    }
                    return null;
                })
                .repeat()
                .takeWhile(tlmsDoneResponse -> {
                    return tlmsDoneResponse != null;
        })
                .subscribe(new Observer<ToggleDeviceResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("/////////s", "subscribe");
            }

            @Override
            public void onNext(ToggleDeviceResponse tlmsDoneResponse) {
                Log.e("/////////s", "onNext" + tlmsDoneResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("/////////s", "onError" + e);
            }

            @Override
            public void onComplete() {
                Log.e("/////////s", "onComplete");
            }
        });
    }

    public void toggleDevicePin1(ToggleDeviceAnimationProgress fragment, Device device, int position, RayanApplication rayanApplication){
        Log.e(TAG,"Be Chi Befrestam? : " + rayanApplication.getMtd().sendMessage(device));
        Toast.makeText(rayanApplication, rayanApplication.getMtd().sendMessage(device), Toast.LENGTH_SHORT).show();
        sendToInsurance(device, rayanApplication, fragment, position);
//        switch (rayanApplication.getMtd().sendMessage(device)){
//            case "UDP":
//                sendUdpPin1(device, rayanApplication,fragment, position);
//                break;
//            case "MQTT":
//                sendMqttPin1(rayanApplication,device, position, fragment);
//                break;
//            case "STANDALONE":
//                Toast.makeText(rayanApplication, "دستگاه فقط از طریق اتصال مستقیم قابل دسترسی است", Toast.LENGTH_SHORT).show();
//                break;
//            case "HTTP":
//                break;
//            case "NONE":
//                Toast.makeText(rayanApplication, "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
//                break;
//        }
    }
    public void toggleDevicePin2(ToggleDeviceAnimationProgress fragment, Device device, int position, RayanApplication rayanApplication){
        Log.e(TAG,"Be Chi Befrestam2? : " + rayanApplication.getMtd().sendMessage(device));
        Toast.makeText(rayanApplication, rayanApplication.getMtd().sendMessage(device), Toast.LENGTH_SHORT).show();
        switch (rayanApplication.getMtd().sendMessage(device)){
            case "UDP":
                sendUdpPin2(device, rayanApplication,fragment, position);
                break;
            case "MQTT":
                sendMqttPin2(rayanApplication,device, position, fragment);
                break;
            case "STANDALONE":
                Toast.makeText(rayanApplication, "دستگاه فقط از طریق اتصال مستقیم قابل دسترسی است", Toast.LENGTH_SHORT).show();
                break;
            case "HTTP":
                break;
            case "NONE":
                Toast.makeText(rayanApplication, "دستگاه در دسترس نیست", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
