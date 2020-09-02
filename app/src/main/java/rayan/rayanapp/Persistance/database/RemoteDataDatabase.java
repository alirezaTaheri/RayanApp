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
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.RemoteDatasDAO;

public class RemoteDataDatabase {
    private AppDatabase appDatabase;
    private RemoteDatasDAO remoteDatasDAO;
    ExecutorService executorService;
    public RemoteDataDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        remoteDatasDAO = appDatabase.getRemoteDataDAO();
        executorService= Executors.newSingleThreadExecutor();
    }
    public RemoteData getRemoteData(String chipId){
        return remoteDatasDAO.getRemoteData(chipId);
    }

    public Maybe<RemoteData> getRemoteDataFlowable(String chipId){
        return remoteDatasDAO.getRemoteDataFlowable(chipId);
    }

    public LiveData<List<RemoteData>> getAllRemoteDatasLive(){
        return remoteDatasDAO.getAllLive();
    }

    public List<RemoteData> getAllRemoteDatas(){
        return remoteDatasDAO.getAll();
    }
    public Flowable<List<RemoteData>> getAllRemoteDatasFlowable(){
        return remoteDatasDAO.getAllFlowable();
    }

    public Single<List<RemoteData>> getAllRemoteDatasSingle(){
        return remoteDatasDAO.getAllSingle();
    }

    public void addRemoteData(RemoteData remote){
        remoteDatasDAO.add(remote);
    }

    public void addRemoteDatas(List<RemoteData> remoteDatas){
        executorService.execute(()-> remoteDatasDAO.addAll(remoteDatas));
         }

    public void updateRemoteData(RemoteData remote){
        executorService.execute(()-> remoteDatasDAO.updateRemoteData(remote));
    }

    public void deleteRemoteDatas(List<RemoteData> remoteDatas){
        remoteDatasDAO.deleteRemoteDatas(remoteDatas);
    }
    public void deleteRemoteData(RemoteData remote){
        executorService.execute(()->remoteDatasDAO.deleteRemoteData(remote));
    }

    public void updateRemoteDatas(List<RemoteData> remoteDatas){
        executorService.execute(()->remoteDatasDAO.updateRemoteDatas(remoteDatas));
    }
}
