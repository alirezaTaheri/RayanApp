package rayan.rayanapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Fragments.NewDevicesListFragment;
import rayan.rayanapp.R;
import rayan.rayanapp.Wifi.WifiHandler;

public class AddNewDeviceActivity extends AppCompatActivity {
    private final String TAG = AddNewDeviceActivity.class.getSimpleName();
    private WifiHandler wifiHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);
        wifiHandler = new WifiHandler();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, NewDevicesListFragment.newInstance())
                    .commitNow();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RayanApplication.getContext()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission Granted");
        } else {
            Log.e(TAG, "Permission not Granted...");
        }
    }

}
