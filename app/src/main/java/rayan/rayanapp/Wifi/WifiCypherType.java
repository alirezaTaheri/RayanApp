package rayan.rayanapp.Wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiCypherType {
    public enum WifiCipherType {
        /**
         * WEP
         */
        WIFI_CIPHER_WEP,
        /**
         * WPA or WPA2
         */
        WIFI_CIPHER_WPA,
        /**
         * OPEN means the wifi don't have password
         */
        WIFI_CIPHER_OPEN,
        /**
         * Invalid wifi cipher type, it shouldn't happen
         */
        WIFI_CIPHER_INVALID;

        public static WifiCipherType getWifiCipherType(ScanResult scanResult) {
            String capa = scanResult.capabilities;
            Log.d(WifiCipherType.class.getSimpleName(), capa);
            if (capa.contains("PSK")) {
                Log.e(WifiCipherType.class.getSimpleName(), "WifiCipherType Is: " + WIFI_CIPHER_WPA);
                return WIFI_CIPHER_WPA;
            }
            else if (capa.contains("WEP")) {
                Log.e(WifiCipherType.class.getSimpleName(), "WifiCipherType Is: " + WIFI_CIPHER_WEP);
                return WIFI_CIPHER_WEP;
            }
            else {
                Log.e(WifiCipherType.class.getSimpleName(), "WifiCipherType Is: " + WIFI_CIPHER_OPEN);
                return WIFI_CIPHER_OPEN;
            }
        }
    }
}
