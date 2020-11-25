package rayan.rayanapp.Persistance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import rayan.rayanapp.Data.LocallyChange;

@Dao
public interface LocallyChangesDAO extends BaseDAO<LocallyChange>{

    @Query("SELECT * FROM LocallyChange ")
    List<LocallyChange> getAll();

    @Query("SELECT * FROM LocallyChange WHERE type = :type")
    List<LocallyChange> getAll(String type);

    @Delete
    void deleteItem(LocallyChange item);

    @Query("DELETE FROM LocallyChange")
    void deleteAll();
}
