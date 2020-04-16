package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.RemoteHubsDAO;

public class RemoteHubDatabase {
    private AppDatabase appDatabase;
    private RemoteHubsDAO remoteHubDAO;
    ExecutorService executorService;
    public RemoteHubDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        remoteHubDAO = appDatabase.getRemoteHubDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public RemoteHub getRemoteHub(String chipId){
        return remoteHubDAO.getRemoteHub(chipId);
    }

    public Maybe<RemoteHub> getRemoteHubFlowable(String chipId){
        return remoteHubDAO.getRemoteHubFlowable(chipId);
    }

    public LiveData<List<RemoteHub>> getAllRemoteHubsLive(){
        return remoteHubDAO.getAllLive();
    }

    public List<RemoteHub> getAllRemoteHubs(){
        return remoteHubDAO.getAll();
    }
    public Flowable<List<RemoteHub>> getAllRemoteHubsFlowable(){
        return remoteHubDAO.getAllFlowable();
    }

    public Single<List<RemoteHub>> getAllRemoteHubsSingle(){
        return remoteHubDAO.getAllSingle();
    }

    public void addRemoteHub(RemoteHub remoteHub){
        remoteHubDAO.add(remoteHub);
    }

    public void addRemoteHubs(List<RemoteHub> remoteHubs){
        executorService.execute(()-> remoteHubDAO.addAll(remoteHubs));
         }

    public void updateRemoteHub(RemoteHub remoteHub){
        executorService.execute(()-> remoteHubDAO.updateRemoteHub(remoteHub));
    }

    public List<String> getAllTopics(){
        return remoteHubDAO.getAllTopics();
    }
//    public List<RemoteHub> getAllInGroup(String groupId){
//        return remoteHubDAO.getAllInGroup(groupId);
//    }
//    public Single<List<RemoteHub>> getAllInGroupSingle(String groupId){
//        return remoteHubDAO.getAllInGroupSingle(groupId);
//    }
//    public LiveData<List<RemoteHub>> getAllInGroupLive(String groupId){
//        return remoteHubDAO.getAllInGroupLive(groupId);
//    }

    public void deleteRemoteHubs(List<RemoteHub> remoteHubs){
        remoteHubDAO.deleteRemoteHubs(remoteHubs);
    }
    public void deleteRemoteHub(RemoteHub remoteHub){
        executorService.execute(()->remoteHubDAO.deleteRemoteHub(remoteHub));
    }

    public void updateRemoteHubs(List<RemoteHub> remoteHubs){
        executorService.execute(()->remoteHubDAO.updateRemoteHubs(remoteHubs));
    }

    public LiveData<List<RemoteHub>> getFavoriteRemoteHubs(){
        return remoteHubDAO.getAllFavoritesLive();
    }

    public List<RemoteHub> getFavorates(){
        return remoteHubDAO.getAllFavorites();
    }
}
