package rayan.rayanapp.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.RegisterUserFragment;
import rayan.rayanapp.R;

public class SignUpUserActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.signup_frameLayout, RegisterUserFragment.newInstance("", ""));
        transaction.commit();
    }
}
