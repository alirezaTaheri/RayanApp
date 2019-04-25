package rayan.rayanapp.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.graphics.Color;
import android.graphics.Color;
import android.net.wifi.WifiManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polyak.iconswitch.IconSwitch;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.recyclerView.SortByGroupRecyclerViewAdapter;
import rayan.rayanapp.Adapters.viewPager.BottomNavigationViewPagerAdapter;
import rayan.rayanapp.Adapters.viewPager.MainActivityViewPagerAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Listeners.MqttStatus;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.CustomViewPager;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MqttStatus, View.OnClickListener, OnGroupClicked<Group> {
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
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;
    MqttStatus mqttStatus;
    @BindView(R.id.status)
    TextView actionBarStatus;
    @BindView(R.id.statusIcon)
    ImageView retryIcon;
    @BindView(R.id.drawerrrrr)
    View drawerrrrr;
    ///////////////////////////////////////////////////////
    @BindView(R.id.expand_arrow_icon)
    ImageView expand_arrow_icon;
    @BindView(R.id.expandable_layout)
    ExpandableLayout expandableLayout;
    @BindView(R.id.groupsActivity)
    LinearLayout drawer_groupsActivity;
    @BindView(R.id.addNewDeviceActivity)
    LinearLayout drawer_addNewDeviceActivity;
    @BindView(R.id.devicesManagementActivity)
    LinearLayout drawer_deviceManagementActivity;
    @BindView(R.id.settingsActivity)
    LinearLayout drawer_settings;
    @BindView(R.id.sortByGroup)
    RelativeLayout drawer_sortByGroup;
    @BindView(R.id.profileActivity)
    LinearLayout drawer_profile;
    @BindView(R.id.parent)
    LinearLayout drawer_parent;
    @BindView(R.id.groupsRecyclerView)
    RecyclerView drawer_groupsRecyclerView;
    @BindView(R.id.drawer_userImage)
    ImageView drawer_userImage;
    @BindView(R.id.drawer_userName)
    TextView drawer_userName;
    SortByGroupRecyclerViewAdapter drawer_groupsRecyclerViewAdapter;
    int connectionRetries;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RayanApplication.getPref().isLoggedIn()) {
            Intent intent = new Intent(this, StartUpSplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
        setContentView(R.layout.activity_main);
//        requestRecordAudioPermission();
        ButterKnife.bind(this);
        mqttStatus = this;
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
                if (networkConnection.getType() == AppConstants.VPN_NETWORK) {
                    Log.e(MainActivity.this.getClass().getSimpleName(), "Vpn is Disconnected so toggling wifi");
                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);
                    wifi.setWifiEnabled(true);
                }
                RayanApplication.getPref().saveProtocol(AppConstants.UDP);
//                actionBarStatus.setTextColor(ContextCompat.getColor(this,R.color.yellow_acc_4));
                retryIcon.setVisibility(View.INVISIBLE);
                actionBarStatus.setText("عدم اتصال به اینترنت");
            } else
                for (int a = 0;a<getSupportFragmentManager().getFragments().size();a++)
                    if (getSupportFragmentManager().getFragments().get(a) instanceof DevicesFragment){
                        ((DevicesFragment)getSupportFragmentManager().getFragments().get(a)).devicesFragmentViewModel.getGroups();
                    }
                if (MainActivityViewModel.connection.getValue() == null || MainActivityViewModel.connection.getValue().getClient() != null && !Objects.requireNonNull(MainActivityViewModel.connection.getValue()).isConnected() && !MainActivityViewModel.connection.getValue().getStatus().equals(Connection.ConnectionStatus.CONNECTING)) {
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
        initialize();
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
    if (RayanApplication.getPref().isLoggedIn()) {
        int width = (getResources().getDisplayMetrics().widthPixels*6)/8;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) drawerrrrr.getLayoutParams();
        params.width = width;
        drawerrrrr.setLayoutParams(params);
        drawerLayout.setScrimColor(Color.parseColor("#33000000"));

        if (RayanApplication.getPref().getGenderKey().equals("Male")) {
            drawer_userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_man));
        } else if (RayanApplication.getPref().getGenderKey().equals("Female")) {
            drawer_userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_woman));
        } else {
            drawer_userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_man));
        }
        if (RayanApplication.getPref().getNameKey().isEmpty() && RayanApplication.getPref().getNameKey() == null) {
            drawer_userName.setText("Rayan App");
        } else {
            drawer_userName.setText(RayanApplication.getPref().getNameKey());
        }
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
        drawer_addNewDeviceActivity.setOnClickListener(this);
        drawer_settings.setOnClickListener(this);
        drawer_profile.setOnClickListener(this);
        drawer_groupsActivity.setOnClickListener(this);
        drawer_sortByGroup.setOnClickListener(this);
        drawer_deviceManagementActivity.setOnClickListener(this);
        drawer_parent.setOnClickListener(this);
        //app version
