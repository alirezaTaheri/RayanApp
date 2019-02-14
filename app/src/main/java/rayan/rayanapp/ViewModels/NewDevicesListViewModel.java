package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Wifi.WifiHandler;

public class NewDevicesListViewModel extends DevicesFragmentViewModel {
    public NewDevicesListViewModel(@NonNull Application application) {
        super(application);
    }
    public List<NewDevice> getSSIDs(){
        List<NewDevice> newDevices = new ArrayList<>();
        List<ScanResult> scanResults = new ArrayList<>();
        WifiHandler wifiHandler = new WifiHandler();
        scanResults = wifiHandler.scan();
        for (int a = 0; a<scanResults.size();a++){
            newDevices.add(new NewDevice(scanResults.get(a).SSID, scanResults.get(a).BSSID,scanResults.get(a).capabilities,scanResults.get(a).level));
        }
        return newDevices;
    }

    public void scan(){
        WifiHandler wifiHandler = new WifiHandler();
        wifiHandler.scan();
    }

}