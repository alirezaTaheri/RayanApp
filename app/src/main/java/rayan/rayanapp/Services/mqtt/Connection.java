package rayan.rayanapp.Services.mqtt;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rayan.rayanapp.R;
import rayan.rayanapp.Services.mqtt.internal.IReceivedMessageListener;
import rayan.rayanapp.Services.mqtt.internal.Persistence;
import rayan.rayanapp.Services.mqtt.internal.PersistenceException;
import rayan.rayanapp.Services.mqtt.model.ReceivedMessage;
import rayan.rayanapp.Services.mqtt.model.Subscription;


@Entity
public class Connection {
    @Ignore
    private static final String activityClass = "org.eclipse.paho.android.sample.activity.MainActivity";

    private String clientHandle = null;

    private String clientId = null;

    private String host = null;

    private int port = 0;
    @Ignore
    private ConnectionStatus status = ConnectionStatus.NONE;
    @Ignore
    private ArrayList<String> history = null;
    @Ignore
    private MqttAndroidClient client = null;
    @Ignore
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
    @Ignore
    private Context context = null;
    @Ignore
    private MqttConnectOptions mqttConnectOptions;

    private boolean tlsConnection = true;
    @Ignore
    private long persistenceId = -1;
    @Ignore
    private final Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();
    @Ignore
    private final ArrayList<ReceivedMessage> messageHistory =  new ArrayList<ReceivedMessage>();
    @Ignore
    private final ArrayList<IReceivedMessageListener> receivedMessageListeners = new ArrayList<IReceivedMessageListener>();

    public enum ConnectionStatus {

        /** Client is Connecting **/
        CONNECTING,
        /** Client is Connected **/
        CONNECTED,
        /** Client is Disconnecting **/
        DISCONNECTING,
        /** Client is Disconnected **/
        DISCONNECTED,
        /** Client has encountered an Error **/
        ERROR,
        /** Status is unknown **/
        NONE
    }

    public static Connection createConnection(String clientHandle, String clientId, String host, int port, Context context, boolean tlsConnection){

        String uri;
        if(tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }

        MqttAndroidClient client = new MqttAndroidClient(context, uri, clientId);
        return new Connection(clientHandle, clientId, host, port, context, client, tlsConnection);
    }

    public void updateConnection(String clientId, String host, int port, boolean tlsConnection){
        String uri;
        if(tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }

        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.tlsConnection = tlsConnection;
        this.client = new MqttAndroidClient(context, uri, clientId);

    }


    private Connection(String clientHandle, String clientId, String host,
                       int port, Context context, MqttAndroidClient client, boolean tlsConnection) {
        //generate the client handle from its hash code
        this.clientHandle = clientHandle;
        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.context = context;
        this.client = client;
        this.tlsConnection = tlsConnection;
        history = new ArrayList<String>();
        String sb = "Client: " +
                clientId +
                " created";
        addAction(sb);
    }


    public void addAction(String action) {

        Object[] args = new String[1];
        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        args[0] = dateTimeFormatter.format(new Date());

        String timestamp = context.getString(R.string.timestamp, args);
        history.add(action + timestamp);

        notifyListeners(new PropertyChangeEvent(this, ActivityConstants.historyProperty, null, null));
    }

    /**
     * Gets the client handle for this connection
     * @return client Handle for this connection
     */
    public String handle() {
        return clientHandle;
    }

    /**
     * Determines if the client is connected
     * @return is the client connected
     */
    public boolean isConnected() {
        return status == ConnectionStatus.CONNECTED;
    }

    /**
     * Changes the connection status of the client
     * @param connectionStatus The connection status of this connection
     */
    public void changeConnectionStatus(ConnectionStatus connectionStatus) {
        status = connectionStatus;
        notifyListeners((new PropertyChangeEvent(this, ActivityConstants.ConnectionStatusProperty, null, null)));
    }

    /**
     * A string representing the state of the client this connection
     * object represents
     *
     *
     * @return A string representing the state of the client
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(clientId);
        sb.append("\n ");

        switch (status) {

            case CONNECTED :
                sb.append(context.getString(R.string.connection_connected_to));
                break;
            case DISCONNECTED :
                sb.append(context.getString(R.string.connection_disconnected_from));
                break;
            case NONE :
                sb.append(context.getString(R.string.connection_unknown_status));
                break;
            case CONNECTING :
                sb.append(context.getString(R.string.connection_connecting_to));
                break;
            case DISCONNECTING :
                sb.append(context.getString(R.string.connection_disconnecting_from));
                break;
            case ERROR :
                sb.append(context.getString(R.string.connection_error_connecting_to));
        }
        sb.append(" ");
        sb.append(host);

        return sb.toString();
    }

    /**
     * Compares two connection objects for equality
     * this only takes account of the client handle
     * @param o The object to compare to
     * @return true if the client handles match
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Connection)) {
            return false;
        }

        Connection c = (Connection) o;

        return clientHandle.equals(c.clientHandle);

    }

    /**
     * Get the client Id for the client this object represents
     * @return the client id for the client this object represents
     */
    public String getId() {
        return clientId;
    }

