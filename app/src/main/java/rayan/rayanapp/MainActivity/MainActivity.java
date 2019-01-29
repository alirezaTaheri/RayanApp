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
                if (!networkConnection.isConnected()){
                    PROTOCOL = AppConstants.UDP;
                    mainActivityViewModel.disconnectMQTT();
                    RayanApplication.getPref().saveProtocol(AppConstants.UDP);
                    accessModeSwitch.setInactiveTintIconLeft();
                }

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


}