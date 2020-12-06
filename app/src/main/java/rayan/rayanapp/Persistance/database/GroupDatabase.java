package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.GroupsDAO;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;

public class GroupDatabase {
    private AppDatabase appDatabase;
    private GroupsDAO groupsDAO;
    private ExecutorService executorService;
    public GroupDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        groupsDAO = appDatabase.getGroupDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public void addGroup(Group group){
        groupsDAO.add(group);
    }
    public Group getGroup(String id){
        return groupsDAO.getGroup(id);
    }
    public Flowable<Group> getGroupFlowable(String id){
        return groupsDAO.getGroupFlowable(id);
    }
    public LiveData<Group> getGroupLive(String id){
        return groupsDAO.getGroupLive(id);
    }
    public LiveData<List<Group>> getAllGroupsLive(){
        return groupsDAO.getAllLive();
    }
    public List<Group> getAllGroups(){
        return groupsDAO.getAll();
    }
    public Flowable<List<Group>> getAllGroupsFlowable(){
        return groupsDAO.getAllFlowable();
    }
    public void addGroups(List<Group> devices){
        executorService.execute(()->groupsDAO.addAll(devices));
    }
    public void updateGroup(Group device){
        executorService.execute(()->groupsDAO.updateGroup(device));
    }
    public void deleteGroups(List<Group> devices){
        executorService.execute(()->groupsDAO.deleteGroups(devices));
    }
    public void deleteGroup(Group group){
        executorService.execute(()->groupsDAO.deleteGroup(group));
    }
    public void updateGroups(List<Group> devices){
        groupsDAO.updateGroups(devices);
    }

    public void getAllCount(String groupId){
        Log.e("FFFFFFFFFFFFF","LLLLLLLLLLLLLLLL" + groupsDAO.getAllBaseDevices());
        Log.e("FFFFFFFFFFFFF","LLLLLLLLLLLLLLLL" + groupsDAO.getAllGroupBaseDevices("5ee3aed04703f279137e6c0a"));
//        Cursor c = groupsDAO.getAllGroupBaseDevices();
//        Log.e("FFFFFFFF",""+c.getColumnCount());
//        Log.e("FFFFFFFF",""+c.getCount());
//        Log.e("FFFFFFFF",""+c.getPosition());
//        Log.e("FFFFFFFF",""+ Arrays.toString(c.getColumnNames()));
//        while(c.moveToNext()) {
//            int nameFieldColumnIndex = c.getColumnIndex("c1");
//            int a = c.getInt(nameFieldColumnIndex);
//            int numberFieldColumnIndex = c.getColumnIndex("c2");
//            int b = c.getInt(numberFieldColumnIndex);
//            Log.e("FFFFFFFF",a+"  ||  "+b);
//        }
//        c.close();

    }
}
