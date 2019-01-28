package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.GroupsDAO;
import rayan.rayanapp.Retrofit.Models.Group;

public class GroupDatabase {
    private AppDatabase appDatabase;
    private GroupsDAO groupsDAO;
    public GroupDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        groupsDAO = appDatabase.getGroupDAO();
    }
    public void addGroup(Group group){
        groupsDAO.add(group);
    }
    public Group getGroup(String id){
        return groupsDAO.getGroup(id);
    }
    public LiveData<List<Group>> getAllGroupsLive(){
        return groupsDAO.getAllLive();
    }
    public List<Group> getAllGroups(){
        return groupsDAO.getAll();
    }
    public void addGroups(List<Group> devices){
        groupsDAO.addAll(devices);
    }
    public void updateGroup(Group device){
        groupsDAO.updateGroup(device);
    }
    public void deleteGroups(List<Group> devices){
        groupsDAO.deleteGroups(devices);
    }
    public void updateGroups(List<Group> devices){
        groupsDAO.updateGroups(devices);
    }
}
