package rayan.rayanapp.Mqtt;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.security.KeyStore;
import java.util.Random;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.mqtt.ActionListener;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.mqtt.MyMqttCallbackHandler;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MqttClient {
    private MutableLiveData<Connection> observableConnection;
    private String TAG = "MqttClient";
    private MqttConnectOptions mqttConnectOptions;
    private Connection connection;
    private String URL = AppConstants.MQTT_HOST;
    private ActionListener callback;
    private RayanApplication rayanApplication;
    private static volatile MqttClient mqttClient;
    private Handler uiHandler;
    public PublishSubject<Connection> connectionBus = PublishSubject.create();


    private MqttClient(Builder builder){
        Log.d(TAG, "MqttClient() called with: builder = [" + builder + "]");
        rayanApplication = builder.rayanApplication;
//        connection = builder.connection;
//        mqttConnectOptions = builder.mqttConnectOptions;
//        callback = builder.callback;
        if (builder.URL != null)
            URL = builder.URL;
        try {
            initialize();
        } catch (IllegalStateException|MqttException e) {
            Log.e(TAG, "MqttClient: Exception occurred", e);
            e.printStackTrace();
        }
//        try {
//            connectToBroker();
//        } catch (MqttException e) {
//            e.printStackTrace();
//            Log.e(TAG, "MqttClient: Exception occurred", e);
//        }

    }

    private void initialize() throws MqttException {
        if (connection == null) {
            Log.e(getClass().getCanonicalName(), "Making the connection Object...");
            connection = Connection.createConnection(
                    //"ClientHandle" + System.currentTimeMillis(),
                    System.currentTimeMillis()+RayanApplication.getPref().getId()+new Random().nextInt(),
                    System.currentTimeMillis()+RayanApplication.getPref().getId()+new Random().nextInt(),
//                    RayanApplication.getPref().getId(),
//                    RayanApplication.getPref().getId(),
                    AppConstants.MQTT_HOST,
                    AppConstants.MQTT_PORT_SSL,
                    rayanApplication,
                    true);
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
            MainActivityViewModel.connection.postValue(connection);
            updateConnectionStatus(connection);
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setConnectionTimeout(5);
            mqttConnectOptions.setKeepAliveInterval(500);
            mqttConnectOptions.setUserName(RayanApplication.getPref().getUsername());
            mqttConnectOptions.setPassword(RayanApplication.getPref().getPassword().toCharArray());
            mqttConnectOptions.setSocketFactory(getSocketFactory(rayanApplication));
            connection.addConnectionOptions(mqttConnectOptions);
            connection.getClient().setCallback(new MyMqttCallbackHandler(rayanApplication, rayanApplication));
            String[] actionArgs = new String[1];
            actionArgs[0] = connection.getId();
            callback = new ActionListener(this, rayanApplication,
                    ActionListener.Action.CONNECT, connection, MainActivityViewModel.connection, actionArgs);
//            Log.e(TAG, "I am Going To Connect To mqtt using thread: " + Thread.currentThread().getName());
        }else{
            Log.e(getClass().getCanonicalName(), "connection is already initialized");
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
            MainActivityViewModel.connection.postValue(connection);
            updateConnectionStatus(connection);
        }
    }

    public void connectToBroker(){
        try {
            connection.getClient().connect(connection.getConnectionOptions(), null, callback);
        } catch (Exception e) {
            Log.e(TAG, "MqttClient: Exception occurred", e);
            e.printStackTrace();
        }
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

    public static class Builder{
        private MqttConnectOptions mqttConnectOptions;
        private Connection connection;
        private String URL = AppConstants.MQTT_HOST;
        private ActionListener callback;
        private RayanApplication rayanApplication;

        public Builder setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
            this.mqttConnectOptions = mqttConnectOptions;
            return this;
        }

        public Builder setConnection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public Builder setURL(String URL) {
            this.URL = URL;
            return this;
        }

        public Builder setCallback(ActionListener callback) {
            this.callback = callback;
            return this;
        }

        public Builder setRayanApplication(RayanApplication rayanApplication) {
            this.rayanApplication = rayanApplication;
            return this;
        }

        public MqttClient build(){
            return new MqttClient(this);
        }
    }

    public void updateConnectionStatus(Connection connection){
        connectionBus.onNext(connection);
    }

    public Observable<Connection> listenToConnectionChanges(){
        return connectionBus;
    }

    public Connection getConnection() {
        return connection;
    }
}
