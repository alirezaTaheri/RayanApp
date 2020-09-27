package rayan.rayanapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
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
