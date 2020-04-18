package rayan.rayanapp.Data;

import rayan.rayanapp.Util.AppConstants;

public class BaseDevice {
    private int position, inGroupPosition, favoritePosition;
    private String deviceType;
    private String baseId;
    private boolean favorite;

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
}
