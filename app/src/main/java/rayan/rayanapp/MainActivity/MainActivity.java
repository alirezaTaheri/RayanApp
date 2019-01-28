package rayan.rayanapp.MainActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.polyak.iconswitch.IconSwitch;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.NetworkConnection;
import rayan.rayanapp.Data.NetworkConnectionLiveData;
import rayan.rayanapp.MainActivity.viewModels.DevicesFragmentViewModel;
import rayan.rayanapp.MainActivity.viewModels.MainActivityViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.accessModeSwitch)
    IconSwitch accessModeSwitch;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    MainActivityViewModel mainActivityViewModel;
    public static String PROTOCOL;
    private final String TAG = MainActivity.class.getSimpleName();
    public NetworkConnectionLiveData networkConnectionLiveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        ButterKnife.bind(this);
        networkConnectionLiveData = new NetworkConnectionLiveData(getApplicationContext());
        networkConnectionLiveData.observe(this, new Observer<NetworkConnection>() {
            @Override
            public void onChanged(@Nullable NetworkConnection networkConnection) {
                Log.e(TAG, "Network Connection: " + networkConnection);
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        startService(new Intent(this, UDPServerService.class));
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);
        mainActivityViewModel.getConnection().observe(this, connection -> {
            accessModeSwitch.setChecked(connection.isConnected()? IconSwitch.Checked.RIGHT: IconSwitch.Checked.LEFT);
        });
        accessModeSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                if (current.equals(IconSwitch.Checked.RIGHT)){
                    mainActivityViewModel.connectToMqtt(MainActivity.this);
                    RayanApplication.getPref().saveProtocol(AppConstants.MQTT);
                    PROTOCOL = AppConstants.MQTT;
                }
                else{
                    mainActivityViewModel.disconnectMQTT();
                    RayanApplication.getPref().saveProtocol(AppConstants.UDP);
                    PROTOCOL = AppConstants.UDP;
                }
            }
        });
        initialize();
    }

    public void initialize(){
        if (RayanApplication.getPref().getProtocol() == null)
            PROTOCOL = AppConstants.UDP;
        else
            PROTOCOL = RayanApplication.getPref().getProtocol();
            if (PROTOCOL.equals(AppConstants.MQTT))
                accessModeSwitch.setChecked(IconSwitch.Checked.RIGHT);
//                mainActivityViewModel.connectToMqtt(MainActivity.this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, UDPServerService.class));
        RayanApplication.getPref().saveLocalBroadcastAddress(mainActivityViewModel.getBroadcastAddress().toString().replace("/",""));
        mainActivityViewModel.sendNodeToAll();
    }

    @Override
    public void onBackPressed() {

        networkConnectionLiveData.observe(this, new Observer<NetworkConnection>() {
            @Override
            public void onChanged(@Nullable NetworkConnection networkConnection) {
                Log.e(TAG, "Network Connectionnnnnnnn: " + networkConnection);
            }
        });
        DevicesFragmentViewModel devicesFragmentViewModel = new DevicesFragmentViewModel(getApplication());
        devicesFragmentViewModel.getGroups();
    }

}
/*
+5min
10:58
13:58
//////
18:55
20:45

11 H:
11:15
11:20
/////
11:55
13:10
/////

12 : H
10:10
11:50
/////
13:10
14:10
/////
14:50
15:55
/////03:45
16:55
20:30
/////07:20
21:00
22:15
/////08:35
23:55
00:15

22 H:
17:50
20:40
/////
21:25
23:25

23
10:05
11:05

24
12:56
13:26
/////
13:31
13:41
/////
14:39
15:21
/////


26 H:
17:10
19:30
/////
21:30
21:50
/////
22:15
23:30
/////
23:30
12:05

27 H:
12:15
14:15
/////
14:30
15:30

30 : H
17:00
19:35
/////
20:45
21:20
/////
23:23
00:23

01: H
10:25
11:05
/////
11:50
16:38
/////
17:55
20:30
/////
21:04
22:25
/////
22:38
23:15
/////
23:30
00:45

02 :H
10:33
11:27
/////
12:08
16:45
/////
18:05
20:24
/////
22:01
23:06
 */