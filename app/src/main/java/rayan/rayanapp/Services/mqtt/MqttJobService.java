package rayan.rayanapp.Services.mqtt;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.mqtt.model.Subscription;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MqttJobService extends JobService {

    private final String TAG = "MqttJobService";
    @SuppressLint("CheckResult")
    @Override
    public boolean onStartJob(JobParameters params) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    deviceDatabase = new DeviceDatabase(getApplicationContext());
                    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                    mqttConnectOptions.setAutomaticReconnect(true);
                    mqttConnectOptions.setCleanSession(false);
                    mqttConnectOptions.setConnectionTimeout(5);
                    mqttConnectOptions.setKeepAliveInterval(500);
                    Connection connection = Connection.createConnection("ClientHandle" + System.currentTimeMillis(),"ClientId"+ System.currentTimeMillis(),AppConstants.MQTT_HOST,AppConstants.MQTT_PORT,getApplicationContext(),true);
                    connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
                    connection.setSubscriptions(getSubscriptions(connection));
                    mqttConnectOptions.setUserName(RayanApplication.getPref().getUsername());
                    mqttConnectOptions.setPassword(RayanApplication.getPref().getPassword().toCharArray());
                    boolean b = RayanApplication.getPref().isMqttSsl();
                    Log.e(TAG, "Mqtt Connection is ssl? " + b);
                    if (b) {
                        mqttConnectOptions.setSocketFactory(getSocketFactory(getApplicationContext()));
                    }
                    connection.addConnectionOptions(mqttConnectOptions);
                    MainActivityViewModel.connection.postValue(connection);
                    String[] actionArgs = new String[1];
                    actionArgs[0] = connection.getId();
                    ActionListener callback = new ActionListener(getApplicationContext(),
                            ActionListener.Action.CONNECT,connection, MainActivityViewModel.connection, actionArgs);
                    connection.getClient().setCallback(new MyMqttCallbackHandler(getApplicationContext(),(RayanApplication) getApplication()));
                    connection.getClient().connect(connection.getConnectionOptions(), null, callback);
                    emitter.onNext(new Object(
                    ));
                }
                catch (MqttException e) {
                    Log.e(this.getClass().getCanonicalName(),
                            "MqttException occurred", e);
                    Log.e("//////////error","error: " + e);
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(MqttJobService.class.getCanonicalName(), "connectidconnectid");
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
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


    DeviceDatabase deviceDatabase;
    private ArrayList<Subscription> getSubscriptions(Connection connection){
        List<String> topics = deviceDatabase.getAllTopics();
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        for (int a = 0; a<topics.size();a++){
            subscriptions.add(new Subscription(topics.get(a), 0, connection.handle(), false));
        }
        return subscriptions;
    }
}
