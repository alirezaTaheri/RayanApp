package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.DevicesDAO;

public class DeviceDatabase {
    private AppDatabase appDatabase;
    private DevicesDAO deviceDAO;
    public DeviceDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        deviceDAO = appDatabase.getDeviceDAO();
    }
    public Device getDevice(String chipId){
        return deviceDAO.getDevice(chipId);
    }

    public LiveData<List<Device>> getAllDevicesLive(){
        return deviceDAO.getAllLive();
    }

    public List<Device> getAllDevices(){
        return deviceDAO.getAll();
    }

    public void addDevice(Device device){
        deviceDAO.add(device);
    }

    public void addDevices(List<Device> devices){
        deviceDAO.addAll(devices);
    }

    public void updateDevice(Device device){
        deviceDAO.updateDevice(device);
    }

    public List<String> getAllTopics(){
        return deviceDAO.getAllTopics();
    }
    public List<Device> getAllInGroup(String groupId){
        return deviceDAO.getAllInGroup(groupId);
    }
    public LiveData<List<Device>> getAllInGroupLive(String groupId){
        return deviceDAO.getAllInGroupLive(groupId);
    }

    public void deleteDevices(List<Device> devices){
        deviceDAO.deleteDevices(devices);
    }
    public void updateDevices(List<Device> devices){
        deviceDAO.updateDevices(devices);
    }
    public LiveData<List<Device>> getFavoriteDevices(){
        return deviceDAO.getAllFavoritesLive();
    }
}
