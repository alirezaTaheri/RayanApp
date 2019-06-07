package rayan.rayanapp.Listeners;

public interface NetworkConnectivityListener {
    void wifiNetwork(boolean connected, String ssid);
    void mobileNetwork(boolean connected);
    void vpnNetwork();
    void notConnected();
}
