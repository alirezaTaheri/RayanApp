package rayan.rayanapp.Services;

import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.ConnectionLiveData;
import rayan.rayanapp.Services.mqtt.ActionListener;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.mqtt.MyMqttCallbackHandler;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class AlwaysOnService extends Service {
    private final String TAG = getClass().getCanonicalName();
    public AlwaysOnService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    int counter = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Hey You", "Service Just Started");
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionChangeReceiver(), intentFilter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Log.e("Hey You", "Yes: " + counter);
                        counter++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return START_STICKY;
    }



    public void connectToMqtt(Context context){
        Log.e("MainActivityViewModel", "........................");
        disconnectMQTT(MainActivityViewModel.connection);
//        MutableLiveData<Connection> updateConnection = new MutableLiveData<>();
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
//                    MainActivityViewModel.connection.postValue(connection);
//                    String[] actionArgs = new String[1];
//                    actionArgs[0] = connection.getId();
//                    final ActionListener callback = new ActionListener(context,
//                            ActionListener.Action.CONNECT,connection, MainActivityViewModel.connection, actionArgs);
//        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
                    connection.getClient().setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                            Log.e("////////////////", "connectionLost");
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.e("+++++++++++++", "message Arrived: " + topic + message);
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                    Log.e(TAG, "I am Going To Connect To mqtt using thread: " + Thread.currentThread().getName());
                    connection.getClient().connect(connection.getConnectionOptions(), null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.e("+++++++++++++", "Successfully Connected");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.e("+++++++++++++", "Failed To Connect");
                        }
                    });
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

    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive( Context context, Intent intent ) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
            if (activeNetInfo != null) {
                Log.e("MainActivity", "connectionLiveData:" + activeNetInfo);
                Toast.makeText( context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
                connectToMqtt(context);
            }
//            if( mobNetInfo != null ) {
//                Log.e(TAG, "Connected To Mobile Network");
//                Toast.makeText( context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
//            }
        }
    }
}
