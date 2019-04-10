package rayan.rayanapp.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.polyak.iconswitch.IconSwitch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.viewPager.BottomNavigationViewPagerAdapter;
import rayan.rayanapp.Adapters.viewPager.MainActivityViewPagerAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Helper.NsdHelper;
import rayan.rayanapp.Listeners.MqttStatus;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.CustomViewPager;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MqttStatus {
    int bottomNavigationHeight;
    @BindView(R.id.accessModeSwitch)
    IconSwitch accessModeSwitch;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    MainActivityViewModel mainActivityViewModel;
    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    MqttStatus mqttStatus;
    @BindView(R.id.status)
    TextView actionBarStatus;
    @BindView(R.id.statusIcon)
    ImageView statusIcon;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RayanApplication.getPref().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
        setContentView(R.layout.activity_main);
//        requestRecordAudioPermission();
        ButterKnife.bind(this);
        mqttStatus = this;
//        setSupportActionBar(toolbar_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
         navigationView.bringToFront();
       navigationView.invalidate();
       // Log.e("setting", RayanApplication.getPref().getThemeKey() + " " + RayanApplication.getPref().getShowNotification());
        navigationView.bringToFront();
        navigationView.invalidate();
        navigationView.setNavigationItemSelectedListener(this);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        MainActivityViewModel.connection.observe(this, connection -> {
            switch (connection.getStatus()) {
                case NONE:
                    break;
                case CONNECTING:
                    this.mqttConnecting();
                    break;
                case CONNECTED:
                    this.mqttConnected();
                    break;
                case DISCONNECTING:
                    break;
                case DISCONNECTED:
                    this.mqttDisconnected();
                    break;
                case ERROR:
                    this.mqttError();
                    break;
            }
        });
        ((RayanApplication) getApplication()).getNetworkStatus().observe(this, networkConnection -> {
            Log.e(TAG, "Network Connection: " + networkConnection);
            Log.e("seekbarthis","in the mainactivity received update for observable: " + networkConnection);
            if (!networkConnection.isConnected()) {
                mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection).observe(this, connection -> {
                    MainActivityViewModel.connection.postValue(connection);
                    switch (connection.getStatus()) {
                        case NONE:
                            break;
                        case CONNECTING:
                            this.mqttConnecting();
                            break;
                        case CONNECTED:
                            this.mqttConnected();
                            break;
                        case DISCONNECTING:
                            break;
                        case DISCONNECTED:
                            this.mqttDisconnected();
                            break;
                        case ERROR:
                            this.mqttError();
                            break;
                    }
                });
                RayanApplication.getPref().saveProtocol(AppConstants.UDP);
//                actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.yellow_acc_4));
                statusIcon.setVisibility(View.INVISIBLE);
                actionBarStatus.setText("عدم اتصال به اینترنت");
            } else
//                if (MainActivityViewModel.connection.getValue() == null || MainActivityViewModel.connection.getValue() != null && !MainActivityViewModel.connection.getValue().isConnected())
            {
                Log.e("///////////////", "////connecting to mqtt");
                mainActivityViewModel.connectToMqtt(MainActivity.this).observe(this, connection -> {
                    MainActivityViewModel.connection.postValue(connection);
                    switch (connection.getStatus()) {
                        case NONE:
                            break;
                        case CONNECTING:
                            this.mqttConnecting();
                            break;
                        case CONNECTED:
                            this.mqttConnected();
                            break;
                        case DISCONNECTING:
                            break;
                        case DISCONNECTED:
                            this.mqttDisconnected();
                            break;
                        case ERROR:
                            this.mqttError();
                            break;
                    }
                });
            }
        });
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MainActivityViewPagerAdapter viewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        // viewPager.setBackgroundColor(Color.GREEN);
//        accessModeSwitch.setCheckedChangeListener(current -> {
//            if (current.equals(IconSwitch.Checked.RIGHT)){
//                Log.e(TAG, "SET To Right");
//                mainActivityViewModel.connectToMqtt(MainActivity.this).observe(this, connection -> {
//                    MainActivityViewModel.connection.postValue(connection);
//                    switch (connection.getStatus()){
//                        case NONE:
//                            break;
//                        case CONNECTING:
//                            this.mqttConnecting();
//                            break;
//                        case CONNECTED:
//                            this.mqttConnected();
//                            break;
//                        case DISCONNECTING:
//                            break;
//                        case DISCONNECTED:
//                            this.mqttDisconnected();
//                            break;
//                        case ERROR:
//                            this.mqttError();
//                            break;
//                    }
//                });
//            }
//            else{
//                Log.e(TAG, "SET To Left");
//                mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection).observe(this, connection -> {
//                    MainActivityViewModel.connection.postValue(connection);
//                    switch (connection.getStatus()){
//                        case NONE:
//                            break;
//                        case CONNECTING:
//                            this.mqttConnecting();
//                            break;
//                        case CONNECTED:
//                            this.mqttConnected();
//                            break;
//                        case DISCONNECTING:
//                            break;
//                        case DISCONNECTED:
//                            this.mqttDisconnected();
//                            break;
//                        case ERROR:
//                            this.mqttError();
//                            break;
//                    }
//                });
//                RayanApplication.getPref().saveProtocol(AppConstants.UDP);
//            }
//        });
        initialize();
        if (isExternalStorageWritable()) {

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/RayanAppFolder");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");
            File logFile2 = new File(logDirectory, "logcat" + System.currentTimeMillis() + "2.txt");

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
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }


        initializeBottomNavigation();
    }

}


    public void initialize(){
        if (RayanApplication.getPref().getProtocol() == null){
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
            accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
        }
            if (RayanApplication.getPref().getProtocol().equals(AppConstants.MQTT)){
                accessModeSwitch.setChecked(IconSwitch.Checked.RIGHT);
            }
            else
                accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RayanApplication.getPref().isLoggedIn()) {
            startService(new Intent(this, UDPServerService.class));
            RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/", ""));
//            mainActivityViewModel.sendNodeToAll();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.groupsActivity:
                startActivity(new Intent(this, GroupsActivity.class));
                break;
            case R.id.devicesManagementActivity:
                startActivity(new Intent(this, DeviceManagementActivity.class));
                break;
            case R.id.profileActivity:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.addNewDeviceActivity:
                startActivity(new Intent(this, AddNewDeviceActivity.class));
                break;
            case R.id.settingsActivity:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.statusIcon)
    public void onRetryMqtt(){
        Log.e("///////////////", "////Retry Connecting To mqtt");
        mainActivityViewModel.connectToMqtt(MainActivity.this).observe(this, connection -> {
            MainActivityViewModel.connection.postValue(connection);
            switch (connection.getStatus()){
                case NONE:
                    break;
                case CONNECTING:
                    this.mqttConnecting();
                    break;
                case CONNECTED:
                    this.mqttConnected();
                    break;
                case DISCONNECTING:
                    break;
                case DISCONNECTED:
                    this.mqttDisconnected();
                    break;
                case ERROR:
                    this.mqttError();
                    break;
            }
        });
    }

    @Override
    public void mqttConnecting() {
//        actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.yellow_acc_4));
        actionBarStatus.setText("درحال اتصال");
        statusIcon.setVisibility(View.INVISIBLE);
//        accessModeSwitch.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "CONNECTING", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: CONNECTING");
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(false);
    }

    @Override
    public void mqttConnected() {
//        actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.orange_acc_4));
        actionBarStatus.setText("رایان");
        statusIcon.setVisibility(View.INVISIBLE);
//        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.RIGHT);
        RayanApplication.getPref().saveProtocol(AppConstants.MQTT);
//        Toast.makeText(this, "CONNECTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: CONNECTED");
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(true);
    }

    @Override
    public void mqttDisconnected() {
//        actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.red_acc_4));
        if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)){
            actionBarStatus.setText("عدم اتصال به اینترنت");
            statusIcon.setVisibility(View.INVISIBLE);
        }
        else{
            actionBarStatus.setText("عدم اتصال");
            statusIcon.setVisibility(View.VISIBLE);

        }
