package rayan.rayanapp.Data;

import android.net.NetworkInfo;

public class ConnectionStatusModel {
    private String ssid;
    private int type;
    private String typeName;
    private NetworkInfo.State state;

    public ConnectionStatusModel(String ssid, int type, String typeName, NetworkInfo.State state) {
        this.ssid = ssid;
        this.type = type;
        this.typeName = typeName;
        this.state = state;
    }

    @Override
    public String toString() {
        return "ConnectionStatusModel{" +
                "ssid='" + ssid + '\'' +
                ", type=" + type +
                ", typeName='" + typeName + '\'' +
                ", state=" + state +
                '}';
    }

    public String getSsid() {
        return ssid;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public NetworkInfo.State getState() {
        return state;
    }
}
