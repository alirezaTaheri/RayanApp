package rayan.rayanapp.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.polyak.iconswitch.IconSwitch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Adapters.viewPager.MainActivityViewPagerAdapter;
import rayan.rayanapp.Adapters.viewPager.BottomNavigationViewPagerAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Fragments.FavoritesFragment;
import rayan.rayanapp.Fragments.ScenariosFragment;
import rayan.rayanapp.Listeners.MqttStatus;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MqttStatus {
    int bottomNavigationHeight;
    @BindView(R.id.accessModeSwitch)
    IconSwitch accessModeSwitch;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    MainActivityViewModel mainActivityViewModel;
    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.bottom_navigation_viewpager)
    ViewPager bottom_navigation_viewpager;
    MenuItem prevMenuItem;
    MqttStatus mqttStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRecordAudioPermission();
        ButterKnife.bind(this);
        mqttStatus = this;
//        setSupportActionBar(toolbar_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Log.e("setting",RayanApplication.getPref().getThemeKey()+ " "+ RayanApplication.getPref().getShowNotification());
        navigationView.bringToFront();
       navigationView.invalidate();
        navigationView.setNavigationItemSelectedListener(this);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        MainActivityViewModel.connection.observe(this, connection -> {
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
        ((RayanApplication)getApplication()).getNetworkStatus().observe(this, networkConnection -> {
            Log.e(TAG, "Network Connection: " + networkConnection);
            if (!networkConnection.isConnected()){
                mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection).observe(this, connection -> {
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
                RayanApplication.getPref().saveProtocol(AppConstants.UDP);
            }
        });
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MainActivityViewPagerAdapter viewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
       // viewPager.setBackgroundColor(Color.GREEN);
        accessModeSwitch.setCheckedChangeListener(current -> {
            if (current.equals(IconSwitch.Checked.RIGHT)){
                Log.e(TAG, "SET To Right");
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
            else{
                Log.e(TAG, "SET To Left");
                mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection).observe(this, connection -> {
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
                RayanApplication.getPref().saveProtocol(AppConstants.UDP);
            }
        });
        initialize();
        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/RayanAppFolder" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );
            File logFile2 = new File( logDirectory, "logcat" + System.currentTimeMillis() + "2.txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
//                process = Runtime.getRuntime().exec("logcat -f " + logFile2 + " *:S EditDeviceFragment:E UDPServerService:E EditGroupFragmentViewModel:E");
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }


        initializeBottomNavigation();
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
        startService(new Intent(this, UDPServerService.class));
        RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/",""));
        mainActivityViewModel.sendNodeToAll();
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

    @Override
    public void mqttConnecting() {
        accessModeSwitch.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "CONNECTING", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: CONNECTING");
    }

    @Override
    public void mqttConnected() {
        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.RIGHT);
        RayanApplication.getPref().saveProtocol(AppConstants.MQTT);
        Toast.makeText(this, "CONNECTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: CONNECTED");
    }

    @Override
    public void mqttDisconnected() {
        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        Toast.makeText(this, "DISCONNECTED", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: DISCONNECTED");
    }

    @Override
    public void mqttError() {
        accessModeSwitch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        accessModeSwitch.setChecked(IconSwitch.Checked.LEFT);
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Mqtt Status: ERROR");
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
    public void onBackPressed() {
        reco();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UDPServerService.class));
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
        setupBottomNavigationViewPager(viewPager);

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_device:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_senario:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_favorite:
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
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        lp.bottomMargin += actionBarHeight+15;
        viewPager.setCurrentItem(1);

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
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }
}