package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import rayan.rayanapp.Retrofit.Models.Responses.Group;

@Dao
public interface GroupsDAO extends BaseDAO<Group> {

    @Query("SELECT * FROM `Group`")
    List<Group> getAll();

    @Query("SELECT * FROM `Group`")
    LiveData<List<Group>> getAllLive();

    @Query("SELECT * FROM `Group` WHERE id = :id")
    Group getGroup(String id);

    @Update
    void updateGroup(Group group);

    @Update
    void updateGroups(List<Group> groups);

    @Delete
    void deleteGroups(List<Group> groups);


}
