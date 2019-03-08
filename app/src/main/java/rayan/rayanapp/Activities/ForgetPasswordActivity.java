package rayan.rayanapp.Activities;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rayan.rayanapp.Fragments.ForgetPasswordFragment;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgetPasswordActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
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
