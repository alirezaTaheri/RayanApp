package rayan.rayanapp.Activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Dialogs.PrivacyPolicyDialog;
import rayan.rayanapp.Fragments.RegisterUserFragment;
import rayan.rayanapp.R;

public class SignUpUserActivity extends BaseActivity {
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
        if (!RayanApplication.getPref().isPrivacyPolicyAccepted()) {
            PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(this, "این متن سیاست است");
            privacyPolicyDialog.show();
        }
    }
}
