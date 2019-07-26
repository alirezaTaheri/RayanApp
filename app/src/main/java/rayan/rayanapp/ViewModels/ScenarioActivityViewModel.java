package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.support.annotation.NonNull;

import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Persistance.database.ScenarioDatabase;

public class ScenarioActivityViewModel extends ScenariosFragmentViewModel {
    ScenarioDatabase scenarioDatabase;
    public ScenarioActivityViewModel(@NonNull Application application) {
        super(application);
        scenarioDatabase = new ScenarioDatabase(application);
    }

    public void updateScenario(Scenario scenario){
        scenarioDatabase.updateScenario(scenario);
    }

    public void addScenario(Scenario scenario){
        scenarioDatabase.addScenario(scenario);
    }

    public void deleteScenario(Scenario scenario){
        scenarioDatabase.deleteScenario(scenario);
    }

}
