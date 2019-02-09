package rayan.rayanapp.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.polyak.iconswitch.IconSwitch;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.NetworkConnection;
import rayan.rayanapp.Data.NetworkConnectionLiveData;
import rayan.rayanapp.Adapters.viewPager.MainActivityViewPagerAdapter;
import rayan.rayanapp.Listeners.MqttStatus;
import rayan.rayanapp.Services.mqtt.Connection;
import rayan.rayanapp.ViewModels.MainActivityViewModel;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.udp.UDPServerService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Wifi.WifiCypherType;
import rayan.rayanapp.Wifi.WifiHandler;
import rayan.rayanapp.Wifi.WifiHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MqttStatus {

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
    public NetworkConnectionLiveData networkConnectionLiveData;
    MqttStatus mqttStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mqttStatus = this;
//        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        networkConnectionLiveData = new NetworkConnectionLiveData(getApplicationContext());
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
        networkConnectionLiveData.observe(this, networkConnection -> {
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MainActivityViewPagerAdapter viewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
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
                startActivity(new Intent(this, DeviceManagementListActivity.class));
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
        Log.e(TAG, "Mqt Status: CONNECTING");
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
}