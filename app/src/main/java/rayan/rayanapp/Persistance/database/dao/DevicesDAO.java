package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import rayan.rayanapp.Data.Device;

@Dao
public interface DevicesDAO extends BaseDAO<Device>{

    @Query("SELECT * FROM Device ")
    LiveData<List<Device>> getAllLive();

    @Query("SELECT * FROM Device WHERE favorite = 1")
    LiveData<List<Device>> getAllFavoritesLive();

    @Query("SELECT * FROM Device ")
    List<Device> getAll();

    @Query("SELECT * FROM Device WHERE groupId = :groupId")
    List<Device> getAllInGroup(String groupId);

    @Query("SELECT * FROM Device WHERE groupId = :groupId")
    LiveData<List<Device>> getAllInGroupLive(String groupId);

    @Query("SELECT * FROM Device WHERE chipId = :chipId")
    Device getDevice(String chipId);

    @Update
    void updateDevice(Device device);

    @Update
    void updateDevices(List<Device> devices);

    @Query("SELECT topic_topic FROM Device ")
    List<String> getAllTopics();

    @Delete
    void deleteDevices(List<Device> devices);
}
