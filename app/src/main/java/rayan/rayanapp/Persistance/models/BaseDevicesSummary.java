package rayan.rayanapp.Persistance.models;

public class BaseDevicesSummary {
    private int devicesCount, remoteHubsCount, remotesCount;

    public int getDevicesCount() {
        return devicesCount;
    }

    public void setDevicesCount(int devicesCount) {
        this.devicesCount = devicesCount;
    }

    public int getRemoteHubsCount() {
        return remoteHubsCount;
    }

    public void setRemoteHubsCount(int remoteHubsCount) {
        this.remoteHubsCount = remoteHubsCount;
    }

    public int getRemotesCount() {
        return remotesCount;
    }

    public void setRemotesCount(int remotesCount) {
        this.remotesCount = remotesCount;
    }

    @Override
    public String toString() {
        return "BaseDevicesSummary{" +
                "devicesCount=" + devicesCount +
                ", remoteHubsCount=" + remoteHubsCount +
                ", remotesCount=" + remotesCount +
                '}';
    }
}
