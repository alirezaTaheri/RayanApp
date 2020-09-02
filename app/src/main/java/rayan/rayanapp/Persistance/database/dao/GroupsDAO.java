package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import io.reactivex.Flowable;
import rayan.rayanapp.Persistance.models.BaseDevicesSummary;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;

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

//    @Query("SELECT g.id, d.id, rh.id from `group` as g left join device as d on d.groupId = g.id left join remotehub as rh on rh.groupId = g.id  where g.id=")
//    String getAllGroupBaseDevices(String id);

    @Query("SELECT (select COUNT(*) from `Device`) as devicesCount, (select COUNT(*) from `RemoteHub`) as remoteHubsCount," +
            "(select COUNT(*) from `Remote`) as remotesCount")
    BaseDevicesSummary getAllBaseDevices();

    @Query("SELECT (select COUNT(*) from `Device` where groupId=:gid) as devicesCount, (select COUNT(*) from `RemoteHub` where groupId=:gid) as remoteHubsCount," +
            "(select COUNT(*) from `Remote` where groupId=:gid) as remotesCount")
    BaseDevicesSummary getAllGroupBaseDevices(String gid);

    @Query("SELECT COUNT(*) from `Group`")
    int abc();

    @Update
    void updateGroup(Group group);

    @Update
    void updateGroups(List<Group> groups);

    @Delete
    void deleteGroups(List<Group> groups);
    @Delete
    void deleteGroup(Group group);


}
