package rayan.rayanapp.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    LoginViewModel loginViewModel;
    @BindView(R.id.phoneNumberEditText)
    EditText phoneEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getLoginResponse().observe(this, baseResponse -> {
            if (baseResponse.getData().getUser().getRegistered().equals("true")) {
                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
                RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(), passwordInput.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });
    }
    @OnClick(R.id.signUpTextView)
    void clickOnSignUp(){
        startActivity(new Intent(this, SignUpUserActivity.class));
    }
    @OnClick(R.id.forgotPasswordTextView)
    void clickOnforgetPass(){
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    @OnClick(R.id.signInButton)
    public void checkForm() {
        if (loginViewModel.isConnected(this)) {
            String phoneNumber = phoneEditText.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (phoneNumber.length() == 0 | password.length() == 0) {
                loginViewModel.snackBarSetup(findViewById(android.R.id.content),"لطفا اطلاعات خود را کامل وارد کنید");
            } else
                loginViewModel.login(phoneEditText.getText().toString(), passwordInput.getText().toString());
        } else {
            Toast.makeText(this, "لطفا اتصال خود به اینترنت را چک کنید", Toast.LENGTH_SHORT).show();
        }
    }

}
