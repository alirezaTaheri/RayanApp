package rayan.rayanapp.Receivers;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import rayan.rayanapp.Data.ConnectionStatusModel;
import rayan.rayanapp.Util.AppConstants;

public class ConnectionLiveData extends LiveData<ConnectionStatusModel> {
    private Context context;

    public ConnectionLiveData(Context context) {
        this.context = context;
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
            if(intent.getExtras()!=null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                NetworkInfo activeNetwork2 = connectivityManager.getActiveNetworkInfo();
                Log.e("0101010101","first: " + activeNetwork);
                Log.e("0101010101","second:" + activeNetwork2);
                if (activeNetwork2 != null)
                    activeNetwork = activeNetwork2;
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    switch (activeNetwork.getType()){
                        case ConnectivityManager.TYPE_WIFI:
                            postValue(new ConnectionStatusModel(activeNetwork.getExtraInfo(),ConnectivityManager.TYPE_WIFI,
                                    AppConstants.WIFI,activeNetwork.getState()));
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            postValue(new ConnectionStatusModel(activeNetwork.getExtraInfo(),ConnectivityManager.TYPE_MOBILE,
                                    AppConstants.MOBILE,activeNetwork.getState()));
                            break;
                    }
                } else {
                    postValue(null);
                }
            }
        }
    };

    private String getCurrentSsid(){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        return info.getSSID();
    }
}
