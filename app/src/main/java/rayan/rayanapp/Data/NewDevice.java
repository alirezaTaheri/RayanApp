package rayan.rayanapp.Data;

public class NewDevice {
    private String SSID;
    private String BSSID;
    private String capability;
    private int level;

    public NewDevice(String SSID, String BSSID, String capability, int level) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.capability = capability;
        this.level = level;
    }

    public String getSSID() {
        return SSID;
    }

    @Override
    public String toString() {
        return "NewDevice{" +
                "SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", capability='" + capability + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
