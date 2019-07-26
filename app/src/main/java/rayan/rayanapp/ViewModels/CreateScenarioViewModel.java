package rayan.rayanapp.Fragments;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Persistance.database.ScenarioDatabase;
import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.ViewModels.ScenariosFragmentViewModel;

public class CreateScenarioViewModel extends ScenariosFragmentViewModel {
    ScenarioDatabase scenarioDatabase;
    public CreateScenarioViewModel(@NonNull Application application) {
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
