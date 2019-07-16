package rayan.rayanapp.Persistance.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;

@Dao
public interface ScenariosDAO extends BaseDAO<Scenario>{

    @Query("SELECT * FROM Scenario ")
    List<Scenario> getAll();

    @Query("SELECT * FROM Scenario WHERE id = :id")
    Scenario getScenario(int id);

    @Update
    void updateScenario(Scenario scenario);

    @Delete
    void deleteScenarios(List<Scenario> scenarios);

    @Delete
    void deleteScenario(Scenario scenario);

}