    /**
     * Get the host name of the server that this connection object is associated with
     * @return the host name of the server this connection object is associated with
     */
    public String getHostName() {

        return host;
    }

    /**
     * Gets the client which communicates with the org.eclipse.paho.android.service service.
     * @return the client which communicates with the org.eclipse.paho.android.service service
     */
    public MqttAndroidClient getClient() {
        return client;
    }

    /**
     * Add the connectOptions used to connect the client to the server
     * @param connectOptions the connectOptions used to connect to the server
     */
    public void addConnectionOptions(MqttConnectOptions connectOptions) {
        mqttConnectOptions = connectOptions;

    }

    /**
     * Get the connectOptions used to connect this client to the server
     * @return The connectOptions used to connect the client to the server
     */
    public MqttConnectOptions getConnectionOptions()
    {
        return mqttConnectOptions;
    }

    /**
     * Register a {@link PropertyChangeListener} to this object
     * @param listener the listener to register
     */
    public void registerChangeListener(PropertyChangeListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Notify {@link PropertyChangeListener} objects that the object has been updated
     * @param propertyChangeEvent - The property Change event
     */
    private void notifyListeners(PropertyChangeEvent propertyChangeEvent)
    {
        for (PropertyChangeListener listener : listeners)
        {
            listener.propertyChange(propertyChangeEvent);
        }
    }

    /**
     * Gets the port that this connection connects to.
     * @return port that this connection connects to
     */
    public int getPort() {
        return port;
    }

    /**
     * Determines if the connection is secured using SSL, returning a C style integer value
     * @return 1 if SSL secured 0 if plain text
     */
    public int isSSL() {
        return tlsConnection ? 1 : 0;
    }

    /**
     * Assign a persistence ID to this object
     * @param id the persistence id to assign
     */
    public void assignPersistenceId(long id) {
        persistenceId = id;
    }

    /**
     * Returns the persistence ID assigned to this object
     * @return the persistence ID assigned to this object
     */
    public long persistenceId() {
        return persistenceId;
    }



    public void addNewSubscription(Subscription subscription) throws MqttException {
        if(!subscriptions.containsKey(subscription.getTopic())){
            try{
                String[] actionArgs = new String[1];
                actionArgs[0] = subscription.getTopic();
                final ActionListener callback = new ActionListener(this.context,
                        ActionListener.Action.SUBSCRIBE, this, actionArgs);
                this.getClient().subscribe(subscription.getTopic(), subscription.getQos(), null, callback);
                Persistence persistence = new Persistence(context);

                long rowId = persistence.persistSubscription(subscription);
                subscription.setPersistenceId(rowId);
                subscriptions.put(subscription.getTopic(), subscription);
            } catch (PersistenceException pe){
                throw new MqttException(pe);
            }

        }
    }


    public void unsubscribe (Subscription subscription) throws MqttException {
        if(subscriptions.containsKey(subscription.getTopic())){
            this.getClient().unsubscribe(subscription.getTopic());
            subscriptions.remove(subscription.getTopic());
            Persistence persistence = new Persistence(context);
            persistence.deleteSubscription(subscription);
        }

    }

    public void setSubscriptions(ArrayList<Subscription> newSubs){
        for(Subscription sub : newSubs){
            subscriptions.put(sub.getTopic(), sub);
        }
    }

    public ArrayList<Subscription> getSubscriptions(){
        ArrayList<Subscription> subs = new ArrayList<Subscription>();
        subs.addAll(subscriptions.values());
        return subs;
    }

    public void addReceivedMessageListner(IReceivedMessageListener listener){
        receivedMessageListeners.add(listener);
    }

    public void messageArrived(String topic, MqttMessage message){
        ReceivedMessage msg = new ReceivedMessage(topic, message);
        Log.e("Connection","Message Arrived: " + message.toString());
        messageHistory.add(0, msg);
        if(subscriptions.containsKey(topic)){
            subscriptions.get(topic).setLastMessage(new String(message.getPayload()));
//            if(subscriptions.get(topic).isEnableNotifications()){
//                //create intent to start activity
//                Intent intent = new Intent();
//                intent.setClassName(context, activityClass);
//                intent.putExtra("handle", clientHandle);
//
//                //format string args
//                Object[] notifyArgs = new String[3];
//                notifyArgs[0] = this.getId();
//                notifyArgs[1] = new String(message.getPayload());
//                notifyArgs[2] = topic;
//
//                //notify the user
//                Notify.notifcation(context, context.getString(R.string.notification, notifyArgs), intent, R.string.notifyTitle);
//
//            }
        }

        for(IReceivedMessageListener listener : receivedMessageListeners){
            listener.onMessageReceived(msg);
        }


    }

    public ArrayList<ReceivedMessage> getMessages(){
        return messageHistory;
    }



}
