package rayan.rayanapp.Data;

import rayan.rayanapp.Util.AppConstants;

public class BaseDevice {
    private int position, inGroupPosition, favoritePosition;
    private String deviceType;
    private String baseId;
    private boolean favorite, verified;
    private String version;
    public BaseDevice() {
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public BaseDevice(BaseDevice baseDevice) {
        this.position = baseDevice.getPosition();
        this.inGroupPosition = baseDevice.getInGroupPosition();
        this.deviceType = baseDevice.getDeviceType();
        this.baseId = baseDevice.getBaseId();
        this.favorite = baseDevice.isFavorite();
        this.favoritePosition = baseDevice.getFavoritePosition();
        this.version = baseDevice.getVersion();
        this.verified = baseDevice.isVerified();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getFavoritePosition() {
        return favoritePosition;
    }

    public void setFavoritePosition(int favoritePosition) {
        this.favoritePosition = favoritePosition;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getInGroupPosition() {
        return inGroupPosition;
    }

    public void setInGroupPosition(int inGroupPosition) {
        this.inGroupPosition = inGroupPosition;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
