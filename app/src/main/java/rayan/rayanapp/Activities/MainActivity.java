package rayan.rayanapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import rayan.rayanapp.Adapters.recyclerView.SortByGroupRecyclerViewAdapter;
import rayan.rayanapp.Adapters.viewPager.BottomNavigationViewPagerAdapter;
import rayan.rayanapp.Adapters.viewPager.MainActivityViewPagerAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Fragments.DevicesFragment;
import rayan.rayanapp.Fragments.FavoritesFragment;
import rayan.rayanapp.Helper.ControlRequests;
import rayan.rayanapp.Helper.MessageTransmissionDecider;
import rayan.rayanapp.Helper.RetryConnectMqtt;
import rayan.rayanapp.Listeners.DevicesAndFavoritesListener;
import rayan.rayanapp.Listeners.MqttStatus;
import rayan.rayanapp.Listeners.NetworkConnectivityListener;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.Mqtt.MqttClient;
import rayan.rayanapp.Mqtt.MqttClientService;
import rayan.rayanapp.Persistance.database.RemoteHubDatabase;
import rayan.rayanapp.R;
import rayan.rayanapp.Receivers.ConnectionLiveData;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.CustomViewPager;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.Util.api.StartupApiRequests;
import rayan.rayanapp.Util.diffUtil.DevicesDiffCallBack;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MqttStatus, View.OnClickListener, OnGroupClicked<Group>, NetworkConnectivityListener, DevicesAndFavoritesListener {
    private static final int REQUEST_PHONE_CALL = 1;
    @BindView(R.id.connectionAnimation)
    LottieAnimationView connectionAnimation;
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
    @BindView(R.id.add_new_expand_arrow_icon)
    ImageView add_new_expand_icon;
    @BindView(R.id.expandable_layout)
    ExpandableLayout expandableLayout;
    @BindView(R.id.addNewExpandable)
    ExpandableLayout addNewExpandable;
    @BindView(R.id.groupsActivity)
    LinearLayout drawer_groupsActivity;
    @BindView(R.id.addNew)
    RelativeLayout drawer_addNew;
    @BindView(R.id.addNewDeviceActivity)
    LinearLayout drawer_addNewDeviceActivity;
    @BindView(R.id.addNewRemoteActivity)
    LinearLayout drawer_addNewRemoteActivity;
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
    @BindView(R.id.supportActivity)
    LinearLayout drawer_support;
    MainActivityViewPagerAdapter viewPagerAdapter;
    ControlRequests cr;
    SortByGroupRecyclerViewAdapter drawer_groupsRecyclerViewAdapter;
    int connectionRetries;
    Connection.ConnectionStatus mqttConnectionStatus;
    private RetryConnectMqtt retryConnectMqtt;
    boolean networkConnected = false;
    Animation fadeIn, fadeOut;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "OnCreate MainActivity");
        if (!RayanApplication.getPref().isLoggedIn()) {
            Intent intent = new Intent(this, StartUpSplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
            observeDevicesForPlaySoundEffect();
            MqttClientService.removeClient();
            mqttClient = MqttClientService.getMqttClientInstance(getApplication());
            Log.d(TAG, "onCreate: ViewModel is: " + mainActivityViewModel);
            String currentShowingGroup = RayanApplication.getPref().getCurrentShowingGroup();
            if (currentShowingGroup == null){
                connectionAnimation.setSpeed(1.8f);
                actionBarStatus.setText("رایان");
            }
            else {
                Log.e(TAG, "onCreate: getGroupsFirstOfMainActivity viewModel is: " + mainActivityViewModel);
                Group currentGroup = mainActivityViewModel.getGroup(currentShowingGroup);
                if (currentGroup != null)
                    actionBarStatus.setText(currentGroup.getName());
            }
            initializeAnimations();
            mqttConnectionStatus = Connection.ConnectionStatus.NONE;
            retryConnectMqtt = new RetryConnectMqtt(this);
            ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext(),((RayanApplication) (getApplication())).getNetworkBus());
            connectionLiveData.observe(this, connectionStatusModel -> {
                Log.e("MainActivity", "connectionLiveData:" + connectionStatusModel);
                if (connectionStatusModel == null){
                 this.notConnected();
                }else {
//                    if (AddNewDeviceActivity.getNewDevice() != null && AddNewDeviceActivity.getNewDevice().isFailed() && AddNewDeviceActivity.getNewDevice().getPreGroupId() != null){
//                    Log.e("MainActivity", "In The MainActivity: Going to addDeviceToPreviousGroup");
//                    Observable.zip(mainActivityViewModel.deleteUserObservable(new DeleteUserRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getGroup().getId())),
//                            mainActivityViewModel.addDeviceToGroupObservable(new AddDeviceToGroupRequest(AddNewDeviceActivity.getNewDevice().getId(), AddNewDeviceActivity.getNewDevice().getPreGroupId())),
//                            new BiFunction<BaseResponse, BaseResponse, Object>() {
//                                @Override
//                                public Object apply(BaseResponse baseResponse, BaseResponse baseResponse2) throws Exception {
//                                    Log.e("MainActivity", "Results of tofmal: " + baseResponse);
//                                    Log.e("MainActivity", "Results of tofmal: " + baseResponse2);
//                                    if (baseResponse.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION) && baseResponse2.getStatus().getDescription().equals(AppConstants.SUCCESS_DESCRIPTION)){
//                                        Log.e("MainActivity", "Both Done ");
//                                        Toast.makeText(MainActivity.this, "Fixed", Toast.LENGTH_SHORT).show();
//                                        AddNewDeviceActivity.getNewDevice().setFailed(false);
//                                    }
//                                    return new Object();
//                                }
//                            }).subscribe(new Consumer<Object>() {
//                        @Override
//                        public void accept(Object o) throws Exception {
//
//                        }
//                    });
//                    }
                    if (connectionStatusModel.getType() == AppConstants.WIFI_NETWORK) {
                        String extraInfo = connectionStatusModel.getSsid();
                        if (extraInfo != null && connectionStatusModel.getSsid().charAt(connectionStatusModel.getSsid().length()-1) == connectionStatusModel.getSsid().charAt(0) && String.valueOf(connectionStatusModel.getSsid().charAt(0)).equals("\"")) {
                            extraInfo = connectionStatusModel.getSsid().substring(1, extraInfo.length() - 1);
                            this.wifiNetwork(connectionStatusModel.getState().equals(NetworkInfo.State.CONNECTED), extraInfo);
                        }else if (extraInfo != null && !String.valueOf(connectionStatusModel.getSsid().charAt(connectionStatusModel.getSsid().length()-1)).equals("\"") && !String.valueOf(connectionStatusModel.getSsid().charAt(0)).equals("\"")){
                            this.wifiNetwork(connectionStatusModel.getState().equals(NetworkInfo.State.CONNECTED), extraInfo);
                        }
                    } else if (connectionStatusModel.getType() == AppConstants.MOBILE_DATA) {
                        this.mobileNetwork(connectionStatusModel.getState().equals(NetworkInfo.State.CONNECTED));
                    }
                }
            });
//        requestRecordAudioPermission();
//            ValueAnimator v = ValueAnimator.ofFloat(0f,0.6f);
//            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    connectionAnimation.setProgress((float)animation.getAnimatedValue());
//                    Log.e("animanimanim", animation.getAnimatedValue()+"animation: " +connectionAnimation.getProgress());
//                }
//            });
//            v.setDuration(1000);
//            v.setRepeatMode(ValueAnimator.RESTART);
//            v.setRepeatCount(ValueAnimator.INFINITE);
//            v.start();
            mqttStatus = this;
            cr = new ControlRequests(this);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.syncState();
//            MainActivityViewModel.connection.observe(this, connection -> {
//                Log.e("thsithisklfj", "thisdlajt: " + connection + connection.getStatus());
//                switch (connection.getStatus()) {
//                    case NONE:
//                        break;
//                    case CONNECTING:
//                        this.mqttConnecting();
//                        break;
//                    case CONNECTED:
//                        this.mqttConnected();
//                        break;
//                    case DISCONNECTING:
//                        break;
//                    case DISCONNECTED:
//                        this.mqttDisconnected();
//                        break;
//                    case ERROR:
//                        this.mqttError();
//                        break;
//                }
//            });
            mqttClient.listenToConnectionChanges().subscribe(new Consumer<Connection>() {
                @Override
                public void accept(Connection connection) throws Exception {
                    switch (connection.getStatus()) {
                    case NONE:
                        break;
                    case CONNECTING:
                        MainActivity.this.mqttConnecting();
                        break;
                    case CONNECTED:
                        MainActivity.this.mqttConnected();
                        break;
                    case DISCONNECTING:
                        break;
                    case DISCONNECTED:
                        MainActivity.this.mqttDisconnected();
                        break;
                    case ERROR:
                        MainActivity.this.mqttError();
                        break;
                }
                }
            });
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            viewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(1);
            initialize();
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
            initializeBottomNavigation();
            int width = (getResources().getDisplayMetrics().widthPixels * 7) / 9;
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerrrrr.getLayoutParams();
            params.width = width;
            drawerrrrr.setLayoutParams(params);
            drawerLayout.setScrimColor(Color.parseColor("#99000000"));

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

    @OnClick(R.id.drawerMenuIcon)
    public void clickOnDrawerIcon(){
        drawerLayout.openDrawer(Gravity.RIGHT);
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

    public void initialize(){
        if (RayanApplication.getPref().getProtocol() == null){
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        }
        drawer_addNew.setOnClickListener(this);
        drawer_settings.setOnClickListener(this);
        drawer_profile.setOnClickListener(this);
        drawer_groupsActivity.setOnClickListener(this);
        drawer_sortByGroup.setOnClickListener(this);
        drawer_deviceManagementActivity.setOnClickListener(this);
        drawer_parent.setOnClickListener(this);
        drawer_support.setOnClickListener(this);
        //app version
//        try {
//            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            version.setText(pInfo.versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }


    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "OnResume");
//        ReactiveNetwork
//                .observeNetworkConnectivity(this)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(connectivity -> {
//                    Log.e("NetworkDetector", "From context: " + this + " connectivityIs: " + connectivity);
//                    switch (connectivity.type()){
//                        case AppConstants.WIFI_NETWORK:
//                            String extraInfo = connectivity.extraInfo();
//                            if (extraInfo != null && connectivity.extraInfo().charAt(connectivity.extraInfo().length()-1) == connectivity.extraInfo().charAt(0) && String.valueOf(connectivity.extraInfo().charAt(0)).equals("\""))
//                                extraInfo = connectivity.extraInfo().substring(1,extraInfo.length()-1);
//                            this.wifiNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED), extraInfo);
//                            break;
//                        case AppConstants.MOBILE_DATA:
//                            this.mobileNetwork(connectivity.state().equals(NetworkInfo.State.CONNECTED));
//                            break;
//                        case AppConstants.NOT_CONNECTED_NETWORK:
//                            this.notConnected();
//                            break;
//                    }
//                });
        if (RayanApplication.getPref().isLoggedIn()) {
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
            startService(new Intent(this, UDPServerService.class));
            RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/", ""));
            mainActivityViewModel.sendNodeToAll();
//            mainActivityViewModel.getGroups1().observe(this, new android.arch.lifecycle.Observer<StartupApiRequests.requestStatus>() {
//                @Override
//                public void onChanged(@Nullable StartupApiRequests.requestStatus requestStatus) {
//                    Log.e("3$$$$$", "^^^^^^^^^^^^^^^^^^^" + requestStatus);
//                }
//            });
        }
        ((RayanApplication)getApplication()).getMsc().init();
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

    @Override
    public void mqttConnecting() {
        mqttConnectionStatus = Connection.ConnectionStatus.CONNECTING;
        Log.d(TAG, "mqttConnecting() called");
        if (!NetworkUtil.getConnectivityStatusString(this).equals(AppConstants.NOT_CONNECTED)) {
            actionBarStatus.setVisibility(View.INVISIBLE);
//            connectionAnimation.startAnimation(fadeIn);
//            connectionAnimation.setVisibility(View.VISIBLE);
            connectionAnimation.setMaxProgress(0.6f);
            connectionAnimation.setRepeatCount(LottieDrawable.INFINITE);
            connectionAnimation.playAnimation();
//            connectionAnimation.startAnimation(fadeIn);
            connectionAnimation.setVisibility(View.VISIBLE);
//            tempText.startAnimation(fadeOut);
//            if (networkConnected) {
//            actionBarStatus.setText("در حال اتصال");
                retryIcon.setVisibility(View.INVISIBLE);
//                connectionAnimation.setVisibility(View.VISIBLE);
//            ((RayanApplication)getApplication()).getMtd().updateMqttStatus(false);
//            }
        }
        Log.e(TAG, "Mqtt ConnectionStatus: CONNECTING Connection Retries:" + retryConnectMqtt.count);
    }

    public static boolean mqttConnected;
    @Override
        public void mqttConnected() {
        Log.d(TAG, "mqttConnected() called");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionAnimation.startAnimation(fadeOut);
                actionBarStatus.startAnimation(fadeIn);
//        connectionAnimation.setRepeatCount(0);
//        connectionAnimation.setMaxProgress(1f);
                String currentShowingGroup = RayanApplication.getPref().getCurrentShowingGroup();
                if (currentShowingGroup == null)
                    actionBarStatus.setText("رایان");
                else {
                    Group currentGroup = mainActivityViewModel.getGroup(currentShowingGroup);
                    if (currentGroup != null)
                        actionBarStatus.setText(currentGroup.getName());
                }
                retryIcon.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.INVISIBLE);
            }
        });
        Log.e(TAG, "Mqtt ConnectionStatus: Connected Connection Retries: " + retryConnectMqtt.count);
        retryConnectMqtt.stop();
        ((RayanApplication)getApplication()).getMsc().setMqttConnected(true);
        mqttConnectionStatus = Connection.ConnectionStatus.CONNECTED;
        mqttConnected = true;
        connectionRetries = 0;
        RayanApplication.getPref().saveProtocol(AppConstants.MQTT);
        ((RayanApplication)getApplication()).getMtd().updateMqttStatus(true);
    }

    @Override
    public void mqttDisconnected() {
        Log.d(TAG, "mqttDisconnected() called");
        mqttConnected = false;
        Log.e(TAG, "Mqtt ConnectionStatus: DISCONNECTED Connection Retries: " + retryConnectMqtt.count);
        ((RayanApplication)getApplication()).getMsc().setMqttConnected(false);
        mqttConnectionStatus = Connection.ConnectionStatus.DISCONNECTED;
        if (!retryConnectMqtt.isRunning())
            retryConnectMqtt.start();
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        ((RayanApplication) getApplication()).getMtd().updateMqttStatus(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtil.getConnectivityStatusString(MainActivity.this).equals(AppConstants.NOT_CONNECTED)) {
                    actionBarStatus.setVisibility(View.INVISIBLE);
                    connectionAnimation.setMaxProgress(0.6f);
                    connectionAnimation.setRepeatCount(LottieDrawable.INFINITE);
                    connectionAnimation.playAnimation();
                    connectionAnimation.setVisibility(View.VISIBLE);
                    retryIcon.setVisibility(View.INVISIBLE);
                }
                if (NetworkUtil.getConnectivityStatusString(MainActivity.this).equals(AppConstants.NOT_CONNECTED)) {
                    connectionRetries = 0;
                    actionBarStatus.setText("عدم اتصال به اینترنت");
                    retryIcon.setVisibility(View.INVISIBLE);
                    connectionAnimation.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void mqttReallyDisconnected(){
        Log.d(TAG, "mqttReallyDisconnected() called");
        DevicesFragment.subscribedDevices.clear();
        runOnUiThread(() -> {
            actionBarStatus.setText("عدم اتصال");
            retryIcon.setVisibility(View.VISIBLE);
            connectionAnimation.setVisibility(View.INVISIBLE);
        });
    }


    @Override
    public void mqttError() {
        Log.e(TAG, "Mqtt ConnectionStatus: ERROR Connection Retries: " + retryConnectMqtt.count);
        ((RayanApplication)getApplication()).getMsc().setMqttConnected(false);
        mqttConnected = false;
        mqttConnectionStatus = Connection.ConnectionStatus.ERROR;
        if (!retryConnectMqtt.isRunning())
            retryConnectMqtt.start();
        RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((RayanApplication) getApplication()).getMtd().updateMqttStatus(false);
                if (!NetworkUtil.getConnectivityStatusString(MainActivity.this).equals(AppConstants.NOT_CONNECTED)) {
                    actionBarStatus.setVisibility(View.INVISIBLE);
                    connectionAnimation.setMaxProgress(0.6f);
                    connectionAnimation.setRepeatCount(LottieDrawable.INFINITE);
                    connectionAnimation.playAnimation();
                    connectionAnimation.setVisibility(View.VISIBLE);
                    retryIcon.setVisibility(View.INVISIBLE);
                }
                if (NetworkUtil.getConnectivityStatusString(MainActivity.this).equals(AppConstants.NOT_CONNECTED)) {
                    connectionRetries = 0;
                    actionBarStatus.setText("عدم اتصال به اینترنت");
                    retryIcon.setVisibility(View.INVISIBLE);
                    connectionAnimation.setVisibility(View.INVISIBLE);
                }
            }
        });
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
//            default:
//                Uri contactUri = data.getData();
//                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
//                Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
//                        null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    String number = cursor.getString(numberIndex);
//                    Uri uri2 = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number.trim()));
//                    Cursor cursor2 = getContext().getContentResolver().query(uri2, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
//                    String contactName = null;
//                    if(cursor2.moveToFirst()) {
//                        contactName = cursor2.getString(cursor2.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
//                    }
//                    Log.e("lklklklklklklk", "name" + contactName);
//                    cursor2.close();
//                }
//                cursor.close();

        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "OnDestroy...");
        if (RayanApplication.getPref().isLoggedIn()) {
            stopService(new Intent(this, UDPServerService.class));
//            mainActivityViewModel.disconnectMQTT(MainActivityViewModel.connection);
            RayanApplication.getPref().saveProtocol(AppConstants.UDP);
        }
            super.onDestroy();
    }

    int counter = 0;
    int limit = 2;
    int skip = 0;
    ApiService apiService;
    @SuppressLint("CheckResult")
    @Override
    public void onBackPressed() {
//        startService(new Intent(this, AlwaysOnService.class));
//        WifiUtils.withContext(getApplicationContext())
//                .connectWith("Rayan_remote_hub_222222_f", "12345678")
//                .onConnectionResult(new ConnectionSuccessListener() {
//                    @Override
//                    public void success() {
//                        Log.e("isSuccessful" , "isisisisi: ");
//                        Toast.makeText(MainActivity.this, "به دستگاه متصل شد", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void failed(@androidx.annotation.NonNull ConnectionErrorCode errorCode) {
//                        Log.e("NOTsUCCESSFULL" , "Error isisisisi: " + errorCode);
//                        Toast.makeText(MainActivity.this, "خطا در اتصال به دستگاه", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .start();
//        WifiUtils.withContext(getApplicationContext())
//                .connectWith("Rayan_remote_hub_222222_f", "12345678")
//                .setTimeout(40000)
//                .onConnectionResult(new ConnectionSuccessListener() {
//                    @Override
//                    public void success() {
//                        Toast.makeText(MainActivity.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void failed(@NonNull ConnectionErrorCode errorCode) {
//                        Toast.makeText(MainActivity.this, "EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .start();
//        mainActivityViewModel.getGroups();
//        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//        DeviceDatabase deviceDatabase = new DeviceDatabase(this);
//        RemoteDataDatabase remoteDataDatabase = new RemoteDataDatabase(this);
//        RemoteDatabase remoteDatabase = new RemoteDatabase(this);
//        RemoteHubDatabase remoteHubDatabase = new RemoteHubDatabase(this);
//        GroupDatabase groupDatabase = new GroupDatabase(this);
//        Log.e(TAG, "G"+groupDatabase.getAllGroups());
//        Log.e(TAG, "G"+deviceDatabase.getAllDevices());
//        Log.e(TAG, "G"+remoteHubDatabase.getAllRemoteHubs());
//        Log.e(TAG, "G"+remoteDatabase.getAllRemotes());
//        Log.e(TAG, "G"+remoteDataDatabase.getAllRemoteDatas());
//        groupDatabase.getAllCount("");
//
//        mainActivityViewModel.getGroupsv3();

//        apiService = ApiUtils.getApiService();
//        RemoteHubDatabase remoteHubDatabase = new RemoteHubDatabase(this);
//        RemoteDatabase remoteDatabase = new RemoteDatabase(this);
//        RemoteHub poor = remoteHubDatabase.getAllRemoteHubs().get(0);
//        poor.setName("Blah Blah man");
//        remoteHubDatabase.updateRemoteHub(poor);
//        Remote pp = remoteDatabase.getAllRemotes().get(0);
//        pp.setName("man manam");
//        remoteDatabase.updateRemote(pp);
//        Log.e(TAG, "RemoteHub: " + remoteHubDatabase.getAllRemoteHubs());
//        Log.e(TAG, "Remote: " + remoteDatabase.getAllRemotes());
//        Log.e(TAG, "RemoteHub: " + remoteHubDatabase.getAllRemoteHubs().size());
//        Log.e(TAG, "Remote: " + remoteDatabase.getAllRemotes().size());
//        DeviceDatabase deviceDatabase = null;
//        GroupDatabase groupDatabase = null;
//        UserDatabase userDatabase = null;
//        UserMembershipDatabase userMembershipDatabase = null;
//        String tt = "@@@@@@@@@@";
//        StartupApiRequests startupApiRequests = new StartupApiRequests(apiService,deviceDatabase, groupDatabase, groupDatabase, userDatabase, userMembershipDatabase);
//        startupApiRequests.getGroups();
//        Observable.concat(Observable.just(1,2,4,5), Observable.just("a","b","c"))
//                .subscribe(new Observer<Serializable>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
//                    }
//
//                    @Override
//                    public void onNext(Serializable serializable) {
//                        Log.d(TAG, "onNext() called with: serializable = [" + serializable + "]");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: ", e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d(TAG, "onComplete() called");
//                    }
//                });
//        apiService = ApiUtils.getApiService();
//        Observable<RemoteHubsResponse> a = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
//                .flatMap(new Function<Long, Observable<RemoteHubsResponse>>() {
//                    @Override
//                    public Observable<RemoteHubsResponse> apply(Long aLong) throws Exception {
//                        Log.e("//////////","Sending: Limit" + limit + " Skip: " + skip);
//                        Map<String , String> params = new HashMap<>();
//                        params.put("limit", String.valueOf(limit));
//                        params.put("skip", String.valueOf(skip));
//                        return apiService.getRemoteHubs(RayanApplication.getPref().getToken(), params);
//                    }
//                })
//                .takeWhile(new Predicate<RemoteHubsResponse>() {
//                    @Override
//                    public boolean test(RemoteHubsResponse remoteHubsResponse) throws Exception {
//                        if (limit < remoteHubsResponse.getData().getCount()) {
//                            limit += 2;
//                            skip += 2;
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//        Observer b =
//                new Observer<RemoteHubsResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e(tt, "Shoroo shod");
//
//                    }
//
//                    @Override
//                    public void onNext(RemoteHubsResponse remoteHubsResponse) {
//                        Log.e(tt, "badi ro dad"+remoteHubsResponse);
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(tt, "error dad dada"+e);
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(tt, "tamoom shod");
//
//                    }
//                };
//        apiService.getGroups(RayanApplication.getPref().getToken()).subscribeOn(Schedulers.io())
//                .flatMap(new Function<GroupsResponse, Observable<RemoteHubsResponse>>() {
//                    @Override
//                    public Observable<RemoteHubsResponse> apply(GroupsResponse groupsResponse) throws Exception {
//                        return a;
//                    }
//                })
//                .subscribe(new Observer<RemoteHubsResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e(TAG, "onSubscribe: ");
//
//                    }
//
//                    @Override
//                    public void onNext(RemoteHubsResponse remoteHubsResponse) {
//                        Log.e(TAG, "onNext:  group ha ro gereft");
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: ", e);
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "onComplete: tammom shod group ha");
//
//                    }
//                });

