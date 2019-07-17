package rayan.rayanapp.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.Fragments.CreateScenarioFragment;
import rayan.rayanapp.R;

public class CreateScenarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_scenario);
        CreateScenarioFragment createScenarioFragment = CreateScenarioFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameLayout, createScenarioFragment).commit();
    }
}
