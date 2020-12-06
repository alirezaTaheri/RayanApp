package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;

@Dao
public interface UsersDAO extends BaseDAO<User>{

    @Query("SELECT * FROM User ")
    LiveData<List<User>> getAllLive();

    @Query("SELECT * FROM User WHERE type = :type AND groupId = :groupId")
    LiveData<List<User>> getAdminsLive(String groupId, String type);

    @Query("SELECT * FROM User ")
    List<User> getAll();

    @Query("SELECT * FROM User ")
    Flowable<List<User>> getAllFlowable();

    @Query("SELECT * FROM User ")
    Single<List<User>> getAllSingle();

    @Query("SELECT * FROM User WHERE groupId = :groupId")
    List<User> getAllInGroup(String groupId);
    @Query("SELECT * FROM User WHERE groupId = :groupId")
    Single<List<User>> getAllInGroupSingle(String groupId);

    @Query("SELECT * FROM User WHERE groupId = :groupId")
    LiveData<List<User>> getAllInGroupLive(String groupId);

    @Query("SELECT * FROM User WHERE id = :id")
    User getUser(String id);

    @Update
    void updateUser(User user);

    @Update
    void updateUsers(List<User> users);

    @Delete
    void deleteUsers(List<User> users);
    @Delete
    void deleteUser(User user);
}
