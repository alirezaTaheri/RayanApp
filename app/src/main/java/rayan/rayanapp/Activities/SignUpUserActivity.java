package rayan.rayanapp.Activities;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Dialogs.PrivacyPolicyDialog;
import rayan.rayanapp.Fragments.EditUserFragment;
import rayan.rayanapp.Fragments.RegisterUserFragment;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpUserActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.signup_frameLayout, RegisterUserFragment.newInstance("", ""));
        transaction.commit();
        if (!RayanApplication.getPref().isPrivacyPolicyAccepted()) {
            PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(this, "این متن سیاست است");
            privacyPolicyDialog.show();
        }
    }
}
