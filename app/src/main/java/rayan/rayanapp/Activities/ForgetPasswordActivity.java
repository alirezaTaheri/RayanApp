package rayan.rayanapp.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.Fragments.ForgetPasswordFragment;
import rayan.rayanapp.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.forgetpass_frameLayout, ForgetPasswordFragment.newInstance("", ""));
        transaction.commit();
    }
}
