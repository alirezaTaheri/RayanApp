package rayan.rayanapp.Activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import rayan.rayanapp.Fragments.ForgetPasswordFragment;
import rayan.rayanapp.R;

public class ForgetPasswordActivity extends BaseActivity {
    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.forgetpass_frameLayout, ForgetPasswordFragment.newInstance());
        transaction.commit();
    }
}
