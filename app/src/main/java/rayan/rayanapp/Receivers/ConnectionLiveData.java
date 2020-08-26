package rayan.rayanapp.Receivers;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.ConnectionStatusModel;
import rayan.rayanapp.RxBus.NetworkConnectionBus;
import rayan.rayanapp.Util.AppConstants;

import static rayan.rayanapp.Activities.AddNewDeviceActivity.hasPermissions;

public class ConnectionLiveData extends LiveData<ConnectionStatusModel> {
    private Context context;
    private NetworkConnectionBus networkBus;
    private String TAG = "ConnectionLiveData";
    public ConnectionLiveData(Context context, NetworkConnectionBus networkConnectionBus) {
        this.context = context;
        this.networkBus = networkConnectionBus;
    }
    ConnectivityManager.NetworkCallback networkCallback;
    ConnectivityManager manager;
    @Override
    protected void onActive() {
        Log.e(this.getClass().getCanonicalName(),"OnActive For Android: "+Build.VERSION.SDK_INT );
        super.onActive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    Log.i(TAG, "On Capabilities Changed>..."+networkCapabilities);
                }

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    // this ternary operation is not quite true, because non-metered doesn't yet mean, that it's wifi
                    // nevertheless, for simplicity let's assume that's true
                    Log.i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI") + network.toString());
                    if (manager != null) {
                        Log.i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI") + manager.getActiveNetworkInfo().getExtraInfo());
                        Log.i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI") + network.toString() + manager.getActiveNetworkInfo());
                    }
                    String ssid = null;
                    if (manager.getActiveNetworkInfo() != null)
                        ssid = manager.getActiveNetworkInfo().getExtraInfo();
                    if (ssid == null){
                        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        ssid = connectionInfo.getSSID();
                        Log.i(TAG, "SSID IS NULL so using wifiManager Solution: SSID: "+ssid+" ConnectionInfo:"+ connectionInfo);
                    }
                    if (ssid != null && ssid.charAt(ssid.length()-1) == ssid.charAt(0) && String.valueOf(ssid.charAt(0)).equals("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                        Log.e(TAG,"SSID Modified to: "+ssid);
                    }
                    if (ssid == null){
                        ssid = AppConstants.UNKNOWN_SSID;
                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        boolean location = (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                        Log.e(TAG,"is location on? " + location + " = "+locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)+locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
                        String[] PERMISSIONS = {
                                android.Manifest.permission.CHANGE_WIFI_STATE,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                        };
                        Log.e(TAG,"Permissions: "+ hasPermissions(context, PERMISSIONS));
                    }
                    Toast.makeText(context, "" + ssid, Toast.LENGTH_SHORT).show();
                    if (manager.isActiveNetworkMetered()){
                        Log.e(TAG,"Network is metered or lte "+ssid);
                        postValue(new ConnectionStatusModel(ssid,ConnectivityManager.TYPE_MOBILE,
                                AppConstants.MOBILE,manager.getActiveNetworkInfo().getState()));
                    }else{
                        Log.e(TAG,"Network Wifi "+ssid);
                        postValue(new ConnectionStatusModel(ssid,ConnectivityManager.TYPE_WIFI,
                                AppConstants.WIFI,manager.getActiveNetworkInfo().getState()));
                    }
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.i(TAG, "losing active connection");
                    Log.i(TAG, "losing active connection" + network);
                    postValue(null);
                }
            };
            manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.registerDefaultNetworkCallback(networkCallback);
            }else {
                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .build();
                manager.registerNetworkCallback(networkRequest,networkCallback);
            }
        }else{
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(networkReceiver, filter);
        }
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.e(TAG,"OnInactive For context"+context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.unregisterNetworkCallback(networkCallback);
        }else{
            context.unregisterReceiver(networkReceiver);
        }
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = manager.getConnectionInfo();
                NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                NetworkInfo activeNetwork2 = connectivityManager.getActiveNetworkInfo();
                Log.e(TAG+" Receiver","first: " + activeNetwork);
                Log.e(TAG+" Receiver","second:" + activeNetwork2);
                Log.e(TAG+" Receiver","second:" + connectionInfo);
                Log.e(TAG+" Receiver","second:" + connectionInfo.getSSID());
                if (activeNetwork2 != null)
                    activeNetwork = activeNetwork2;
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    switch (activeNetwork.getType()){
                        case ConnectivityManager.TYPE_WIFI:
                            if (activeNetwork != null && activeNetwork.getExtraInfo() != null) {
                                postValue(new ConnectionStatusModel(activeNetwork.getExtraInfo(), ConnectivityManager.TYPE_WIFI,
                                        AppConstants.WIFI, activeNetwork.getState()));
                                networkBus.send(activeNetwork.getExtraInfo());
                            }
                            else {
                                postValue(new ConnectionStatusModel(connectionInfo.getSSID(), ConnectivityManager.TYPE_WIFI,
                                        AppConstants.WIFI, activeNetwork.getState()));
                                networkBus.send(connectionInfo.getSSID());
                            }
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
