package rayan.rayanapp.Persistance.database;

import androidx.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import rayan.rayanapp.Data.UserMembership;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.UserMembershipDAO;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;
import rayan.rayanapp.Util.AppConstants;

public class UserMembershipDatabase {
    private AppDatabase appDatabase;
    private UserMembershipDAO membershipDAO;
    private ExecutorService executorService;
    public UserMembershipDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        membershipDAO = appDatabase.getUserMembershipDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public void addUserMembership(UserMembership userMembership){
        membershipDAO.add(userMembership);
    }
    public UserMembership getUserMembership(String id){
        return membershipDAO.getUserMembership(id);
    }
    public LiveData<List<UserMembership>> getAllUserMembershipsLive(){
        return membershipDAO.getAllLive();
    }
    public List<UserMembership> getAllUserMemberships(){
        return membershipDAO.getAll();
    }
    public Flowable<List<UserMembership>> getAllUserMembershipsFlowable(){
        return membershipDAO.getAllFlowable();
    }
    public LiveData<List<User>> getAllUsersInGroup(String groupId){
        return membershipDAO.getUsersInGroup(groupId);
    }

    public LiveData<List<User>> getJustUsersInGroup(String groupId){
        return membershipDAO.getJustUsersOrAdminsInGroup(groupId, AppConstants.USER_TYPE);
    }

    public LiveData<List<User>> getJustAdminsInGroup(String groupId){
        return membershipDAO.getJustUsersOrAdminsInGroup(groupId, AppConstants.ADMIN_TYPE);
    }

    public void addUserMemberships(List<UserMembership> devices){
        executorService.execute(()-> membershipDAO.addAll(devices));
    }
    public void updateUserMembership(UserMembership userMembership){
        executorService.execute(()-> membershipDAO.updateMembership(userMembership));
    }
    public void deleteUserMemberships(List<UserMembership> userMemberships){
        executorService.execute(()-> membershipDAO.deleteMembership(userMemberships));
    }
    public void deleteUserMembership(UserMembership userMembership){
        executorService.execute(()-> membershipDAO.deleteMembership(userMembership));
    }
    public void updateUserMemberships(List<UserMembership> userMemberships){
        membershipDAO.updateMembership(userMemberships);
    }

}
