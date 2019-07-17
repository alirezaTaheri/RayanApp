package rayan.rayanapp.Fragments;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import rayan.rayanapp.ViewModels.DevicesFragmentViewModel;
import rayan.rayanapp.ViewModels.ScenariosFragmentViewModel;

public class CreateScenarioViewModel extends ScenariosFragmentViewModel {
    public CreateScenarioViewModel(@NonNull Application application) {
        super(application);
    }

}
