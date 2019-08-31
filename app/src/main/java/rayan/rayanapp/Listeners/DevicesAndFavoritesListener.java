package rayan.rayanapp.Listeners;

import java.util.List;

import rayan.rayanapp.Data.Device;

public interface DevicesAndFavoritesListener {
    boolean isInDevicesFragment(String chipId);
    List<Device> getDevices();
}
