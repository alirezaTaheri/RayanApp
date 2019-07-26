package rayan.rayanapp.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Fragments.CreateScenarioFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.ScenarioActivityViewModel;

public class ScenarioActivity extends AppCompatActivity {

    CreateScenarioFragment createScenarioFragment;
    ScenarioActivityViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ScenarioActivityViewModel.class);
        setContentView(R.layout.activity_create_scenario);
        ButterKnife.bind(this);
        createScenarioFragment = CreateScenarioFragment.newInstance(-1);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameLayout, createScenarioFragment).commit();
    }

    @OnClick(R.id.dismiss)
    public void dismissScenario(){
        onBackPressed();
    }

    @OnClick(R.id.createScenarioButton)
    public void createScenario(){
        Scenario scenario = createScenarioFragment.getScenario();
        if (scenario != null) {
            viewModel.addScenario(scenario);
            onBackPressed();
        }
    }
}
