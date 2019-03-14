package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.Services.mqtt.ActionListener;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.mqtt.MyMqttCallbackHandler;
import rayan.rayanapp.Services.mqtt.model.Subscription;
import rayan.rayanapp.Services.udp.SendUDPMessage;
import rayan.rayanapp.Util.AppConstants;


public class MainActivityViewModel extends AndroidViewModel {

    private final String TAG = MainActivityViewModel.class.getSimpleName();
    private ExecutorService executorService;
    private DeviceDatabase deviceDatabase;
    private SendUDPMessage sendUDPMessage;
    public static MutableLiveData<Connection> connection = new MutableLiveData<>();
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        deviceDatabase = new DeviceDatabase(application);
        sendUDPMessage = new SendUDPMessage();
//        connection = new MutableLiveData<>();
//        connection.setValue(Connection.createConnection("ClientHandle","ClientId","api.rayansmarthome.ir",1883,application,false));
    }


    public MutableLiveData<Connection> connectToMqtt(Context context){
        MutableLiveData<Connection> updateConnection = new MutableLiveData<>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(false);
                mqttConnectOptions.setConnectionTimeout(5);
                mqttConnectOptions.setKeepAliveInterval(200);
                Connection connection = Connection.createConnection("ClientHandle" + System.currentTimeMillis(),"ClientId"+ System.currentTimeMillis(),"api.rayansmarthome.ir",1883,context,false);
                connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
                connection.setSubscriptions(getSubscriptions(connection));
                connection.addConnectionOptions(mqttConnectOptions);
                updateConnection.postValue(connection);
                String[] actionArgs = new String[1];
                actionArgs[0] = connection.getId();
                final ActionListener callback = new ActionListener(context,
                        ActionListener.Action.CONNECT,connection, updateConnection, actionArgs);
//        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
                connection.getClient().setCallback(new MyMqttCallbackHandler(context));
                try {
                    connection.getClient().connect(connection.getConnectionOptions(), null, callback);
                }
                catch (MqttException e) {
                    Log.e(this.getClass().getCanonicalName(),
                            "MqttException occurred", e);
                }
            }
        });
        return updateConnection;
    }

    public LiveData<Connection> disconnectMQTT(LiveData<Connection> connection){
        MutableLiveData<Connection> updateConnection = new MutableLiveData<>();
        if (connection.getValue()!= null && connection.getValue().getClient() != null)
        try {
//            connection.getValue().getClient().unregisterResources();
//            connection.getValue().getClient().close();
            connection.getValue().getClient().disconnect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(getClass().getSimpleName(),"OnSuccess in Disconnection");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(getClass().getSimpleName(),"OnFailure in Disconnection");
                }
            });
            connection.getValue().changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
            updateConnection.postValue(connection.getValue());
        } catch(Exception ex){
            Log.e(TAG, "Exception occurred during disconnect: " + ex.getMessage());
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
    }

    public String getCurrentSSID(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID  = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
            currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
        }
        return currentSSID;
    }
}
