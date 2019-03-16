package rayan.rayanapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Fragments.ChangePasswordFragment;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.EditUserFragment.ClickOnChangePassword;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProfileActivity extends AppCompatActivity implements ClickOnChangePassword {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
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
        transaction.addToBackStack("changePasswordFragment");
        transaction.replace(R.id.profile_frameLayout, changePasswordFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            FragmentManager fm = this.getSupportFragmentManager();
            fm.popBackStack();
        }

    }
}