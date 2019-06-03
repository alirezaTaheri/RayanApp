package rayan.rayanapp.Data;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Objects;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Helper.MessageTransmissionDecider;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;

public class NetworkConnectionLiveData extends LiveData<NetworkConnection> {
    private Context context;

    WifiManager wifiManager;
    WifiInfo wifiInfo;
    public NetworkConnectionLiveData(Context context) {
        this.context = context;
        wifiManager = (WifiManager) Objects.requireNonNull(context).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.e("seekbarthis","In networkconnectionlivedata created now");
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("seekbarthis","in the receiver an intent was detected" + intent);
            if(intent.getExtras()!=null) {
                NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                Log.e("seekbarthis","in the receiver an intent was detected" + activeNetwork);
                Log.e("seekbarthis","in the receiver an intent was detected" + (activeNetwork != null && activeNetwork.isConnectedOrConnecting())+activeNetwork.getType());
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    switch (activeNetwork.getType()){
                        case ConnectivityManager.TYPE_WIFI:
                            String status = NetworkUtil.getConnectivityStatusString(context);
                            Log.e("postingthis","in the switch type is wifi: ");
                            if (status.equals(AppConstants.WIFI)){
                                ((RayanApplication)(context)).getNetworkBus().send(getCurrentSSID());
                            }
                            postValue(new NetworkConnection(AppConstants.WIFI_NETWORK,true, getCurrentSSID()));
                            ((RayanApplication)context).getMtd().setCurrentSSID(getCurrentSSID());
                            ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.WIFI);
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            Log.e("postingthis","in thw switch type is mobile: ");
                            ((RayanApplication)context).getMtd().setCurrentSSID(getCurrentSSID());
                            ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.MOBILE);
                            postValue(new NetworkConnection(AppConstants.MOBILE_DATA,true));
                            break;
                        case ConnectivityManager.TYPE_VPN:
                            Log.e("postingthis","in thw switch type is vpn: ");
                            postValue(new NetworkConnection(AppConstants.VPN_NETWORK,true, getCurrentSSID()));
                            ((RayanApplication)context).getMtd().setCurrentSSID(getCurrentSSID());
                            ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.VPN);
                            break;
                    }
                } else {
                    Log.e("seekbarthis","active network.getType: ()" + activeNetwork.getType());
                    Log.e("postingthis","in thw switch type is not connected: ");
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN){
                        Log.e("postingthis","in thw switch type vpn not connected: ");
                        Log.e(this.getClass().getSimpleName(),"Connection is disconnected and sending vpn" + activeNetwork.getType());
                        postValue(new NetworkConnection(AppConstants.VPN_NETWORK,false, getCurrentSSID()));
                        ((RayanApplication)context).getMtd().setCurrentSSID(getCurrentSSID());
                        ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.VPN);
                    }else{
                        ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.NOT_CONNECTED);
                        Log.e("postingthis","in thw switch type not connected: ");
                        postValue(new NetworkConnection(0,false));
                    }
                }
            }
        }
    };
        private String getCurrentSSID(){
            Log.e(this.getClass().getSimpleName(), "Getting current ssid" + wifiManager);
            wifiInfo = wifiManager.getConnectionInfo();
            Log.e(this.getClass().getSimpleName(), "Getting current ssid" + wifiInfo);
            String currentSSID  = wifiInfo.getSSID();
            if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
                currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
            }
            Log.e(this.getClass().getSimpleName(), "Getting current ssid" + currentSSID);
            if (currentSSID == null){
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    String ssid = info.getExtraInfo();
                    Log.d(this.getClass().getSimpleName(), "WiFi SSID: " + ssid);
                    Log.e(this.getClass().getSimpleName(), "now swaping current ssid and ssid: " + currentSSID);
                    currentSSID = ssid;
                }
            }
            Log.e(this.getClass().getSimpleName(), "returning current ssid: "  + currentSSID);
            return currentSSID;
        }
}
