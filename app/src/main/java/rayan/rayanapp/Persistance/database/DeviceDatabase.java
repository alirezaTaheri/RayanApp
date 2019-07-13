package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.DeviceMinimalSSIDIP;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.DevicesDAO;

public class DeviceDatabase {
    private AppDatabase appDatabase;
    private DevicesDAO deviceDAO;
    ExecutorService executorService;
    public DeviceDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        deviceDAO = appDatabase.getDeviceDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public Device getDevice(String chipId){
        return deviceDAO.getDevice(chipId);
    }

    public Maybe<Device> getDeviceFlowable(String chipId){
        return deviceDAO.getDeviceFlowable(chipId);
    }

    public LiveData<List<Device>> getAllDevicesLive(){
        return deviceDAO.getAllLive();
    }

    public List<Device> getAllDevices(){
        return deviceDAO.getAll();
    }
    public Flowable<List<Device>> getAllDevicesFlowable(){
        return deviceDAO.getAllFlowable();
    }

    public Single<List<Device>> getAllDevicesSingle(){
        return deviceDAO.getAllSingle();
    }

    public void addDevice(Device device){
        deviceDAO.add(device);
    }

    public void addDevices(List<Device> devices){
        executorService.execute(()-> deviceDAO.addAll(devices));
         }

    public void updateDevice(Device device){
        executorService.execute(()-> deviceDAO.updateDevice(device));
    }

    public List<String> getAllTopics(){
        return deviceDAO.getAllTopics();
    }
    public List<Device> getAllInGroup(String groupId){
        return deviceDAO.getAllInGroup(groupId);
    }
    public Single<List<Device>> getAllInGroupSingle(String groupId){
        return deviceDAO.getAllInGroupSingle(groupId);
    }
    public LiveData<List<Device>> getAllInGroupLive(String groupId){
        return deviceDAO.getAllInGroupLive(groupId);
    }

    public void deleteDevices(List<Device> devices){
        deviceDAO.deleteDevices(devices);
    }
    public void deleteDevice(Device device){
        executorService.execute(()->deviceDAO.deleteDevice(device));
    }

    public void updateDevices(List<Device> devices){
        executorService.execute(()->deviceDAO.updateDevices(devices));
    }

    public LiveData<List<Device>> getFavoriteDevices(){
        return deviceDAO.getAllFavoritesLive();
    }

    public List<Device> getFavorates(){
        return deviceDAO.getAllFavorites();
    }
}