//        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
//        Toast.makeText(this, "DISCONNECTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: DISCONNECTED");
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(false);
    }

    @Override
    public void mqttError() {
//        actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.red_acc_4));
        if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)){
            actionBarStatus.setText("عدم اتصال به اینترنت");
            statusIcon.setVisibility(View.INVISIBLE);
        }
        else{
            actionBarStatus.setText("عدم اتصال");
            statusIcon.setVisibility(View.VISIBLE);

        }
        statusIcon.setVisibility(View.VISIBLE);
//        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
//        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: ERROR");
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(false);
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
    protected static final int RESULT_SPEECH = 1;

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.e(TAG+ "/////////", "Text is: " + text.get(0));

                }
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (RayanApplication.getPref().isLoggedIn()) {
            stopService(new Intent(this, UDPServerService.class));
            mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection);
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        }
            super.onDestroy();
    }

    private void reco(){

//        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
//        sendOrderedBroadcast(
//                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);
//        Intent intent = new Intent(
//                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fa-IR");
//
//        try {
//            startActivityForResult(intent, RESULT_SPEECH);
//        } catch (ActivityNotFoundException a) {
//            Toast t = Toast.makeText(getApplicationContext(),
//                    "Opps! Your device doesn't support Speech to Text",
//                    Toast.LENGTH_SHORT);
//            t.show();
//        }
//        Locale persianLocale = new Locale("fa","IR");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "rayan.rayanapp");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");

        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    Log.e(TAG, "No voice results");
                } else {
                    Toast.makeText(MainActivity.this,voiceResults.get(0), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Printing matches: ");
                    for (String match : voiceResults) {
                        Log.d(TAG, match);
                    }
                }
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "Ready for speech");
            }

            @Override
            public void onError(int error) {
                Log.d(TAG,
                        "Error listening for speech: " + error);
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);

    }

    public void initializeBottomNavigation(){
        viewPager.disableScroll(true);
        setupBottomNavigationViewPager(viewPager);

        bottomNavigationView.getMenu().getItem(RayanApplication.getPref().getBottomNavigationIndexKey()).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_device:
                                RayanApplication.getPref().setBottomNavigationIndexKey(1);
                                viewPager.setCurrentItem(1);

                                break;
                            case R.id.action_senario:
                                RayanApplication.getPref().setBottomNavigationIndexKey(0);
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_favorite:
                                RayanApplication.getPref().setBottomNavigationIndexKey(2);
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

    }
    int actionBarHeight;
    private void setupBottomNavigationViewPager(ViewPager viewPager) {
        BottomNavigationViewPagerAdapter adapter = new BottomNavigationViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
     //   viewPager.beginFakeDrag();
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        lp.bottomMargin += actionBarHeight+15;
        viewPager.setCurrentItem(RayanApplication.getPref().getBottomNavigationIndexKey());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(1).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                RayanApplication.getPref().setBottomNavigationIndexKey(position);
              //  Toast.makeText(MainActivity.this,RayanApplication.getPref().getBottomNavigationIndexKey()+ "", Toast.LENGTH_SHORT).show();
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        nsdHelper = new NsdHelper(this);
        nsdHelper.registerService(AppConstants.HTTP_TO_DEVICE_PORT);
    }

    NsdHelper nsdHelper;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            nsdHelper.discoverServices();
           finish();

        }
    }

    //    @Override
