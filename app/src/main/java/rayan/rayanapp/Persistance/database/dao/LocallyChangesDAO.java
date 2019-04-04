package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.LocallyChange;

@Dao
public interface LocallyChangesDAO extends BaseDAO<LocallyChange>{

    @Query("SELECT * FROM LocallyChange ")
    List<LocallyChange> getAll();

    @Query("SELECT * FROM LocallyChange WHERE type = :type")
    List<LocallyChange> getAll(String type);

    @Delete
    void deleteItem(LocallyChange item);
}
