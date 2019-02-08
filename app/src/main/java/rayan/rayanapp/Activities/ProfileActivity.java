package rayan.rayanapp.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rayan.rayanapp.R;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.EditUserFragment.ClickOnChangePassword;


public class ProfileActivity extends AppCompatActivity implements ClickOnChangePassword {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.fragmentManager = getSupportFragmentManager();
        this.transaction = this.fragmentManager.beginTransaction();
        this.transaction.replace(R.id.profile_frameLayout, EditUserFragment.newInstance("", ""));
        this.transaction.commit();
    }


    @Override
    public void clickOnChangePassword() {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }
}