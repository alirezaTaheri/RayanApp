package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.net.wifi.ScanResult;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Data.AccessPoint;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDevicesListViewModel extends DevicesFragmentViewModel {
    public NewDevicesListViewModel(@NonNull Application application) {
        super(application);
    }
    WifiHandler wifiHandler;
    public List<AccessPoint> getSSIDs(){
        List<AccessPoint> newDevices = new ArrayList<>();
        List<ScanResult> scanResults = new ArrayList<>();
        wifiHandler = new WifiHandler();
        scanResults = wifiHandler.scan();
        for (int a = 0; a<scanResults.size();a++){
            if (scanResults.get(a).SSID.trim().length()>0)
            newDevices.add(new AccessPoint(scanResults.get(a).SSID, scanResults.get(a).BSSID,scanResults.get(a).capabilities,scanResults.get(a).level));
        }
        return newDevices;
    }

    public void scan(){
        wifiHandler = new WifiHandler();
        wifiHandler.scan();
    }

}
