package rayan.rayanapp.Data;

import android.app.Activity;
import android.app.Application;
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
import rayan.rayanapp.Fragments.NewDevicesListFragment;
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
    }

    @Override
    protected void onActive() {
        super.onActive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
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
                            Log.e("seekbarthis","in thw switch type is wifi: " + status);
                            if (status.equals(AppConstants.WIFI)){
                                ((RayanApplication)(context)).getNetworkBus().send(getCurrentSSID());
                            }
                            postValue(new NetworkConnection(AppConstants.WIFI_NETWORK,true, getCurrentSSID()));
                            ((RayanApplication)context).getMtd().setCurrentSSID(getCurrentSSID());
                            ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.Status.WIFI);
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            Log.e("seekbarthis","in thw switch type is mobile: ");
                            ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.Status.MOBILE);
                            postValue(new NetworkConnection(AppConstants.MOBILE_DATA,true));
                            break;
                        case ConnectivityManager.TYPE_VPN:
                            postValue(new NetworkConnection(AppConstants.VPN_NETWORK,true, getCurrentSSID()));
                            break;
                    }
                } else {
                    Log.e("seekbarthis","in thw switch type is nothing: ");
                    ((RayanApplication)context).getMtd().updateStatus(MessageTransmissionDecider.Status.NOT_CONNECTED);
                    postValue(new NetworkConnection(0,false));
                }
            }
        }
    };
        private String getCurrentSSID(){
            wifiInfo = wifiManager.getConnectionInfo();
            String currentSSID  = wifiInfo.getSSID();
            if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
                currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
            }
            return currentSSID;
        }
}
