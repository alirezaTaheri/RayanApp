package rayan.rayanapp.Persistance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import rayan.rayanapp.Data.UserMembership;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;

@Dao
public interface UserMembershipDAO extends BaseDAO<UserMembership>{

    @Query("SELECT * FROM UserMembership ")
    LiveData<List<UserMembership>> getAllLive();

    @Query("SELECT * FROM UserMembership ")
    List<UserMembership> getAll();

    @Query("SELECT * FROM UserMembership ")
    Flowable<List<UserMembership>> getAllFlowable();

    @Query("SELECT * FROM UserMembership ")
    Single<List<UserMembership>> getAllSingle();

    @Query("SELECT * FROM User INNER JOIN UserMembership ON UserMembership.userId = User.id WHERE UserMembership.groupId = :groupId")
    LiveData<List<User>> getUsersInGroup(String groupId);

    @Query("SELECT * FROM User INNER JOIN UserMembership ON UserMembership.userId = User.id WHERE UserMembership.groupId = :groupId AND UserMembership.userType = :type")
    LiveData<List<User>> getJustUsersOrAdminsInGroup(String groupId, String type);

    @Query("SELECT * FROM UserMembership WHERE groupId = :groupId")
    Single<List<UserMembership>> getAllInGroupSingle(String groupId);

    @Query("SELECT * FROM UserMembership WHERE groupId = :groupId")
    LiveData<List<UserMembership>> getAllInGroupLive(String groupId);

    @Query("SELECT * FROM UserMembership WHERE membershipId = :id")
    UserMembership getUserMembership(String id);

    @Update
    void updateMembership(UserMembership UserMembership);

    @Update
    void updateMembership(List<UserMembership> users);

    @Delete
    void deleteMembership(List<UserMembership> users);
    @Delete
    void deleteMembership(UserMembership UserMembership);
}
