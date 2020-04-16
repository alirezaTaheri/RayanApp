package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import rayan.rayanapp.Data.RemoteHub;

@Dao
public interface RemoteHubsDAO extends BaseDAO<RemoteHub>{

    @Query("SELECT * FROM RemoteHub ")
    LiveData<List<RemoteHub>> getAllLive();

    @Query("SELECT * FROM RemoteHub WHERE favorite = 1")
    LiveData<List<RemoteHub>> getAllFavoritesLive();

    @Query("SELECT * FROM RemoteHub WHERE favorite = 1")
    List<RemoteHub> getAllFavorites();

    @Query("SELECT * FROM RemoteHub ")
    List<RemoteHub> getAll();

    @Query("SELECT * FROM RemoteHub ")
    Flowable<List<RemoteHub>> getAllFlowable();

    @Query("SELECT * FROM RemoteHub ")
    Single<List<RemoteHub>> getAllSingle();

//    @Query("SELECT * FROM RemoteHub WHERE groupId = :groupId")
//    List<RemoteHub> getAllInGroup(String groupId);
//    @Query("SELECT * FROM RemoteHub WHERE groupId = :groupId")
//    Single<List<RemoteHub>> getAllInGroupSingle(String groupId);
//
//    @Query("SELECT * FROM RemoteHub WHERE groupId = :groupId")
//    LiveData<List<RemoteHub>> getAllInGroupLive(String groupId);
//
    @Query("SELECT * FROM RemoteHub WHERE id = :id")
    RemoteHub getRemoteHub(String id);
//
    @Query("SELECT * FROM RemoteHub WHERE id = :id")
    Maybe<RemoteHub> getRemoteHubFlowable(String id);

    @Query("SELECT topic FROM RemoteHub")
    List<String> getAllTopics();

    @Update
    void updateRemoteHub(RemoteHub remoteHub);

    @Update
    void updateRemoteHubs(List<RemoteHub> remoteHubs);

    @Delete
    void deleteRemoteHubs(List<RemoteHub> remoteHubs);
    @Delete
    void deleteRemoteHub(RemoteHub remoteHub);
}
