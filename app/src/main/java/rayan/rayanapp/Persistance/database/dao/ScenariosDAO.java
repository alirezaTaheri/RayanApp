package rayan.rayanapp.Persistance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import rayan.rayanapp.Data.Scenario;

@Dao
public interface ScenariosDAO extends BaseDAO<Scenario>{

    @Query("SELECT * FROM Scenario ")
    List<Scenario> getAll();

    @Query("SELECT * FROM Scenario ")
    LiveData<List<Scenario>> getAllLive();

    @Query("SELECT * FROM Scenario WHERE id = :id")
    Scenario getScenario(int id);

    @Update
    void updateScenario(Scenario scenario);

    @Delete
    void deleteScenarios(List<Scenario> scenarios);

    @Delete
    void deleteScenario(Scenario scenario);

    @Query("DELETE FROM Scenario WHERE id = :id")
    void deleteScenarioWithId(int id);

}
