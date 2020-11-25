package rayan.rayanapp.Services.mqtt;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Mqtt.MqttClient;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.mqtt.internal.Connections;
import rayan.rayanapp.Services.mqtt.model.Subscription;


public class ActionListener implements IMqttActionListener {

    private static final String TAG = "ActionListener";
    private static final String activityClass = "org.eclipse.paho.android.sample.activity.MainActivity";

    public enum Action {
        CONNECT,
        DISCONNECT,
        SUBSCRIBE,
        PUBLISH
    }
    private final Action action;
    private final String[] additionalArgs;
    private final Connection connection;
    private final String clientHandle;
    private final Context context;
    private MutableLiveData<Connection> updateConnection;
    private MqttClient mqttClient;
    public ActionListener(MqttClient mqttClient, Context context, Action action,
                          Connection connection, MutableLiveData<Connection> updateConnection, String... additionalArgs) {
        this.context = context;
        this.action = action;
        this.connection = connection;
        this.clientHandle = connection.handle();
        this.additionalArgs = additionalArgs;
        this.updateConnection = updateConnection;
        this.mqttClient = mqttClient;
    }
    public ActionListener(Context context, Action action,
                          Connection connection, String... additionalArgs) {
        this.context = context;
        this.action = action;
        this.connection = connection;
        this.clientHandle = connection.handle();
        this.additionalArgs = additionalArgs;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.e(TAG, "In onSuccess Method");
        switch (action) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case PUBLISH:
                publish();
                break;
        }

    }

    private void publish() {

        Log.e(TAG, "In publish Method");


        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String actionTaken = context.getString(R.string.toast_pub_success,
              (Object[]) additionalArgs);
        c.addAction(actionTaken);
//        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        System.out.print("Published");

    }

    private void subscribe() {

        Log.e(TAG, "In Subscribe Method");

        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        String actionTaken = context.getString(R.string.toast_sub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
//        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
        System.out.print(actionTaken);

    }

    private void disconnect() {

        Log.e(TAG, "In Disconnect Method");
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
        String actionTaken = context.getString(R.string.toast_disconnected);
        c.addAction(actionTaken);
        updateConnection.postValue(c);
//        MainActivityViewModel.connection.setValue(c);
        Log.i(TAG, c.handle() + " disconnected.");
        //build intent
        Intent intent = new Intent();
        intent.setClassName(context, activityClass);
        intent.putExtra("handle", clientHandle);
    }

    private void connect() {
        Log.e(TAG, "In Connect Method");
//        ArrayList<Subscription> subscriptionss = new ArrayList<>();
//        subscriptionss.add(new Subscription("1234", 0, connection.handle(), true));
//        connection.setSubscriptions(subscriptionss);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTED);
        connection.addAction("Client Connected");
        mqttClient.updateConnectionStatus(connection);
        updateConnection.postValue(connection);
//        Toast.makeText(context, "Successfully Connected", Toast.LENGTH_SHORT).show();
        Log.i(TAG, connection.handle() + " connected.");
        Log.e(TAG, "subscriptions: " + connection.getSubscriptions());
        try {
            ArrayList<Subscription> subscriptions = connection.getSubscriptions();
            if (subscriptions.size() > 0)
                for (Subscription sub : subscriptions) {
                    Log.i(TAG, "Auto-subscribing to: " + sub.getTopic() + " @ QoS: " + sub.getQos() + connection.getClient());
                    connection.getClient().subscribe(sub.getTopic(), sub.getQos()).setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.e(TAG, "Subscribed Successfully "+asyncActionToken.getTopics());
                            DevicesFragment.subscribedDevices.add(sub.getTopic());
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.e(TAG, "Failed To Subscribe  "+asyncActionToken.getTopics());
                        }
                    });
                }
//                else
//                for (String sub : DevicesFragmentViewModel.tempTopics) {
//                    Log.i(TAG, "Auto-subscribing to: " + sub + " @ QoS: " + "0" + connection.getClient());
//                    connection.getClient().subscribe(sub, 0).setActionCallback(new IMqttActionListener() {
//                        @Override
//                        public void onSuccess(IMqttToken asyncActionToken) {
//                            Log.e(TAG, "Subscribed Successfully "+asyncActionToken.getTopics());
//                            DevicesFragment.subscribedDevices.add(sub);
//                        }
//
//                        @Override
//                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                            Log.e(TAG, "Failed To Subscribe  "+asyncActionToken.getTopics());
//                        }
//                    });
//
//                }
        } catch (MqttException | IllegalArgumentException ex){
            Log.e(TAG, "Failed to Auto-Subscribe: " + ex.getMessage());
        }
//        MainActivityViewModel.connection.setValue(connection);

    }

    @Override
    public void onFailure(IMqttToken token, Throwable exception) {
        Log.e(TAG, "In onFailure  Method");
        switch (action) {
            case CONNECT:
                connect(exception);
                break;
            case DISCONNECT:
                disconnect(exception);
                break;
            case SUBSCRIBE:
                subscribe(exception);
                break;
            case PUBLISH:
                publish(exception);
                break;
        }

    }

    private void publish(Throwable exception) {
        Log.e(TAG, "In publish Method 2 ");
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        @SuppressLint("StringFormatMatches") String action = context.getString(R.string.toast_pub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
//        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print("Publish failed");

    }

    private void subscribe(Throwable exception) {
        Log.e(TAG, "In subscribe Method 2 ");

        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        String action = context.getString(R.string.toast_sub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
//        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print(action);

    }

    private void disconnect(Throwable exception) {
        Log.e(TAG, "In Disconnect Method 2");
        Connection c = Connections.getInstance(context).getConnection(clientHandle);
        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
        c.addAction("Disconnect Failed - an error occured");

    }

    private void connect(Throwable exception) {
        Log.e(TAG, "In Connect Method 2" + exception);
        Log.e(TAG, "In Connect Method 2" + exception.getMessage());
//        Toast.makeText(context, "Can't Connect", Toast.LENGTH_SHORT).show();
        exception.printStackTrace();
        if (exception.getMessage().equals("Client is connected") || exception.getMessage().equals("Connect already in progress")) {
            Log.e(TAG, "Client is already connected");//32110
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTED);
        }else{
//        Connection c = MainActivityViewModel.connection.getValue();
//        if (c == null)
//            c = connection;
//        c.changeConnectionStatus(Connection.ConnectionStatus.ERROR);
//        c.addAction("Client failed to connect");
//        MainActivityViewModel.connection.setValue(c);
        connection.changeConnectionStatus(Connection.ConnectionStatus.ERROR);
        connection.addAction("Client failed to connect");
        System.out.println("Client failed to connect");
    }
    mqttClient.updateConnectionStatus(connection);
    updateConnection.postValue(connection);
    }

}