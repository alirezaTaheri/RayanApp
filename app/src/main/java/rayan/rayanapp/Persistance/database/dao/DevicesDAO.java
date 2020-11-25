package rayan.rayanapp.Persistance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import rayan.rayanapp.Data.Device;

@Dao
public interface DevicesDAO extends BaseDAO<Device>{

    @Query("SELECT * FROM Device ")
    LiveData<List<Device>> getAllLive();

    @Query("SELECT * FROM Device WHERE favorite = 1")
    LiveData<List<Device>> getAllFavoritesLive();

    @Query("SELECT * FROM Device WHERE favorite = 1")
    List<Device> getAllFavorites();

    @Query("SELECT * FROM Device ")
    List<Device> getAll();

    @Query("SELECT * FROM Device ")
    Flowable<List<Device>> getAllFlowable();

    @Query("SELECT * FROM Device ")
    Single<List<Device>> getAllSingle();

    @Query("SELECT * FROM Device WHERE groupId = :groupId")
    List<Device> getAllInGroup(String groupId);
    @Query("SELECT * FROM Device WHERE groupId = :groupId")
    Single<List<Device>> getAllInGroupSingle(String groupId);

    @Query("SELECT * FROM Device WHERE groupId = :groupId")
    LiveData<List<Device>> getAllInGroupLive(String groupId);

    @Query("SELECT * FROM Device WHERE chipId = :chipId")
    Device getDevice(String chipId);

    @Query("SELECT * FROM Device WHERE chipId = :chipId")
    Maybe<Device> getDeviceFlowable(String chipId);

    @Update
    void updateDevice(Device device);

    @Update
    void updateDevices(List<Device> devices);

    @Query("SELECT topic_topic FROM Device ")
    List<String> getAllTopics();

    @Delete
    void deleteDevices(List<Device> devices);
    @Delete
    void deleteDevice(Device device);
}
