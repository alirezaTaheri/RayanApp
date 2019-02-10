package rayan.rayanapp.Wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import rayan.rayanapp.App.RayanApplication;

import static com.mikepenz.iconics.Iconics.TAG;

public class WifiHandler implements WifiHelper{

    private WifiCypherType.WifiCipherType getWifiCipherType(WifiConfiguration wifiConfiguration) {
        String wifiConfigurationStr = wifiConfiguration.toString();
        boolean isWEP = wifiConfigurationStr.contains("SHARED");
        if (isWEP) {
            return WifiCypherType.WifiCipherType.WIFI_CIPHER_WEP;
        }
        boolean isWPA = wifiConfigurationStr.contains("WPA_PSK") || wifiConfigurationStr.contains("WPA2_PSK");
        if (isWPA) {
            return WifiCypherType.WifiCipherType.WIFI_CIPHER_WPA;
        }
        return WifiCypherType.WifiCipherType.WIFI_CIPHER_OPEN;
    }

    private NetworkInfo getWifiNetworkInfo() {
        Context context = RayanApplication.getContext();
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    private WifiInfo getConnectionInfo() {
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.getConnectionInfo();
    }

    private WifiConfiguration createWifiConfiguration(String ssid, WifiCypherType.WifiCipherType type, boolean isSsidHidden,
                                                      String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.hiddenSSID = isSsidHidden;
        switch (type) {
            case WIFI_CIPHER_OPEN:
                config.wepKeys[0] = "\"" + "\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFI_CIPHER_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                if (password.length() != 0) {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    }
                    else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case WIFI_CIPHER_WPA:
                config.preSharedKey = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                // for WPA
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                // for WPA2
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                break;
            default:
                return null;
        }
        return config;
    }

    private WifiConfiguration getExistWifiConfiguration(String ssid) {
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                Log.e(TAG, Thread.currentThread().toString() + "##getExistWifiConfiguration(ssid=[" + ssid + "]): "
                        + existingConfig);
                return existingConfig;
            }
        }
        Log.e(TAG, Thread.currentThread().toString() + "##getExistWifiConfiguration(ssid=[" + ssid + "]): null");
        return null;
    }

    @Override
    public boolean isWifiEnabled() {
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = mWifiManager.isWifiEnabled();
        Log.e(TAG, Thread.currentThread().toString() + "##isWifiEnabled(): " + isWifiEnabled);
        return isWifiEnabled;
    }

    @Override
    public boolean isWifiAvailable() {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiAvailable = false;
        if (mWiFiNetworkInfo != null) {
            isWifiAvailable = mWiFiNetworkInfo.isAvailable();
        }
        Log.e(TAG, Thread.currentThread().toString() + "##isWifiAvailable(): " + isWifiAvailable);
        return isWifiAvailable;
    }

    @Override
    public boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null) {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        Log.e(TAG,Thread.currentThread().toString() + "##isWifiConnected(): " + isWifiConnected);
        return isWifiConnected;
    }

    @Override
    public boolean isMobileAvailable() {
        Context context = RayanApplication.getContext();
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = false;
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null)
        {
            isMobileAvailable = mMobileNetworkInfo.isAvailable();
        }
        Log.e(TAG,Thread.currentThread().toString() + "##isMobileAvailable(): " + isMobileAvailable);
        return isMobileAvailable;
    }

    @Override
    public boolean isNetworkAvailable() {
        Context context = RayanApplication.getContext();
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = false;
        if (mNetworkInfo != null)
        {
            isNetworkAvailable = mNetworkInfo.isAvailable();
        }
        Log.e(TAG,Thread.currentThread().toString() + "##isNetworkAvailable(): " + isNetworkAvailable);
        return isNetworkAvailable;
    }

    @Override
    public boolean isWifiConnected(final String ssid)
    {
        WifiInfo mWifiInfo = getConnectionInfo();
        boolean isWifiConnected = false;
        if (mWifiInfo != null && isWifiConnected())
        {
            isWifiConnected = mWifiInfo.getSSID().equals("\"" + ssid + "\"") || mWifiInfo.getSSID().equals(ssid);
        }
        Log.e(TAG,Thread.currentThread().toString() + "##isWifiConnected(String ssid): " + ssid + " " + isWifiConnected);
        return isWifiConnected;
    }

    @Override
    public boolean isConnectedOrConnecting()
    {
        NetworkInfo mNetworkInfo = getWifiNetworkInfo();
        boolean isConnectedOrConnecting = false;
        if (mNetworkInfo != null)
        {
            isConnectedOrConnecting = mNetworkInfo.isConnectedOrConnecting();
        }
        Log.e(TAG,Thread.currentThread().toString() + "##isConnectedOrConnecting(): " + isConnectedOrConnecting);
        return isConnectedOrConnecting;
    }

    @Override
    public boolean setWifiEnabled()
    {
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean setWifiEnabled = mWifiManager.setWifiEnabled(true);
        Log.e(TAG,Thread.currentThread().toString() + "##setWifiEnabled(): " + setWifiEnabled);
        return setWifiEnabled;
    }

    @Override
    public boolean enableConnected(String ssid)
    {
        if (isWifiConnected(ssid))
        {
            Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid
                    + "]): is connected already,return true");
            return true;
        }
        boolean enableConnected;
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        // only use the exist wifi Configuration
        if (wifiConfiguration == null)
        {
            Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid
                    + "]): wifi info don't exist in System,return false");
            return false;
        }
        else
        {
            Context context = RayanApplication.getContext();
            WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            enableConnected = mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
            Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "]): " + enableConnected);
            return enableConnected;
        }
    }

    private final ArrayList<Thread> threads = new ArrayList<>(4);

    private boolean isConnectSuc(String ssid)
            throws InterruptedException
    {
        synchronized (threads)
        {
            for (Thread thread : threads)
            {
                if (thread != Thread.currentThread())
                {
                    thread.interrupt();
                }
            }
            threads.clear();
            threads.trimToSize();
            threads.add(Thread.currentThread());
            Log.e(TAG,Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid + "]): Thread.interrupted()");
        }
        // the max timeout is 20 seconds
        long start = System.currentTimeMillis();
        long runtime;
        do
        {
            Thread.sleep(CONNECT_CHECK_INTERVAL);

            if (isWifiConnected(ssid))
            {
                Log.e(TAG,Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid
                        + "]): isWifiConnected(ssid)=true, return true");
                return true;
            }
            runtime = System.currentTimeMillis() - start;
        } while (runtime < CONNECT_TIMEOUT);
        Log.e(TAG,Thread.currentThread().toString() + "##isConnectSuc(ssid=[" + ssid + "]): is timeout, return false");
        return false;
    }

    @Override
    public boolean connect(String ssid)
            throws InterruptedException
    {
        if (isWifiConnected(ssid))
        {
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid
                    + "]): is connected already,return true");
            return true;
        }
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        // only use the exist wifi Configuration
        if (wifiConfiguration == null)
        {
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid
                    + "]): wifi info don't exist in System,return false");
            return false;
        }
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        mWifiManager.enableNetwork(netId, true);
        boolean connect = isConnectSuc(ssid);
        Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "]): " + connect);
        return connect;
    }

    @Override
    public boolean enableConnected(String ssid, WifiCypherType.WifiCipherType type, String... password)
    {
        return enableConnected(ssid, type, false, password);
    }

    @Override
    public boolean connect(String ssid, WifiCypherType.WifiCipherType type, String... password)
            throws InterruptedException
    {
        return connect(ssid, type, false, password);
    }

    private void addScanResult(List<ScanResult> scanResultList, ScanResult scanResult)
    {
        String scanResultBssid = scanResult.BSSID;
        boolean isExist = false;
        for (ScanResult scanResultInList : scanResultList)
        {
            if (scanResultInList.BSSID.equals(scanResultBssid))
            {
                isExist = true;
                break;
            }
        }
        if (!isExist)
        {
            scanResultList.add(scanResult);
        }
    }

    @Override
    public List<ScanResult> scan() {
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
        List<ScanResult> wifiList;
        long start = System.currentTimeMillis();
        long runtime;
        do {
            try {
                Thread.sleep(SCAN_CHECK_INTERVAL);
            }
            catch (InterruptedException e) {
                Log.e(TAG,Thread.currentThread().toString() + "##scan(): is interrupted, return empty list");
                return Collections.emptyList();
            }

            wifiList = mWifiManager.getScanResults();
            if (wifiList != null) {
                List<ScanResult> result = new ArrayList<>();
                for (ScanResult scan : wifiList) {
                    if (!TextUtils.isEmpty(scan.SSID)) {
                        addScanResult(result, scan);
                    }
                }
                Log.e(TAG,Thread.currentThread().toString() + "##scan(): " + result);
                return result;
            }

            runtime = System.currentTimeMillis() - start;
        } while (runtime < SCAN_TIMEOUT);

        Log.e(TAG,Thread.currentThread().toString() + "##scan(): fail, return empty list");
        // if fail, don't return null
        return Collections.emptyList();
    }

    @Override
    public String getWifiConnectedSsid()
    {
        WifiInfo mWifiInfo = getConnectionInfo();
        String ssid = null;
        if (mWifiInfo != null && mWifiInfo.getSSID() != null && isWifiConnected())
        {
            int len = mWifiInfo.getSSID().length();
            // mWifiInfo.getBSSID() = "\"" + ssid + "\"";
            if (mWifiInfo.getSSID().startsWith("\"") && mWifiInfo.getSSID().endsWith("\""))
            {
                ssid = mWifiInfo.getSSID().substring(1, len - 1);
            }
            else
            {
                ssid = mWifiInfo.getSSID();
            }

        }
        Log.e(TAG,Thread.currentThread().toString() + "##getWifiConnectedSsid(): " + ssid);
        return ssid;
    }

    @Override
    public boolean enableConnected(String ssid, WifiCypherType.WifiCipherType type, boolean isSsidHidden, String... password)
    {
        if (isWifiConnected(ssid))
        {
            Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): is connected already,return true");
            return true;
        }
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        Context context = RayanApplication.getContext();
        WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // only use the paraters,and remove the exist wifi Configuration
        if (wifiConfiguration != null)
        {
            Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): removeNetwork already in Android System");
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
        }
        int len = password.length;
        if (len > 0 && type != WifiCypherType.WifiCipherType.WIFI_CIPHER_OPEN)
        {
            wifiConfiguration = createWifiConfiguration(ssid, type, isSsidHidden, password[0]);
        }
        else
        {
            wifiConfiguration = createWifiConfiguration(ssid, type, isSsidHidden, null);
        }
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        boolean enableConnected = mWifiManager.enableNetwork(netId, true);
        Log.e(TAG,Thread.currentThread().toString() + "##enableConnected(ssid=[" + ssid + "],type=[" + type
                + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0]) + "]): "
                + enableConnected);
        return enableConnected;
    }

    @Override
    public boolean connect(String ssid, WifiCypherType.WifiCipherType type, boolean isSsidHidden, String... password)
            throws InterruptedException
    {
        if (isWifiConnected(ssid))
        {
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): is connected already,return true");
            return true;
        }
        // delete wifi Configuration when the WifiCipherType changed
        WifiConfiguration wifiConfiguration = getExistWifiConfiguration(ssid);
        if (wifiConfiguration != null && getWifiCipherType(wifiConfiguration) != type)
        {
            Context context = RayanApplication.getContext();
            WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): delete the wifiConfiguration changed cipher type");
        }
        // try the exist wifi Configuration firstly
        if (connect(ssid))
        {
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): connect(ssid)=true,return true");
            return true;
        }
        boolean enableConnected = enableConnected(ssid, type, isSsidHidden, password);
        if (!enableConnected)
        {
            Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type
                    + "],isSsidHidden=[" + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0])
                    + "]): enableConnected(ssid, type, password)=false,return false");
            return false;
        }
        boolean connect = isConnectSuc(ssid);
        Log.e(TAG,Thread.currentThread().toString() + "##connect(ssid=[" + ssid + "],type=[" + type + "],isSsidHidden=["
                + isSsidHidden + "],password=[" + (password.length == 0 ? "null" : password[0]) + "]): " + connect);
        return connect;
    }

    public static void connectToSSID(Context context, String ssid, String password){
        WifiManager wifiManager;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", ssid);
        conf.preSharedKey = String.format("\"%s\"", password);
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        Log.d("MainActivity"," on thread " + Thread.currentThread().getName());
    }
}
