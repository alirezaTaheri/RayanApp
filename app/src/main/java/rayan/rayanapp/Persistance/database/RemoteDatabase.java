package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.RemotesDAO;

public class RemoteDatabase {
    private AppDatabase appDatabase;
    private RemotesDAO remoteDAO;
    ExecutorService executorService;
    public RemoteDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        remoteDAO = appDatabase.getRemoteDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public Remote getRemote(String chipId){
        return remoteDAO.getRemote(chipId);
    }

    public Maybe<Remote> getRemoteFlowable(String chipId){
        return remoteDAO.getRemoteFlowable(chipId);
    }

    public LiveData<List<Remote>> getAllRemotesLive(){
        return remoteDAO.getAllLive();
    }

    public List<Remote> getAllRemotes(){
        return remoteDAO.getAll();
    }
    public Flowable<List<Remote>> getAllRemotesFlowable(){
        return remoteDAO.getAllFlowable();
    }

    public Single<List<Remote>> getAllRemotesSingle(){
        return remoteDAO.getAllSingle();
    }

    public void addRemote(Remote remote){
        remoteDAO.add(remote);
    }

    public void addRemotes(List<Remote> remotes){
        executorService.execute(()-> remoteDAO.addAll(remotes));
         }

    public void updateRemote(Remote remote){
        executorService.execute(()-> remoteDAO.updateRemote(remote));
    }

    public List<String> getAllTopics(){
        return remoteDAO.getAllTopics();
    }
//    public List<Remote> getAllInGroup(String groupId){
//        return remoteDAO.getAllInGroup(groupId);
//    }
//    public Single<List<Remote>> getAllInGroupSingle(String groupId){
//        return remoteDAO.getAllInGroupSingle(groupId);
//    }
//    public LiveData<List<Remote>> getAllInGroupLive(String groupId){
//        return remoteDAO.getAllInGroupLive(groupId);
//    }

    public void deleteRemotes(List<Remote> remotes){
        remoteDAO.deleteRemotes(remotes);
    }
    public void deleteRemote(Remote remote){
        executorService.execute(()->remoteDAO.deleteRemote(remote));
    }

    public void updateRemotes(List<Remote> remotes){
        executorService.execute(()->remoteDAO.updateRemotes(remotes));
    }

    public LiveData<List<Remote>> getFavoriteRemotes(){
        return remoteDAO.getAllFavoritesLive();
    }
    public LiveData<List<Remote>> getRemotesOfRemoteHub(String remoteHubId){
        return remoteDAO.getRemotesOfRemoteHub(remoteHubId);
    }

    public List<Remote> getFavorates(){
        return remoteDAO.getAllFavorites();
    }
}