//        try {
//            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            version.setText(pInfo.versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RayanApplication.getPref().isLoggedIn()) {
            startService(new Intent(this, UDPServerService.class));
            RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/", ""));
            mainActivityViewModel.sendNodeToAll();
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
                startActivity(new Intent(this, SettingssActivity.class));
                break;
        }
//        drawerLayout.closeDrawer(GravityCompat.START);
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
        actionBarStatus.setText("درحال اتصال");
        retryIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Mqtt Status: CONNECTING Connection Retries:" + connectionRetries);
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(false);
    }

    @Override
    public void mqttConnected() {
        connectionRetries = 0;
        actionBarStatus.setText("رایان");
        retryIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        RayanApplication.getPref().saveProtocol(AppConstants.MQTT);
        Log.e(TAG, "Mqtt Status: ERROR Connection Retries: " + connectionRetries);
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(true);
    }

    @Override
    public void mqttDisconnected() {
        Log.e(TAG, "Mqtt Status: DISCONNECTED Connection Retries: " + connectionRetries);
        if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)) {
            connectionRetries = 0;
            actionBarStatus.setText("عدم اتصال به اینترنت");
            retryIcon.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        else {
            if (connectionRetries > 8) {
                actionBarStatus.setText("عدم اتصال");
                retryIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {onRetryMqtt(); //connectionRetries++;
                 }
        }
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
            ((RayanApplication) getApplication()).getMtd().updateMqttStatus(false);
    }

    @Override
    public void mqttError() {
        Log.e(TAG, "Mqtt Status: ERROR Connection Retries: " + connectionRetries);
        if (NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)) {
            connectionRetries = 0;
            actionBarStatus.setText("عدم اتصال به اینترنت");
            retryIcon.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }else {
            if (connectionRetries > 8) {
                actionBarStatus.setText("عدم اتصال");
                retryIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            else {onRetryMqtt(); connectionRetries++;}
        }
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
            ((RayanApplication) getApplication()).getMtd().updateMqttStatus(false);
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

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //
//    @Override
//    public void onBackPressed() {
//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//// The first in the list of RunningTasks is always the foreground task.
//        List<ActivityManager.RunningTaskInfo> f = am.getRunningTasks(1);
//        Log.e("//////////////","Foreground task info: " + f);
//        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
//        Log.e("//////////////","Foreground task info: " + foregroundTaskInfo);
//        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
//        Log.e("//////////////","foregroundTaskPackageName: " + foregroundTaskPackageName);
//        PackageManager pm = this.getPackageManager();
//        PackageInfo foregroundAppPackageInfo = null;
//        try {
//            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.e("//////////////","foregroundAppPackageInfo:  " + foregroundAppPackageInfo);
//        String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
//        Log.e("//////////////","foregroundTaskAppName: " + foregroundTaskAppName);
//    }

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
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        lp.bottomMargin += actionBarHeight + 15;
        viewPager.setCurrentItem(RayanApplication.getPref().getBottomNavigationIndexKey());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(1).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                RayanApplication.getPref().setBottomNavigationIndexKey(position);
                //  Toast.makeText(MainActivity.this,RayanApplication.getPref().getBottomNavigationIndexKey()+ "", Toast.LENGTH_SHORT).show();
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
//        nsdHelper = new NsdHelper(this);
//        nsdHelper.registerService(AppConstants.HTTP_TO_DEVICE_PORT);
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

        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.groupsActivity:
                    startActivity(new Intent(this, GroupsActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.devicesManagementActivity:
                    startActivity(new Intent(this, DeviceManagementActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.profileActivity:
                    startActivity(new Intent(this, ProfileActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.addNewDeviceActivity:
                    startActivity(new Intent(this, AddNewDeviceActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.settingsActivity:
                    startActivity(new Intent(this, SettingssActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.sortByGroup:
                    expandableLayout.toggle();
                    if(expandableLayout.isExpanded()){
                        expand_arrow_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                    }else {expand_arrow_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down)); }
                    if (drawer_groupsRecyclerViewAdapter == null) {
                        drawer_groupsRecyclerViewAdapter = new SortByGroupRecyclerViewAdapter(this, new ArrayList<>());
                        drawer_groupsRecyclerViewAdapter.setItems(mainActivityViewModel.getAllGroups());
                        Group g = new Group();
                        g.setDevices(mainActivityViewModel.getAllDevices());
                        g.setName("همه");
                        drawer_groupsRecyclerViewAdapter.addItemToFirst(g);
                        drawer_groupsRecyclerView.setAdapter(drawer_groupsRecyclerViewAdapter);
                        drawer_groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        drawer_groupsRecyclerViewAdapter.setListener(this);
                    }
                    break;
                case R.id.parent:
                    break;
            }
        }

        @Override
        public void onGroupClicked (Group item){
            drawerLayout.closeDrawer(GravityCompat.START);
            for (int a = 0;a<getSupportFragmentManager().getFragments().size();a++)
                if (getSupportFragmentManager().getFragments().get(a) instanceof DevicesFragment)
                    ((DevicesFragment) getSupportFragmentManager().getFragments().get(a)).sortDevicesByGroup(item.getId());
            drawer_groupsRecyclerViewAdapter.notifyDataSetChanged();
        }

}

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
//    }//{"src":"111111","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"yyz+4yK0mByoiYrkw1D3pA=="}
//}//{"src":"14337767","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"fYvqm//fo28GymbrTqhbuA=="}
//{"text":"yxEX1vOqupgRIiIE6mi11szCSG6glHizIdaPimHgGJoMk5B5jC/aTuoLF5p7MTJlz/yIH4seE3HatSek9ipis8JNmUzJX7tnKI7E14ur5jZ7y9Xr0TUIv3HQcU0sfMf3", "cmd":"en", "src":""}
//{"text":"ILaSCiqVbgCucFnGJEuKJ+XqOHxmaaDeeLj2H625xQM=", "cmd":"de", "src":""}
//{"text":"G9DU4Dr/jyMuH6OTchlQebccrrbqa7JrIfGuKZ8RurU=", "cmd":"de", "src":""}
//{"text":"xpq/VGgyD0pAf94O1fmSgg==", "cmd":"de", "src":"","k":"8nro4q0emv8k1uv5"}
//{"text":"ho813pvToMH+YszQT9RVdg==", "cmd":"de", "src":"", "k":"q916zpzn15rcimcv"}
//{"text":"PKa/MINB25FvvfbOwFA2sGbC+OPoM+m3AWoTi7OK/l4=", "cmd":"de", "src":""}
//{"text":"50tLQ4W90zvVMENvGd0DZw==\n", "cmd":"de", "src":"", "k":"8nro4q0emv8k1uv5"}
//{"text":"28#", "cmd":"en", "src":"", "k":"0ewnhmtj57j936gl"}
//{"src":"000000","cmd":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"DoUWn/Dlm0iz+nNAAlCoEA=="}
//{"text":"ho813pvToMH+YszQT9RVdg==", "cmd":"de", "src":"", "k":"q916zpzn15rcimcv"}