//        apiService.getGroups(RayanApplication.getPref().getToken()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
//                .doOnNext(new Consumer<GroupsResponse>() {
//                    @Override
//                    public void accept(GroupsResponse groupsResponse) throws Exception {
//                        Log.e("@@@@@@@@@@@", "Accept" + groupsResponse);
//                    }
//                })
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("@@@@@@@@@@@", "Accept Nashod" + throwable);
//                    }
//                })
//                .flatMap(new Function<GroupsResponse, Observable<RemoteHubsResponse>>() {
//                    @Override
//                    public Observable<RemoteHubsResponse> apply(GroupsResponse groupsResponse) throws Exception {
//                        Log.e("@#@#","Accept:"+groupsResponse);
//                        Map<String , String> params = new HashMap<>();
//                        params.put("limit",limit);
//                        params.put("skip",skip);
//                        return apiService.getRemoteHubs(RayanApplication.getPref().getToken(), params);
//                    }
//                })
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("@@@@@@@@@@@", "Accept Nashod" + throwable);
//                    }
//                })
//                .doOnNext(new Consumer<RemoteHubsResponse>() {
//                    @Override
//                    public void accept(RemoteHubsResponse remoteHubsResponse) throws Exception {
//                        Log.e("@@@@@@@@@@@", "Response oomad" + remoteHubsResponse);
//                    }
//                })
//                .flatMap(new Function<RemoteHubsResponse, Observable<Integer>>() {
//                    @Override
//                    public Observable<Integer> apply(RemoteHubsResponse remoteHubsResponse) throws Exception {
//                        return Observable.just(9999999);
//                    }
//                })
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("EEEE", "E inam error dada ke" + throwable);
//                    }
//                })
//                .doOnNext(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.e("EEEE", "Na ok shod " + integer);
//                    }
//                })
//                .subscribe(new Observer<Integer>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//                        Log.d(TAG, "onNext() called with: integer = [" + integer + "]");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: ", e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d(TAG, "onComplete: complete shod");
//                    }
//                });

