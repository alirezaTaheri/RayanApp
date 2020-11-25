package rayan.rayanapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.ChangePasswordFragment;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.EditUserFragment.ClickOnChangePassword;
import rayan.rayanapp.Fragments.YesNoButtomSheetFragment;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;


public class ProfileActivity extends BaseActivity implements ClickOnChangePassword, OnBottomSheetSubmitClicked {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    EditUserFragment editUserFragment;
    YesNoButtomSheetFragment yesNoButtomSheetFragment;
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
                Intent intent = new Intent(this,StartUpSplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
                break;
        }
    }
}