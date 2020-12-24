package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;

@Dao
public interface RemoteDatasDAO extends BaseDAO<RemoteData>{

    @Query("SELECT * FROM RemoteData ")
    LiveData<List<RemoteData>> getAllLive();

    @Query("SELECT * FROM RemoteData WHERE remoteId = :remoteId")
    LiveData<List<RemoteData>> getDataOfRemoteLive(String remoteId);

    @Query("SELECT * FROM RemoteData ")
    List<RemoteData> getAll();

    @Query("SELECT * FROM RemoteData ")
    Flowable<List<RemoteData>> getAllFlowable();

    @Query("SELECT * FROM RemoteData ")
    Single<List<RemoteData>> getAllSingle();

//    @Query("SELECT * FROM RemoteData WHERE groupId = :groupId")
//    List<RemoteData> getAllInGroup(String groupId);
//    @Query("SELECT * FROM RemoteData WHERE groupId = :groupId")
//    Single<List<RemoteData>> getAllInGroupSingle(String groupId);
//
//    @Query("SELECT * FROM RemoteData WHERE groupId = :groupId")
//    LiveData<List<RemoteData>> getAllInGroupLive(String groupId);
//
    @Query("SELECT * FROM RemoteData WHERE id = :id")
    RemoteData getRemoteData(String id);
//
    @Query("SELECT * FROM RemoteData WHERE id = :id")
    Maybe<RemoteData> getRemoteDataFlowable(String id);

    @Update
    void updateRemoteData(RemoteData device);

    @Update
    void updateRemoteDatas(List<RemoteData> devices);

    @Delete
    void deleteRemoteDatas(List<RemoteData> devices);
    @Delete
    void deleteRemoteData(RemoteData device);
}
