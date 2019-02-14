package rayan.rayanapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
        statusCheck();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("برای ادامه نیاز به سرویس Location داریم")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission Granted 1");
            WifiHandler wifiHandler = new WifiHandler();
            Log.e(TAG, "ttttttt:1111111 " + wifiHandler.scan());
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Log.e(TAG, "tttttttThread11111111: " + wifiHandler.scan());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Log.e(TAG, "Permission not Granted...");
        }
    }

    @Override
    public void onBackPressed() {
        statusCheck();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RayanApplication.getContext()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RayanApplication.getContext()
                .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RayanApplication.getContext()
                .checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
            WifiHandler wifiHandler = new WifiHandler();
            Log.e(TAG, "ttttttt:1111111 " + wifiHandler.scan());
            Toast.makeText(this, "Every Thing is OK", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
