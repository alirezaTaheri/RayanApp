package rayan.rayanapp.Listeners;

import com.stepstone.stepper.StepperLayout;

public interface StepperItemSimulation {
    void onSelect();
    void onError();
    void onNextClicked(StepperLayout.OnNextClickedCallback callback);
    void onBackClicked();
}
