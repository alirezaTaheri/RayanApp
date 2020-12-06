package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.UsersDAO;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.Util.AppConstants;

public class UserDatabase {
    private AppDatabase appDatabase;
    private UsersDAO usersDAO;
    private ExecutorService executorService;
    public UserDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        usersDAO = appDatabase.getUsersDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public void addUser(User user){
        usersDAO.add(user);
    }

    public LiveData<List<User>> getAdminsInGroup(String groupId){
        return usersDAO.getAdminsLive(groupId, AppConstants.ADMIN_TYPE);
    }
    public User getUser(String id){
        return usersDAO.getUser(id);
    }
    public LiveData<List<User>> getAllUsersLive(){
        return usersDAO.getAllLive();
    }
    public List<User> getAllUsers(){
        return usersDAO.getAll();
    }
    public Flowable<List<User>> getAllUsersFlowable(){
        return usersDAO.getAllFlowable();
    }

    public void addUsers(List<User> devices){
        executorService.execute(()-> usersDAO.addAll(devices));
    }
    public void updateUser(User user){
        executorService.execute(()-> usersDAO.updateUser(user));
    }
    public void deleteUsers(List<User> users){
        executorService.execute(()-> usersDAO.deleteUsers(users));
    }
    public void deleteUser(User user){
        executorService.execute(()-> usersDAO.deleteUser(user));
    }
    public void updateUsers(List<User> users){
        usersDAO.updateUsers(users);
    }

}
