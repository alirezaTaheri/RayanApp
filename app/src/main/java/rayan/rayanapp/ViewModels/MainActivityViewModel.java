package rayan.rayanapp.ViewModels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;
import android.util.Log;

import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Mqtt.MqttClientService;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.AddDeviceToGroupRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api.DeleteUserRequest;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.BaseResponse;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Services.mqtt.ActionListener;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.mqtt.MyMqttCallbackHandler;
import rayan.rayanapp.Services.mqtt.model.Subscription;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;


public class MainActivityViewModel extends DevicesFragmentViewModel {

    private final String TAG = MainActivityViewModel.class.getSimpleName();
    private ExecutorService executorService;
    private DeviceDatabase deviceDatabase;
    private SendUDPMessage sendUDPMessage;
    private GroupDatabase groupDatabase;
    private boolean mqttSsl;
    SoundPool soundPool_off, soundPool_on, soundPool;
    int loadedNotification;
    int soundId_on, soundId_off;
    public static MutableLiveData<Connection> connection = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        deviceDatabase = new DeviceDatabase(application);
        sendUDPMessage = new SendUDPMessage();
        groupDatabase = new GroupDatabase(application);
        soundPool_on = new SoundPool(5, AudioManager.STREAM_NOTIFICATION , 0);
        soundPool_off = new SoundPool(5, AudioManager.STREAM_NOTIFICATION , 0);
        soundId_on = soundPool_on.load(getApplication(), R.raw.sound_switch_on, 1);
        soundId_off = soundPool_off.load(getApplication(), R.raw.sound_off_1, 1);

//        Uri uriSound = RingtoneManager.getActualDefaultRingtoneUri(getApplication(), RingtoneManager.TYPE_NOTIFICATION);
//        String soundPath = getRealPathFromURI(uriSound);
//        AudioManager audio = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
//        final int currentVolume = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
//
//        //create pool, load system notification sound, when load - set volume and play
//        soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
//        loadedNotification = soundPool.load(soundPath, 1);
//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
//                soundPool.setVolume(loadedNotification, currentVolume, currentVolume);
//                soundPool.play(loadedNotification, 1, 1, 1, 0, 1);
//            }
//        });

//        connection = new MutableLiveData<>();
//        connection.setValue(Connection.createConnection("ClientHandle","ClientId","api.rayansmarthome.ir",1883,application,false));
    }

    public List<Group> getAllGroups(){
        return groupDatabase.getAllGroups();
    }

    public Flowable<List<Device>> getAllDevicesFlowable(){
        return deviceDatabase.getAllDevicesFlowable().observeOn(Schedulers.io());
    }

    public Flowable<List<Group>> getAllGroupsFlowable(){
        return groupDatabase.getAllGroupsFlowable();
    }

    public Group getGroup(String id){
        return groupDatabase.getGroup(id);
    }
    public List<Device> getAllDevices(){
        return deviceDatabase.getAllDevices();
    }

    public void connectToMqtt(Context context){
        Log.e("MainActivityViewModel", "........................");
        disconnectMQTT(MainActivityViewModel.connection);
//        MutableLiveData<Connection> updateConnection = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                    mqttConnectOptions.setAutomaticReconnect(true);
                    mqttConnectOptions.setCleanSession(false);
                    mqttConnectOptions.setConnectionTimeout(5);
                    mqttConnectOptions.setKeepAliveInterval(500);
                    InputStream input =
                            context.getApplicationContext().getAssets().open("ca_certificate.pem");
                    Log.e("/////////////" ,"/////////////Input: " + input);
                    Connection connection = Connection.createConnection("ClientHandle" + System.currentTimeMillis(),
//                        "ClientId"+ System.currentTimeMillis(),
                            RayanApplication.getPref().getId(),
                            AppConstants.MQTT_HOST,
//                            mqttSsl?
                                    AppConstants.MQTT_PORT_SSL,
//                                    :AppConstants.MQTT_PORT,
                            context,
                            true);
                    connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
//                connection.setSubscriptions(getSubscriptions(connection));
                    Log.e("/////////////" ,"/////////////000000");
//                    SSLSocketFactory result;  	// check to see if already created
//
//                        KeyStore keystoreTrust = KeyStore.getInstance("BKS");        // Bouncy Castle
//
//                        keystoreTrust.load(context.getResources().openRawResource(R.raw.ca_certificate),
//                                "12345678".toCharArray());
//
//                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//
//                        trustManagerFactory.init(keystoreTrust);
//
//                        SSLContext sslContext = SSLContext.getInstance("TLS");
//
//                        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//
//                        result = sslContext.getSocketFactory();
//
//                mqttConnectOptions.setSocketFactory(result);
                    mqttConnectOptions.setUserName(RayanApplication.getPref().getUsername());
                    mqttConnectOptions.setPassword(RayanApplication.getPref().getPassword().toCharArray());
//                    if (mqttSsl)
                        mqttConnectOptions.setSocketFactory(getSocketFactory(context));
//                mqttConnectOptions.setSocketFactory(connection.getClient().getSSLSocketFactory(input, "12345678"));
//                    mqttConnectOptions.setSocketFactory(connection.getClient().getSSLSocketFactory());
                    Log.e("/////////////" ,"/////////////1111111");
                    connection.addConnectionOptions(mqttConnectOptions);
                    MainActivityViewModel.connection.postValue(connection);
                    String[] actionArgs = new String[1];
                    actionArgs[0] = connection.getId();
                    final ActionListener callback = new ActionListener(MqttClientService.getMqttClientInstance(getApplication()),context,
                            ActionListener.Action.CONNECT,connection, MainActivityViewModel.connection, actionArgs);
//        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
                    connection.getClient().setCallback(new MyMqttCallbackHandler(context, (RayanApplication)getApplication()));
                    Log.e(TAG, "I am Going To Connect To mqtt using thread: " + Thread.currentThread().getName());
                    connection.getClient().connect(connection.getConnectionOptions(), null, callback);
                }
                catch (IllegalStateException | MqttException | IOException e) {
                    Log.e(this.getClass().getCanonicalName(),
                            "MqttException occurred", e);
                    if (e instanceof IllegalStateException)
                        Log.e(TAG, "There is an os Permission issue with connect to mqtt...");
                    if (e instanceof SSLHandshakeException){
                        Log.d(TAG, "Mqtt SSLHandshakeException occurred so connecting without ssl");
//                        mqttSsl = false;
                    }
                    Log.e("//////////error","error: " + e);
                    e.printStackTrace();
                }
            }
        });
    }

    public LiveData<Connection> disconnectMQTT(LiveData<Connection> connection){
        MutableLiveData<Connection> updateConnection = new MutableLiveData<>();
        if (connection.getValue()!= null && connection.getValue().getClient() != null)
        try {
//            connection.getValue().getClient().unregisterResources();
//            connection.getValue().getClient().close();
            connection.getValue().getClient().disconnect();
            connection.getValue().changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
            updateConnection.postValue(connection.getValue());
        } catch(Exception ex){
            Log.e(TAG, "Exception occurred during disconnect: " + ex.getMessage());
            ex.printStackTrace();
        }
        return updateConnection;
    }

    private ArrayList<Subscription> getSubscriptions(Connection connection){
        List<String> topics = deviceDatabase.getAllTopics();
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        for (int a = 0; a<topics.size();a++){
            subscriptions.add(new Subscription(topics.get(a), 0, connection.handle(), false));
        }
        return subscriptions;
    }

    public InetAddress getBroadcastAddress() {
        try {
            WifiManager wifi = (WifiManager) RayanApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifi != null;
            DhcpInfo dhcp = wifi.getDhcpInfo();
            String gateway = intToIp(dhcp.gateway);
            RayanApplication.getPref().saveLocalBroadcastAddress(gateway);
            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (broadcast >> (k * 8));
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }
    public void sendNodeToAll(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("src", RayanApplication.getPref().getId());
        jsonObject.addProperty("cmd", AppConstants.TO_DEVICE_NODE);
        sendUDPMessage.sendUdpMessage(RayanApplication.getPref().getLocalBroadcastAddress(), jsonObject.toString());
        counterObservable.takeWhile(aLong -> aLong<2)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        sendUDPMessage.sendUdpMessage(RayanApplication.getPref().getLocalBroadcastAddress(), jsonObject.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    Observable<Long> counterObservable = Observable.interval(0,700,TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    public String getCurrentSSID(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }

    private SocketFactory getSocketFactory(Context c) {
        try {
            SSLContext context;
            KeyStore ts = KeyStore.getInstance("BKS");
            ts.load(c.getResources().openRawResource(R.raw.def),
                    "12345678".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ts);
            TrustManager[] tm = tmf.getTrustManagers();
            context = SSLContext.getInstance("TLS");
            context.init(null, tm, null);

            return context.getSocketFactory();
        } catch (Exception e) {
            Log.e(TAG+"......", "", e);
            Log.e(TAG+"......", "" + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Observable<BaseResponse> addDeviceToGroupObservable(AddDeviceToGroupRequest addDeviceToGroupRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .addDeviceToGroup(RayanApplication.getPref().getToken(), addDeviceToGroupRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<BaseResponse> addDeviceToGroup(AddDeviceToGroupRequest addDeviceToGroupRequest){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        addDeviceToGroupObservable(addDeviceToGroupRequest).subscribe();
        return results;
    }

    private Observer<BaseResponse> addDeviceToGroupObserver(MutableLiveData<BaseResponse> results){
        return new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.e(TAG,"OnNext adding dobare..."+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Error adding dobare..."+e);
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed adding dobare...");
            }
        };
    }


    public Observable<BaseResponse> deleteUserObservable(DeleteUserRequest deleteUserRequest){
        ApiService apiService = ApiUtils.getApiService();
        return apiService
                .deleteUser(RayanApplication.getPref().getToken(), deleteUserRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<BaseResponse> deleteUser(DeleteUserRequest deleteUserRequest){
        final MutableLiveData<BaseResponse> results = new MutableLiveData<>();
        deleteUserObservable(deleteUserRequest).subscribe(deleteUserObserver(results));
        return results;
    }

    public  Observer<BaseResponse> deleteUserObserver(MutableLiveData<BaseResponse> results){
        return new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.e(TAG,"OnNext adding dobare..."+baseResponse);
                results.postValue(baseResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Error adding dobare..."+e);
                e.printStackTrace();

            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed adding dobare...");
            }
        };
    }

    public void playSoundEffect(String status) throws IOException {
        if (status.equals("on"))
        soundPool_on.play(soundId_on, 1, 1, 0, 0, 1);
        else
            soundPool_off.play(soundId_off, 1, 1, 0, 0, 1);
//        soundPool.play(loadedNotification, 1, 1, 1, 0, 1);

//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                soundPool.release();
//            }
//        });
//        final MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setDataSource(getApplication(),
//                Uri.parse("android.resource://"+getApplication().getPackageName()+"/raw/sound_switch_"+status));
//        mediaPlayer.prepareAsync();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaPlayer.start();
//            }
//        });
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.e("MainActivity", "Released");
//                mediaPlayer.release();
//            }
//        });
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplication(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
