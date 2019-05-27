package rayan.rayanapp.Data;

public class DeviceCommunicationParams {
    private String chipId, deviceSsid, currentSsid, ip;
    boolean mqttConnected;

    public DeviceCommunicationParams(String deviceSsid, String currentSsid, String ip, boolean mqttConnected) {
        this.deviceSsid = deviceSsid;
        this.currentSsid = currentSsid;
        this.ip = ip;
        this.mqttConnected = mqttConnected;
    }

    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    public String getDeviceSsid() {
        return deviceSsid;
    }

    public void setDeviceSsid(String deviceSsid) {
        this.deviceSsid = deviceSsid;
    }

    public String getCurrentSsid() {
        return currentSsid;
    }

    public void setCurrentSsid(String currentSsid) {
        this.currentSsid = currentSsid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isMqttConnected() {
        return mqttConnected;
    }

    public void setMqttConnected(boolean mqttConnected) {
        this.mqttConnected = mqttConnected;
    }

    @Override
    public String toString() {
        return
                " deviceSsid='" + deviceSsid + '\'' +
                ", currentSsid='" + currentSsid + '\'' +
                ", ip='" + ip + '\'' +
                ", mqttConnected=" + mqttConnected +
                '}';
    }
}
