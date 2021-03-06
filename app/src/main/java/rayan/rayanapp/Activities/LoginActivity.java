package rayan.rayanapp.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTouch;
//import co.ronash.pushe.Pushe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Helper.DialogPresenter;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.KeyboardUtil;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.LoginViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    LoginViewModel loginViewModel;
    @BindView(R.id.phoneNumberEditText)
    EditText phoneEditText;
    @BindView(R.id.passwordEditText)
    EditText passwordInput;
    @BindView(R.id.forgotPasswordTextView)
    TextView forgotPasswordTextView;
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        new KeyboardUtil(this, findViewById(R.id.nestedScrollView));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)){
            SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"دستگاه به اینترنت متصل نیست");
        }
        ButterKnife.bind(this);

        writeLog().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getLoginResponse().observe(this, baseResponse -> {
            if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)){
                SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"دستگاه به اینترنت متصل نیست");
            }else {
                if (baseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION)) {
                    if (baseResponse.getData().getUser().getRegistered().equals("true")) {
                        RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
                        RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(), passwordInput.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(this, "کاربری شما تایید نشده است", Toast.LENGTH_SHORT).show();
                } else
                    switch (baseResponse.getData().getMessage()) {
                        case AppConstants.SOCKET_TIME_OUT:
                            Toast.makeText(this, "مشکلی در دسترسی وجود دارد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.USER_NOT_FOUND_RESPONSE:
                            Toast.makeText(this, "کاربری با این مشخصات وجود ندارد", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.WRONG_PASSWORD_RESPONSE:
                            Toast.makeText(this, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstants.SUCCESS_DESCRIPTION:

                            break;
                    }
            }  });

        phoneEditText.requestFocus();
        phoneEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(phoneEditText.getText().toString().length()==11)     //size as per your requirement
                {
                    passwordInput.requestFocus();
                }
                return false;
            }
        });


//        loginViewModel.getLoginResponse().observe(this, baseResponse -> {
//            if (baseResponse.getData().getUser().getRegistered().equals("true")) {
//                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
//                RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(), passwordInput.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }else {
//                Log.e("login msg",baseResponse.getData().getMessage());
//            }
//        });
//<<<<<<< HEAD
//>>>>>>> 1603fc81d4a5d3a7cc5890deaf896d735dffe242
//=======

//        loginViewModel.getLoginResponse().observe(this, baseResponse -> {
//            if (baseResponse.getData().getUser().getRegistered().equals("true")) {
//                RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
//                RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(), passwordInput.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public Observable<Object> writeLog(){
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                if (isExternalStorageWritable()) {
                    File appDirectory = new File(Environment.getExternalStorageDirectory() + "/RayanAppFolder");
                    File logDirectory = new File(appDirectory + "/log");
                    File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");
                    Date currentTime = Calendar.getInstance().getTime();
                    Log.e(">>>>>>>>>", ">>>>>>>>>Date<<<<<<<< " + currentTime);
                    File logFile2 = new File(logDirectory, "logcat" + System.currentTimeMillis() + "_2.txt");
                    // create app folder
                    if (!appDirectory.exists()) {
                        appDirectory.mkdir();
                    }
                    // create log folder
                    if (!logDirectory.exists()) {
                        logDirectory.mkdir();
                    }
                    // clear the previous logcat and then write the new one to the file
                    try {
                        Process process = Runtime.getRuntime().exec("logcat -c");
                        process = Runtime.getRuntime().exec("logcat -f " + logFile);
//                process = Runtime.getRuntime().exec("logcat -f " + logFile2 + " *:S EditDeviceFragment:E UDPServerService:E EditGroupFragmentViewModel:E");
                    } catch (IOException error) {
                        error.printStackTrace();
                    }

                } else if (isExternalStorageReadable()) {
                    // only readable
                } else {
                    // not accessible
                }
            }
        });
    }

@OnFocusChange(R.id.phoneNumberEditText)
void onPhoneEditTextFocusChange(){
    phoneEditText.setHint("09123456789");
}
    @OnFocusChange(R.id.passwordEditText)
    void onPasswordEditTextFocusChange(){
        phoneEditText.setHint("");
    }
    @OnClick(R.id.forgotPasswordTextView)
    void clickOnforgetPass(){
      //  ShortcutIcon();
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    @OnClick(R.id.signInButton)
    public void checkForm() {
//        Log.e(TAG, "PUSHE ID: " + Pushe.isPusheInitialized(this));
//        Log.e(TAG, "PUSHE ID: " + Pushe.getPusheId(this));
        Log.e(TAG,"WEEWEWEWWEWELCOM");
        if (loginViewModel.isConnected(this)) {
            String phoneNumber = phoneEditText.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (phoneNumber.length() == 0 | password.length() == 0) {
                SnackBarSetup.snackBarSetup(findViewById(android.R.id.content),"لطفا اطلاعات خود را کامل وارد کنید");
            } else {
                loginViewModel.login(phoneEditText.getText().toString(), passwordInput.getText().toString(), new DialogPresenter(getSupportFragmentManager())).observe(this, baseResponse -> {
                    Log.e("message", baseResponse.getData().getMessage()+" "+ baseResponse.getStatus().getCode());
                    if (baseResponse.getStatus().getCode().equals("200")) {
                        Toast.makeText(this, "با موفقیت وارد شدید", Toast.LENGTH_SHORT).show();
                        RayanApplication.getPref().saveToken(baseResponse.getData().getToken());
                        RayanApplication.getPref().createSession(baseResponse.getData().getUser().getId(), baseResponse.getData().getUser().getUsername(), passwordInput.getText().toString(), baseResponse.getData().getUser().getUserInfo(), baseResponse.getData().getUser().getEmail());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("You must change your password")) {
                        SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"برای ورود باید رمز خود را تغییر دهید");
                    } else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().contains("Please try")) {
                        String msj=baseResponse.getData().getMessage().replaceAll("\\D+","");
                        int min = Integer.parseInt(msj);
                        SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content)," دقیقه دیگر دوباره امتحان کنید"+min+"لطفا ");
                    }else if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")) {
                        SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"کاربری با این شماره وجود ندارد");
                    }else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("Wrong password")) {
                        SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"رمز وارد شده اشتباه است");
                    } else {
                        Log.e(TAG, "edit user problem: " + baseResponse.getStatus().getCode());
                        Log.e("message", baseResponse.getData().getMessage());
                        SnackBarSetup.snackBarSetup(this.findViewById(android.R.id.content),"مشکلی وجود دارد");
                    }
                });
            }} else {
            SnackBarSetup.snackBarSetup(findViewById(android.R.id.content),"لطفا اتصال خود به اینترنت را چک کنید");
        }
    }
}
