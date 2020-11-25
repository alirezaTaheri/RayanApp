package rayan.rayanapp.Persistance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;

@Dao
public interface GroupsDAO extends BaseDAO<Group> {

    @Query("SELECT * FROM `Group`")
    List<Group> getAll();

    @Query("SELECT * FROM `Group`")
    Flowable<List<Group>> getAllFlowable();

    @Query("SELECT * FROM `Group`")
    LiveData<List<Group>> getAllLive();

    @Query("SELECT * FROM `Group` WHERE id = :id")
    Group getGroup(String id);

    @Query("SELECT * FROM `Group` WHERE id = :id")
    Flowable<Group> getGroupFlowable(String id);

    @Query("SELECT * FROM `Group` WHERE id = :id")
    LiveData<Group> getGroupLive(String id);

    @Update
    void updateGroup(Group group);

    @Update
    void updateGroups(List<Group> groups);

    @Delete
    void deleteGroups(List<Group> groups);
    @Delete
    void deleteGroup(Group group);


}
