package rayan.rayanapp.Persistance.database;

import android.content.Context;

import java.util.List;

import rayan.rayanapp.Data.LocallyChange;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.LocallyChangesDAO;

public class LocallyChangesDatabase {
    private AppDatabase appDatabase;
    private LocallyChangesDAO locallyChangesDAO;
    public LocallyChangesDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        locallyChangesDAO = appDatabase.getLocallyChangesDAO();
    }

    public void removeAll(){
        locallyChangesDAO.deleteAll();
    }

    public List<LocallyChange> getAllRequest(){
        return locallyChangesDAO.getAll();
    }

    public List<LocallyChange> getAllRequest(LocallyChange.Type type){
        return locallyChangesDAO.getAll(type.toString());
    }

    public void removeRequest(LocallyChange id){
        locallyChangesDAO.deleteItem(id);
    }

    public void addRequest(LocallyChange locallyChange){
        locallyChangesDAO.add(locallyChange);
    }

}
