package rayan.rayanapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import rayan.rayanapp.App.RayanApplication;

public class WifiScanReceiver extends BroadcastReceiver {
    WifiManager wifi;

    public void onReceive(Context c, Intent intent) {
        String action = intent.getAction();
        Log.e("+_+_+_)(**", "intent-------------------------------------: " + action+"--------------------------------------------------" );
        if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            wifi=(WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> wifiScanList = wifi.getScanResults();
            ((RayanApplication)c.getApplicationContext()).getWifiBus().send(wifiScanList);
        }
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int iTemp = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            Log.e("+_+_+_)(**", "++++++++++++++++++++wifiStateReceiver+++++++++++++++");
            checkState(iTemp);
        }
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf((SupplicantState)
                    intent.getParcelableExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED));
            Log.e("+_+_+_)(**", "EXTRA_SUPPLICANT_CONNECTED: "+ state);
            changeState(state);

        }
        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
        {
            NetworkInfo.DetailedState state1 = WifiInfo.getDetailedStateOf((SupplicantState)
                    intent.getParcelableExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED));
            NetworkInfo.DetailedState state=
                    ((NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
            Log.e("+_+_+_)(**", ">>>>>>>>>>>>>>>>>>>NETWORK_STATE_CHANGED_ACTION<<<<<<<<<<<<<<<<");
            changeState(state);
        }

        if (action.equals(WifiManager.NETWORK_IDS_CHANGED_ACTION)){
            Log.e("+_+_+_)(**", ">>>>>>>>>>>>>>NETWORK_IDS_CHANGED_ACTION<<<<<<<<<<<<<<<");
        }
    }

    private void changeState(NetworkInfo.DetailedState aState) {
        Log.e("+_+_+_)(**", ">>>>>>>>>>>>>>>>>>changeState<<<<<<<<<<<<<<<<");
        if (aState == NetworkInfo.DetailedState.SCANNING) {
            Log.e("+_+_+_)(**wifiSupplicanState", "SCANNING");
        } else if (aState == NetworkInfo.DetailedState.CONNECTING) {
            Log.e("+_+_+_)(**wifiSupplicanState", "CONNECTING");
        } else if (aState == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
            Log.e("+_+_+_)(**wifiSupplicanState", "OBTAINING_IPADDR");
        } else if (aState == NetworkInfo.DetailedState.CONNECTED) {
            Log.e("+_+_+_)(**wifiSupplicanState", "CONNECTED");
        } else if (aState == NetworkInfo.DetailedState.DISCONNECTING) {
            Log.e("+_+_+_)(**wifiSupplicanState", "DISCONNECTING");
        } else if (aState == NetworkInfo.DetailedState.DISCONNECTED) {
            Log.e("+_+_+_)(**wifiSupplicanState", "DISCONNECTTED");
        } else if (aState == NetworkInfo.DetailedState.FAILED) {
            Log.e("+_+_+_)(**wifiSupplicanState", "FAILED");
        }else if (aState == NetworkInfo.DetailedState.AUTHENTICATING){
            Log.e("+_+_+_)(**wifiSupplicanState", "AUTHENTICATING");
        }else if (aState == NetworkInfo.DetailedState.BLOCKED){
            Log.e("+_+_+_)(**wifiSupplicanState", "BLOCKED");
        }else if (aState == NetworkInfo.DetailedState.IDLE){
            Log.e("+_+_+_)(**wifiSupplicanState", "IDLE");
        }else if (aState == NetworkInfo.DetailedState.SUSPENDED){
            Log.e("+_+_+_)(**wifiSupplicanState", "SUSPENDED");
        }else {
            Log.e("+_+_+_)(**wifiSupplicanState", "ELSE" + aState);
        }
    }

    public void checkState(int aInt) {
        Log.e("+_+_+_)(**cstate", "==>>>>>>>>checkState<<<<<<<<" + aInt);
        if (aInt == WifiManager.WIFI_STATE_ENABLING) {
            Log.e("+_+_+_)(**WifiManager", "WIFI_STATE_ENABLING");
        } else if (aInt == WifiManager.WIFI_STATE_ENABLED) {
            Log.e("+_+_+_)(**WifiManager", "WIFI_STATE_ENABLED");
        } else if (aInt == WifiManager.WIFI_STATE_DISABLING) {
            Log.e("+_+_+_)(**WifiManager", "WIFI_STATE_DISABLING");
        } else if (aInt == WifiManager.WIFI_STATE_DISABLED) {
            Log.e("+_+_+_)(**WifiManager", "WIFI_STATE_DISABLED");
        } else if (aInt == WifiManager.WIFI_STATE_UNKNOWN) {
            Log.e("+_+_+_)(**WifiManager", "WIFI_STATE_UNKNOWN");
        }else {
            Log.e("+_+_+_)(**WifiManager", "ELSE" + aInt);
        }
    }
}
