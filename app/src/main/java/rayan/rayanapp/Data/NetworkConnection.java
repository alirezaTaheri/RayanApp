package rayan.rayanapp.Data;

public class NetworkConnection  {
    private int type;
    private boolean isConnected;

    public NetworkConnection(int type, boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }

    public int getType() {
        return type;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    @Override
    public String toString() {
        return "NetworkConnection{" +
                "type=" + type +
                ", isConnected=" + isConnected +
                '}';
    }
}
