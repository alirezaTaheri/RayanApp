package rayan.rayanapp.Data;

public class DeviceMinimalSSIDIP {
    private String chipId, ssid, ip;

    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "DeviceMinimalSSIDIP{" +
                "chipId='" + chipId + '\'' +
                ", ssid='" + ssid + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
