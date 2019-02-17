package rayan.rayanapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

import rayan.rayanapp.Activities.AddNewDeviceActivity;

public class WifiScanReceiver extends BroadcastReceiver {
    WifiManager wifi;

    public void onReceive(Context c, Intent intent) {
        wifi=(WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiScanList = wifi.getScanResults();
        ((AddNewDeviceActivity)c).getWifiBus().send(wifiScanList);
    }
}
