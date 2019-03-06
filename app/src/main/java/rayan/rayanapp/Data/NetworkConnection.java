package rayan.rayanapp.Data;

public class NetworkConnection  {
    private int type;
    private boolean isConnected;
    private boolean mqttConnected;
    private boolean udpServerStabled;
    private String currentSSID;

    public NetworkConnection(int type, boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }


    public NetworkConnection(int type, boolean isConnected, String currentSSID) {
        this.type = type;
        this.isConnected = isConnected;
        this.currentSSID = currentSSID;
    }

    public String getCurrentSSID() {
        return currentSSID;
    }

    public void setCurrentSSID(String currentSSID) {
        this.currentSSID = currentSSID;
    }

    public int getType() {
        return type;
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public String toString() {
        return "NetworkConnection{" +
                "type=" + type +
                ", isConnected=" + isConnected +
                ", mqttConnected=" + mqttConnected +
                ", udpServerStabled=" + udpServerStabled +
                ", currentSSID='" + currentSSID + '\'' +
                '}';
    }
}
