package rayan.rayanapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.ChangePasswordFragment;
import rayan.rayanapp.Fragments.CreateGroupFragment;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.EditUserFragment.ClickOnChangePassword;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ProfileActivity extends AppCompatActivity implements ClickOnChangePassword, OnBottomSheetSubmitClicked {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    EditUserFragment editUserFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;
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
        yesNoButtomSheetFragment=new YesNoButtomSheetFragment();
        yesNoButtomSheetFragment.setOnBottomSheetSubmitClicked(this);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
editUserFragment=EditUserFragment.newInstance("", "");
        transaction.replace(R.id.profile_frameLayout, editUserFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
//            startActivity(new Intent(this, MainActivity.class));
        } else {
            FragmentManager fm = this.getSupportFragmentManager();
            fm.popBackStack();
        }

    }

    @Override
    public void submitClicked(String tag) {
        switch (tag) {
            case "EditUserFragment":
                RayanApplication.getPref().removeSession();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
                break;
        }
    }
}