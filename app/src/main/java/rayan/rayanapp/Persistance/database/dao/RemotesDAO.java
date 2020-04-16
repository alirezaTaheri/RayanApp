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

@Dao
public interface RemotesDAO extends BaseDAO<Remote>{

    @Query("SELECT * FROM Remote ")
    LiveData<List<Remote>> getAllLive();

    @Query("SELECT * FROM Remote WHERE favorite = 1")
    LiveData<List<Remote>> getAllFavoritesLive();

    @Query("SELECT * FROM Remote WHERE favorite = 1")
    List<Remote> getAllFavorites();

    @Query("SELECT * FROM Remote ")
    List<Remote> getAll();

    @Query("SELECT * FROM Remote ")
    Flowable<List<Remote>> getAllFlowable();

    @Query("SELECT * FROM Remote ")
    Single<List<Remote>> getAllSingle();

//    @Query("SELECT * FROM Remote WHERE groupId = :groupId")
//    List<Remote> getAllInGroup(String groupId);
//    @Query("SELECT * FROM Remote WHERE groupId = :groupId")
//    Single<List<Remote>> getAllInGroupSingle(String groupId);
//
//    @Query("SELECT * FROM Remote WHERE groupId = :groupId")
//    LiveData<List<Remote>> getAllInGroupLive(String groupId);
//
    @Query("SELECT * FROM Remote WHERE id = :id")
    Remote getRemote(String id);
//
    @Query("SELECT * FROM Remote WHERE id = :id")
    Maybe<Remote> getRemoteFlowable(String id);

    @Query("SELECT topic FROM Remote")
    List<String> getAllTopics();

    @Update
    void updateRemote(Remote device);

    @Update
    void updateRemotes(List<Remote> devices);

    @Delete
    void deleteRemotes(List<Remote> devices);
    @Delete
    void deleteRemote(Remote device);
}
