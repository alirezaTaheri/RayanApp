package rayan.rayanapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rayan.rayanapp.Fragments.ChangePasswordFragment;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.EditUserFragment.ClickOnChangePassword;


public class ProfileActivity extends AppCompatActivity implements ClickOnChangePassword {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.profile_frameLayout, EditUserFragment.newInstance("", ""));
        transaction.commit();
    }


    @Override
    public void clickOnChangePassword() {
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left,R.anim.animation_transition_enter_from_left, R.anim.animation_transition_ext_to_left);
        ChangePasswordFragment changePasswordFragment = ChangePasswordFragment.newInstance();
        transaction.replace(R.id.profile_frameLayout, changePasswordFragment);
        transaction.commit();
    }
    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            getFragmentManager().popBackStack();
        }

    }
}