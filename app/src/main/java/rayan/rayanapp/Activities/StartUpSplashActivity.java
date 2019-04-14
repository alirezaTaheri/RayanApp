package rayan.rayanapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StartUpSplashActivity extends AppCompatActivity {

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up_splash_activity);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.loginSplash)
    void clickOnLoginSplash(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.signUpSplash)
    void clickOnSignUpSplash(){
        Intent intent = new Intent(this, SignUpUserActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        // android.os.Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
