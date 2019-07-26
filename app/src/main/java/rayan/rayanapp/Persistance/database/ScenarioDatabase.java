package rayan.rayanapp.Persistance.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Persistance.AppDatabase;
import rayan.rayanapp.Persistance.database.dao.DevicesDAO;
import rayan.rayanapp.Persistance.database.dao.ScenariosDAO;

public class ScenarioDatabase {
    private AppDatabase appDatabase;
    private ScenariosDAO scenariosDAO;
    ExecutorService executorService;
    public ScenarioDatabase(Context context){
        appDatabase = AppDatabase.getInstance(context);
        scenariosDAO = appDatabase.getScenarioDAO();
        executorService= Executors.newSingleThreadExecutor();
    }

    public List<Scenario> getScenarios(){
        return scenariosDAO.getAll();
    }
    public LiveData<List<Scenario>> getScenariosLive(){
        return scenariosDAO.getAllLive();
    }

    public Scenario getScenario(int id){
        return scenariosDAO.getScenario(id);
    }

    public void addScenario(Scenario scenario){
        scenariosDAO.add(scenario);
    }

    public void updateScenario(Scenario scenario){
        scenariosDAO.updateScenario(scenario);
    }

    public void deleteScenario(Scenario scenario){
        scenariosDAO.deleteScenario(scenario);
    }

    public void deleteScenarioWithId(int id){
        scenariosDAO.deleteScenarioWithId(id);
    }
}