//        Observable<Integer> o1 =
//                Observable.just(1,10,20,30,40, 140, 150, 170)
//                        .flatMap(new Function<Integer, Observable<Integer>>() {
//                            @Override
//                            public Observable<Integer> apply(Integer integer) throws Exception {
//                                return Observable.just(integer);
//                            }
//                        })
////                .map(new Function<Integer, ObservableSource<?>>() {
////                    @Override
////                    public ObservableSource<?> apply(Integer integer) throws Exception {
////                        Log.e("?>?>?>?>?>?>", "First FlatMap " + counter);
////                        counter++;
////                        return Observable.just(counter);
////                    }
////                })
//                .repeatWhen(objectObservable -> objectObservable.delay(100, TimeUnit.MILLISECONDS))
////                .repeat(20)
//                .takeWhile(new Predicate<Object>() {
//                    @Override
//                    public boolean test(Object o) throws Exception {
//                        Log.d(TAG, "test() called with: o = [" + o + "]" + ((int)o < 100));
//                        if ((int)o < 100)
//                            return true;
//                        return false;
//                    }
//                });
//        Observable<Integer> o2 = Observable.just(6,7,8,9,10);
//        Observable.concat(o1, o2).subscribe(new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.e(TAG, "onSubscribe() called with: d = [" + d + "]");
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//                Log.e(TAG, "onNext() called with: integer = [" + integer + "]");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "onError() called with: e = [" + e + "]");
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "onComplete() called");
//            }
//        });
////        Observable.just(counter)
////                .flatMap(new Function<Integer, ObservableSource<?>>() {
////            @Override
////            public ObservableSource<?> apply(Integer integer) throws Exception {
////                Log.e("?>?>?>?>?>?>", "First FlatMap " + counter);
////                counter++;
////                return Observable.just(counter);
////            }
////        })
////                .repeatWhen(objectObservable -> objectObservable.delay(100, TimeUnit.MILLISECONDS))
//////                .repeat(20)
////                .takeWhile(new Predicate<Object>() {
////            @Override
////            public boolean test(Object o) throws Exception {
////                Log.d(TAG, "test() called with: o = [" + o + "]" + ((int)o < 10));
////                if ((int)o < 10)
////                    return true;
////                return false;
////            }
////        })
////                .concatWith(new ObservableSource<Object>() {
////            @Override
////            public void subscribe(Observer<? super Object> observer) {
////                Log.e("CCCCCCCCCCCCC", "Concated Man>>>>" + observer);
////                observer.onNext(1000);
////
////            }
////        })
//////                .flatMap(new Function<Object, ObservableSource<?>>() {
//////                    @Override
//////                    public ObservableSource<?> apply(Object o) throws Exception {
//////                        Log.e(TAG, "apply() called with: o = [" + o + "]");
//////                        return new ObservableSource<Object>() {
//////                            @Override
//////                            public void subscribe(Observer<? super Object> observer) {
//////                                observer.onError(new RuntimeException());
////////                                observer.onNext(o);
////////                                observer.onComplete();
//////                            }
//////                        };
//////                    }
//////                })
////                .subscribe(new Observer<Object>() {
////            @Override
////            public void onSubscribe(Disposable d) {
////
////            }
////
////            @Override
////            public void onNext(Object o) {
////                Log.e(TAG, "onNext() called with: o = [" + o + "]");
////            }
////
////            @Override
////            public void onError(Throwable e) {
////                Log.e(TAG, "onError() called with: e = [" + e + "]");
////            }
////
////            @Override
////            public void onComplete() {
////                Log.d(TAG, "onComplete() called");
////            }
////        });
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void getScanResults(@NonNull final List<ScanResult> results){
        if (results.isEmpty()) {
            Log.i(TAG, "SCAN RESULTS IT'S EMPTY");
            return;
        }
        Log.i(TAG, "GOT SCAN RESULTS " + results);
    }

    private void checkResult(boolean isSuccess) {
        if (isSuccess)
            Toast.makeText(MainActivity.this, "WIFI ENABLED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "COULDN'T ENABLE WIFI", Toast.LENGTH_SHORT).show();
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

    private void setupBottomNavigationViewPager(ViewPager viewPager) {
        BottomNavigationViewPagerAdapter adapter = new BottomNavigationViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //   viewPager.beginFakeDrag();
//        TypedValue tv = new TypedValue();
//        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
//        }
//        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
//        lp.bottomMargin += actionBarHeight + 15;
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
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    DevicesDiffCallBack devicesDiffCallBack;
    List<Device> devices = new ArrayList<>();
    public void observeDevicesForPlaySoundEffect(){
        mainActivityViewModel.getAllDevicesLive().observe(this, new android.arch.lifecycle.Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> newDevices) {
                devicesDiffCallBack = new DevicesDiffCallBack(newDevices, devices);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(devicesDiffCallBack);
                diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                    @Override
                    public void onInserted(int i, int i1) {

                    }

                    @Override
                    public void onRemoved(int i, int i1) {

                    }

                    @Override
                    public void onMoved(int i, int i1) {

                    }

                    @Override
                    public void onChanged(int position, int count, @Nullable Object o) {
                        if (RayanApplication.getPref().getIsNodeSoundOn() && o != null)
                            try {
                                for (String key : ((Bundle) o).keySet()) {
                                    if (key.equals("pin1")) {
                                        mainActivityViewModel.playSoundEffect(((Bundle) o).getString("pin1"));
                                    } else if (key.equals("pin2")) {
                                        mainActivityViewModel.playSoundEffect(((Bundle) o).getString("pin2"));
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });
                devices = newDevices;
            }
        });
    }
//        nsdHelper = new NsdHelper(this);
//        nsdHelper.registerService(AppConstants.HTTP_TO_DEVICE_PORT);
  //  NsdHelper nsdHelper;
//    NsdHelper nsdHelper;
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
        @SuppressLint("CheckResult")
        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.devicesManagementActivity:
//                    Log.e("?_?_?_?_?_", "allRequests: " + cr.getAllRequests());
                    startActivity(new Intent(this, DeviceManagementActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.groupsActivity:
//                    cr.addRequest(new LocallyChange("111111", ""+System.currentTimeMillis(), LocallyChange.Type.NAME_API,"111111"));
                    startActivity(new Intent(this, GroupsActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.addNew:
                    addNewExpandable.toggle();
                    if(addNewExpandable.isExpanded()){
                        add_new_expand_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                    }else {add_new_expand_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down)); }
                    break;
                case R.id.addNewDeviceActivity:
                    startActivity(new Intent(this, AddNewDeviceActivity.class));
                    addNewExpandable.toggle();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.addNewRemoteActivity:
                    startActivity(new Intent(this, AddNewRemoteActivity.class));
                    addNewExpandable.toggle();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.profileActivity:
//                    cr.submitRequests();
                    startActivity(new Intent(this, ProfileActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.settingsActivity:
                    startActivity(new Intent(this, SettingssActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.sortByGroup:
//                    mainActivityViewModel.getAllDevicesFlowable().zipWith(mainActivityViewModel.getAllGroupsFlowable(), new BiFunction<List<Device>, List<Group>, Object>() {
//                        @Override
//                        public Object apply(List<Device> devices, List<Group> groups) throws Exception {
//                            Log.e("111111111111111", "Object isisisigooloo: " +Thread.currentThread()+ devices);
//                            Log.e("222222222222222", "Object isisisigooloo: " + groups);


//                            return new Object();
//                        }
//                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
//                        @Override
//                        public void accept(Object o) throws Exception {
//                            Log.e("??????????????", "Object isisisigooloo: " + o);
//                        }
//                    });
                    expandableLayout.toggle();
                    if(expandableLayout.isExpanded()){
                        expand_arrow_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                    }else {expand_arrow_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down)); }
                    if (drawer_groupsRecyclerViewAdapter == null) {
                        drawer_groupsRecyclerViewAdapter = new SortByGroupRecyclerViewAdapter(this, new ArrayList<>());
                        drawer_groupsRecyclerView.setAdapter(drawer_groupsRecyclerViewAdapter);
                        drawer_groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        drawer_groupsRecyclerViewAdapter.setListener(this);
                        Flowable.zip(mainActivityViewModel.getAllGroupsFlowable(), mainActivityViewModel.getAllDevicesFlowable(),mainActivityViewModel.getAllRemoteHubsFlowable(), (groups, devices1, remoteHubs) -> {
                            List<Group> items = new ArrayList<>();
                            for (Group group:groups) {
                                List<BaseDevice> baseDevices = new ArrayList<>();
                                for (Device device : devices1) {
                                    if (device.getGroupId().equals(group.getId()))
                                        baseDevices.add(device);
                                }for (RemoteHub remoteHub : remoteHubs) {
                                    if (remoteHub.getGroupId().equals(group.getId()))
                                        baseDevices.add(remoteHub);
                                }
                                group.setBaseDevices(baseDevices);
                            }
                            Group g = new Group();
                            List<BaseDevice> baseDevices = new ArrayList<>();
                            baseDevices.addAll(devices1);
                            baseDevices.addAll(remoteHubs);
                            g.setBaseDevices(baseDevices);
                            g.setDevices(devices);
                            g.setName("همه");
                            items.add(g);
                            items.addAll(groups);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawer_groupsRecyclerViewAdapter.setItems(items);
                                }
                            });
                            return new Object();
                        }).subscribe();
                    }
                    break;
                case R.id.supportActivity:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    }
                    else {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:09190596520"));
                        startActivity(callIntent);
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.parent:
                    break;
            }
//            throw new RuntimeException("...");
        }

        @Override
        public void onGroupClicked (Group item){
            drawerLayout.closeDrawer(GravityCompat.START);
            for (int a = 0;a<getSupportFragmentManager().getFragments().size();a++)
                if (getSupportFragmentManager().getFragments().get(a) instanceof DevicesFragment)
                    ((DevicesFragment) getSupportFragmentManager().getFragments().get(a)).sortDevicesByGroup(item.getId());
            drawer_groupsRecyclerViewAdapter.notifyDataSetChanged();
            if (item.getName().equals("همه")){

//                String firstWord = "ر" ;
//                String secondWord = "ا" ;
//                String thirdWord = "ی" ;
//                String fourthWord = "ا" ;
//                String fifthWord = "ن" ;
//                Spannable spannable = new SpannableString(firstWord+secondWord+thirdWord+fourthWord + fifthWord);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.red)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.blue)), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.ms_black)), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.deep_orange)), 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.pink)), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                actionBarStatus.setText( spannable );
                actionBarStatus.setText("رایان");
            }else{
                actionBarStatus.setText(item.getName());
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
            switch (requestCode) {
                case REQUEST_PHONE_CALL: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:09190596520"));
                        startActivity(callIntent);
                    }
                    else {
                        Toast.makeText(this, "برای تماس با پشتیبانی اجازه دسترسی لازم است", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
    }

//    @OnClick(R.id.statusIcon)

    private MqttClient mqttClient;
    public void connectToMqtt(){
        Log.d(TAG, "connectToMqtt() called mqttConnectionStatus Is: " + mqttConnectionStatus);
        MqttClientService.initializeAndConnectToBroker(getApplication());
//        actionBarStatus.setText("کانکتینگ");
//        if (!(mqttConnectionStatus.equals(Connection.ConnectionStatus.CONNECTING) || mqttConnectionStatus.equals(Connection.ConnectionStatus.CONNECTED)))
//        mainActivityViewModel.connectToMqtt(MainActivity.this);
//                .observe(this, connection -> {
//            MainActivityViewModel.connection.postValue(connection);
//            switch (connection.getStatus()) {
//                case NONE:
//                    break;
//                case CONNECTING:
//                    this.mqttConnecting();
//                    break;
//                case CONNECTED:
//                    this.mqttConnected();
//                    break;
//                case DISCONNECTING:
//                    break;
//                case DISCONNECTED:
//                    this.mqttDisconnected();
//                    break;
//                case ERROR:
//                    this.mqttError();
//                    break;
//            }
//        });
    }

    @Override
    public void wifiNetwork(boolean connected, String ssid) {
//        Toast.makeText(this, "Wifi "+this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        Log.e("MainActivity","wifiNetwork "+ssid+connected + "mqttStatus: "+mqttConnectionStatus);
        ((RayanApplication)getApplication()).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.WIFI);
        if (connected && !mqttConnectionStatus.equals(Connection.ConnectionStatus.CONNECTED)) {
            Log.e("///////////////", "////connecting to mqtt" + mqttConnectionStatus);
            connectToMqtt();
//            if (!retryConnectMqtt.isRunning())
//                retryConnectMqtt.start();
        }
        if (connected){
            ((RayanApplication)getApplication()).getMtd().setCurrentSSID(ssid);
            mainActivityViewModel.getGroups();
            RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/", ""));
            mainActivityViewModel.sendNodeToAll();
        }
    }

    @Override
    public void mobileNetwork(boolean connected) {
        Log.e("MainActivity","mobileNetwork "+connected);
        ((RayanApplication)getApplication()).getMtd().setCurrentSSID(AppConstants.UNKNOWN_SSID);
        ((RayanApplication)getApplication()).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.MOBILE);
        if (connected && !mqttConnectionStatus.equals(Connection.ConnectionStatus.CONNECTED)) {
            Log.e("///////////////", "////connecting to mqtt");
            connectToMqtt();
        }
        if (connected){
//            if (!retryConnectMqtt.isRunning())
//                retryConnectMqtt.start();
            mainActivityViewModel.getGroups();
        }
    }

    @Override
    public void vpnNetwork() {
        Log.e("MainActivity","vpnNetwork ");
//        Toast.makeText(this, "VPN", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notConnected() {
        Log.e("MainActivity","notConnected ");
//        Toast.makeText(this, "Not Connected "+this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        ((RayanApplication)getApplication()).getMtd().updateStatus(MessageTransmissionDecider.ConnectionStatus.NOT_CONNECTED);
        actionBarStatus.setText("عدم اتصال به اینترنت");
        actionBarStatus.setVisibility(View.VISIBLE);
        retryIcon.setVisibility(View.INVISIBLE);
        connectionAnimation.setVisibility(View.INVISIBLE);
    }

    public void initializeAnimations(){
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(600);
        fadeIn.setFillAfter(true);
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(700);
        fadeOut.setFillAfter(true);
    }

    DevicesFragment devicesFragment;
    FavoritesFragment favoritesFragment;
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DevicesFragment)
            devicesFragment = (DevicesFragment) fragment;
        else if (fragment instanceof  FavoritesFragment)
            favoritesFragment = (FavoritesFragment) fragment;
    }

    @Override
    public boolean isInDevicesFragment(String chipId) {
        for (Device d: getDevices())
            if (d.getChipId().equals(chipId))
                return true;
        return false;
    }

    @Override
    public List<Device> getDevices() {
        return devicesFragment.getFinalDevices();
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
//    }//{"src":"111111","result":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"yyz+4yK0mByoiYrkw1D3pA=="}
//}//

//{"text":"yxEX1vOqupgRIiIE6mi11szCSG6glHizIdaPimHgGJoMk5B5jC/aTuoLF5p7MTJlz/yIH4seE3HatSek9ipis8JNmUzJX7tnKI7E14ur5jZ7y9Xr0TUIv3HQcU0sfMf3", "result":"en", "src":""}
//{"result":"YES", "src":"5958528"}
//{"text":"G9DU4Dr/jyMuH6OTchlQebccrrbqa7JrIfGuKZ8RurU=", "result":"de", "src":""}
//{"text":"xpq/VGgyD0pAf94O1fmSgg==", "result":"de", "src":"","k":"8nro4q0emv8k1uv5"}
//{"text":"PKa/MINB25FvvfbOwFA2sGbC+OPoM+m3AWoTi7OK/l4=", "result":"de", "src":""}
//{"text":"50tLQ4W90zvVMENvGd0DZw==\n", "result":"de", "src":"", "k":"vg4mseo0ouo2zf1j"}
//{"src":"222222","result":"TLMSDONE", "name":"ab","pin1":"on","pin2":"off", "stword":"juO+H8ZKWVwZvMP8r/RQgw=="}
//{"text":"ho813pvToMH+YszQT9RVdg==", "result":"de", "src":"", "k":"q916zpzn15rcimcv"}
//{"text":"tffd9rtikd202gcw", "result":"hmac", "src":"5958528", "k":"zmk4rid5h3e1mk66"}
//{"text":"5mrr48f1nlgex7cy", "result":"en", "src":"137067", "k":"zmk4rid5h3e1mk66"}

//{"text":"abcdefgh#25#", "result":"en", "src":"5958528", "k":"2kd6xdeesfc8kh2i"}
//
/*
{"text":"P27upkIy3kIvJnhRL/p59A==", "result":"de", "src":"", "k":"zmk4rid5h3e1mk66"}
 */

// 24 Bahman
//entrance 10:30
//1:20
//2:45
//1:30
//2:55
//Exit: 19:30

/*

7 Esfand
25 min

8 Esfand
1 Hour = 60 min

9 Esfand
50 min
20 min

10 Esfand
50 min
60 min == 1 
15 min 
15 min 
15 min
10 min
//////////////2:45


19:
1:10
////////////////////////////////////////////

22 esfand:
55 min
35 min

25 esfand
50 min

26 esfand
50 min
30 min

28 Esfand:
40 min



5,6,7 Farvardin
2:15 hours

8 Farvardin
50 min

9 Farvardin
2:20 min
15 min


11 Farvardin
50 min


12 Farvardin
2:40 min

14 Farvardin
1:05 min

15 Farvardin
4:35 hours

16 farvardin:
50 min

17 Farvardin
3:40 min

18 Farvardin
1:45 min

19 Farvardin
1:10 min

20 Farvardin
2:55 min

21 Farvardin
2:25 min

22 Farvardin
5:35 min

23 Farvardin
2:0 min

24 Farvardin
3:20 min

25 Farvardin
2:40 min

26 Farvardin
2:50 min

27 Farvardin
problem with -44 -54
2:50 min


28 Farvardin
2:00 min
2:15 min

29 Farvardin
4:35 min


30 Farvardin
30 min
50 min

01 Ordibehesht
50 min

7 ordibehesht
48 min

8 Ordibehesht
28min

14 Ordibehesht
1:13 min

15 Ordibehesht
1:53 min

////////////////

30 Ordibehesht
3:19 min

//////////////

22 Khordad
55 min

/////////////
4 Tir
2:10 min...




 */