//    public void onBackPressed() {
//        publish(MainActivityViewModel.connection.getValue(), null, "message", 0, false);
//    }

    //    String key = "q7tt0yk18nrjrqur";
//    String textToDecrypt = "xpq/VGgyD0pAf94O1fmSgg==";
//    String secretOfAkbar = "8nro4q0emv8k1uv5";


//    private static SecretKeySpec generateKey(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] bytes = password.getBytes("UTF-8");
//        digest.update(bytes, 0, bytes.length);
//        byte[] key = digest.digest();
//
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        return secretKeySpec;
//    }
    public static SecretKeySpec secretKeySpec;
//    @Override
//    public void onBackPressed() {
////        secretKeySpec = new SecretKeySpec("8nro4q0emv8k1uv5".getBytes(), "AES");
//        secretKeySpec = new SecretKeySpec("1234567812345678".getBytes(), "AES");
//        String plainText = "123";
//        String encryptedString = Encryptor.encrypt(plainText, key);
//        String decryptedString = Encryptor.decrypt("yxEX1vOqupgRIiIE6mi11szCSG6glHizIdaPimHgGJoMk5B5jC/aTuoLF5p7MTJlz/yIH4seE3HatSek9ipis8JNmUzJX7tnKI7E14ur5jZ7y9Xr0TUIv3HQcU0sfMf3", key);
//        Log.e(getClass().getSimpleName(), "Plaing Text: " + plainText);
//        Log.e(getClass().getSimpleName(), "encryptedString: " + encryptedString);
////        Log.e(getClass().getSimpleName(), "decryptedString: " + decryptedString);
////        Log.e(getClass().getSimpleName(), "text to decrypt is: " + Encryptor.decrypt(textToDecrypt, secretOfAkbar));
////        Log.e(getClass().getSimpleName(), "text to decrypt is: " + Encryptor.encrypt(Encryptor.decrypt(textToDecrypt, secretOfAkbar), secretOfAkbar));
////        try {
////            Log.e(getClass().getSimpleName(), "Base64 of node message is: " + new String(Base64.decode(textToDecrypt, Base64.DEFAULT), "UTF-8"));
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
//    }//{"src":"111111","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"fYvqm//fo28GymbrTqhbuA=="}
}//{"src":"14337767","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"fYvqm//fo28GymbrTqhbuA=="}
//{"text":"yxEX1vOqupgRIiIE6mi11szCSG6glHizIdaPimHgGJoMk5B5jC/aTuoLF5p7MTJlz/yIH4seE3HatSek9ipis8JNmUzJX7tnKI7E14ur5jZ7y9Xr0TUIv3HQcU0sfMf3", "cmd":"en", "src":""}
//{"text":"ILaSCiqVbgCucFnGJEuKJ+XqOHxmaaDeeLj2H625xQM=", "cmd":"de", "src":""}
//{"text":"G9DU4Dr/jyMuH6OTchlQebccrrbqa7JrIfGuKZ8RurU=", "cmd":"de", "src":""}
//{"text":"xpq/VGgyD0pAf94O1fmSgg==", "cmd":"de", "src":"","k":"8nro4q0emv8k1uv5"}
//{"text":"WJywYCPo75GyPxMPPSTKFg==", "cmd":"de", "src":""}
//{"text":"PKa/MINB25FvvfbOwFA2sGbC+OPoM+m3AWoTi7OK/l4=", "cmd":"de", "src":""}
//{"text":"50tLQ4W90zvVMENvGd0DZw==\n", "cmd":"de", "src":"", "k":"8nro4q0emv8k1uv5"}
//{"text":"28#", "cmd":"en", "src":"", "k":"q7tt0yk18nrjrqur"}
//{"src":"5958528","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"TJpYO/lEqtn6Yg6L8uBkIQ=="}
