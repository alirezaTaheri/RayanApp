package rayan.rayanapp.Data;

public class AccessPoint {
    private String SSID;
    private String BSSID;
    private String capability;
    private int level;
    private boolean selected;

    public AccessPoint() {
    }

    public AccessPoint(String SSID, String BSSID, String capability, int level) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.capability = capability;
        this.level = level;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSSID() {
        return SSID;
    }

    @Override
    public String toString() {
        return "AccessPoint{" +
                "SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", capability='" + capability + '\'' +
                ", level='" + level + '\'' +
                '}';
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }
}
