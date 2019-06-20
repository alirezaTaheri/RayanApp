package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
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
import rayan.rayanapp.Helper.Encryptor;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Persistance.database.GroupDatabase;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
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
    public static MutableLiveData<Connection> connection = new MutableLiveData<>();
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        deviceDatabase = new DeviceDatabase(application);
        sendUDPMessage = new SendUDPMessage();
        groupDatabase = new GroupDatabase(application);
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
                Connection connection = Connection.createConnection("ClientHandle" + System.currentTimeMillis(),"ClientId"+ System.currentTimeMillis(),AppConstants.MQTT_HOST,AppConstants.MQTT_PORT,context,true);
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
                    mqttConnectOptions.setSocketFactory(getSocketFactory(context));
//                mqttConnectOptions.setSocketFactory(connection.getClient().getSSLSocketFactory(input, "12345678"));
//                    mqttConnectOptions.setSocketFactory(connection.getClient().getSSLSocketFactory());
                    Log.e("/////////////" ,"/////////////1111111");
                connection.addConnectionOptions(mqttConnectOptions);
                MainActivityViewModel.connection.postValue(connection);
                String[] actionArgs = new String[1];
                actionArgs[0] = connection.getId();
                final ActionListener callback = new ActionListener(context,
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
        counterObservable.takeWhile(aLong -> aLong<5)
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
}
