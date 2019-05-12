package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MessageTransmissionDecider {

    private final String TAG = this.getClass().getSimpleName();
    public enum Status {
        NOT_CONNECTED,
        WIFI,
        MOBILE,
        VPN
    }
    public enum PROTOCOL{
        HTTP,
        UDP,
        MQTT,
        STANDALONE
    }
    private String currentSSID;
    private boolean mqttConnected;
    private Status status;
    private List<Device> devices;
    private Map<String ,List<PROTOCOL>> communicationRoutes;


    public MessageTransmissionDecider(Context context, List<Device> devices) {
        communicationRoutes = new HashMap<>();
        this.devices = devices;
        switch (NetworkUtil.getConnectivityStatusString(context)){
            case "WIFI":
                status = Status.WIFI;
                break;
            case "NOT_CONNECTED":
                status = Status.NOT_CONNECTED;
                break;
            case "MOBILE":
                status = Status.MOBILE;
                break;
            case "VPN":
                status = Status.VPN;
                break;
        }
        Log.e(TAG, "I am created Going to Compute...Devices: " + devices + "\nStatus: " + status);
        computeCommunicationRoutes(devices);
    }

    public void updateMqttStatus(boolean mqttConnected){
        if (this.mqttConnected != mqttConnected){
            this.mqttConnected = mqttConnected;
            Log.e(TAG, "mqtt Status Changed Going to Compute...");
            computeCommunicationRoutes(this.devices);
        }
    }

    public void addDevices(List<Device> insertedDevices){
        this.devices.addAll(insertedDevices);
        computeCommunicationRoutes(insertedDevices);
    }

    public void setDevices(List<Device> devices){
        Log.e(TAG,"Updating Devices " +devices);
        this.devices = devices;
        Log.e(TAG, "List of devices changed Going to Compute...");
        computeCommunicationRoutes(devices);
    }

    public void updateDevice(Device device){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                List<PROTOCOL> protocols = new ArrayList<>();
                Log.e(TAG, "Updating 1 device: " + device);
                for (int a = 0;a<MessageTransmissionDecider.this.devices.size();a++)
                    if (devices.get(a).getChipId().equals(device.getChipId()))
                        devices.set(a, device);
                switch (status){
                    case WIFI:
                        Log.e(TAG,"switch is wifi "+ status+
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>ip != nulL? "+ (device.getIp() != null)+
                                "<!>mqttConnected "+ (mqttConnected)+
                                "<!>devices.get(a).getTopic()!= null? "+ (device.getTopic()!= null)+
                                "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                        );
                        if (MainActivityViewModel.connection.getValue() != null )
                            Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                        if (device.getSsid().equals(currentSSID) && device.getIp() != null) {
                            protocols.add(PROTOCOL.HTTP);
                            protocols.add(PROTOCOL.UDP);
                            Log.e(TAG,"http addeddd ");
                        }
                        if (mqttConnected && device.getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                            protocols.add(PROTOCOL.MQTT);
                            Log.e(TAG,"mqtt addeddd");
                        }
                        break;
                    case MOBILE:
                        Log.e(TAG,"switch in mobile "+
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>ip nulL? "+ (device.getIp() != null)+
                                "<!>mqttConnected "+ (mqttConnected)+
                                "<!>devices.get(a).getTopic()!= null? "+ (device.getTopic()!= null)+
                                "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                        );
                        if (MainActivityViewModel.connection.getValue() != null )
                            Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                        if (mqttConnected && device.getTopic()!= null && MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected()){
                            protocols.add(PROTOCOL.MQTT);
                            Log.e(TAG,"mqtt addeddd ");
                        }
                        break;
                    case NOT_CONNECTED:
                        Log.e(TAG,"switch in not connected");
//                    if (!devices.get(a).getSsid().equals(AppConstants.UNKNOWN_SSID) && devices.get(a).getStyle().equals(AppConstants.DEVICE_STANDALONE_STYLE))
//                        protocols.add(PROTOCOL.STANDALONE);
                        Log.e(TAG,"STANDALONESTANDALONEcomputeCommunicationRoutes ");
                        break;
                    case VPN:
                        Log.e(TAG,"switch is VPN "+ status+
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>ip != nulL? "+ (device.getIp() != null)+
                                "<!>mqttConnected "+ (mqttConnected)+
                                "<!>devices.get(a).getTopic()!= null? "+ (device.getTopic()!= null)+
                                "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                        );
                        if (MainActivityViewModel.connection.getValue() != null )
                            Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                        if (device.getSsid().equals(currentSSID) && device.getIp() != null) {
                            protocols.add(PROTOCOL.HTTP);
                            protocols.add(PROTOCOL.UDP);
                            Log.e(TAG,"http addeddd ");
                        }
                        if (mqttConnected && device.getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                            protocols.add(PROTOCOL.MQTT);
                            Log.e(TAG,"mqtt addeddd");
                        }
                        break;
                }
                communicationRoutes.put(device.getChipId(),protocols);
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
        .subscribe(new Observer<Object>() {
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

    }

    public void updateStatus(Status status){
        Log.e(TAG,"Updating Status: "+ status+" Going to Compute...? " + !status.equals(this.status));
        if (!status.equals(this.status)) {
            this.status = status;
            computeCommunicationRoutes(this.devices);
        }
    }

    public void setCurrentSSID(String currentSSID) {
        this.currentSSID = currentSSID;
    }

    private void computeCommunicationRoutes(List<Device> devices){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Log.e(TAG,"Starting Computing for "+devices.size()+" Devices: Current SSID:" + currentSSID +" Status: " + status);
                Map<String ,List<PROTOCOL>> cr = new HashMap<>();
                for (int a = 0;a<devices.size();a++){
                    Log.e(TAG,"-------------------------------------------------------------------------------");
                    List<PROTOCOL> protocols = new ArrayList<>();
                    Log.e(TAG,"Checking This Device: " + devices.get(a));
                    switch (status){
                        case WIFI:
                            Log.e(TAG,"switch is wifi "+ status+
                                    "<!>equality of ssids "+ (devices.get(a).getSsid().equals(currentSSID))+
                                    "<!>ip != nulL? " + (devices.get(a).getIp() != null)+
                                    "<!>mqttConnected " + (mqttConnected)+
                                    "<!>devices.get(a).getTopic()!= null? "+ (devices.get(a).getTopic()!= null)+
                                    "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                            );
                            if (MainActivityViewModel.connection.getValue() != null )
                                Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                            if (devices.get(a).getSsid().equals(currentSSID) && devices.get(a).getIp() != null) {
                                protocols.add(PROTOCOL.HTTP);
                                protocols.add(PROTOCOL.UDP);
                                Log.e(TAG,"http addeddd ");
                            }
                            if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                                protocols.add(PROTOCOL.MQTT);
                                Log.e(TAG,"mqtt addeddd");
                            }
                            break;
                        case MOBILE:
                            Log.e(TAG,"switch in mobile "+
                                    "<!>equality of ssids "+ (devices.get(a).getSsid().equals(currentSSID))+
                                    "<!>ip nulL? "+ (devices.get(a).getIp() != null)+
                                    "<!>mqttConnected "+ (mqttConnected)+
                                    "<!>devices.get(a).getTopic()!= null? "+ (devices.get(a).getTopic()!= null)+
                                    "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                            );
                            if (MainActivityViewModel.connection.getValue() != null )
                                Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                            if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected()){
                                protocols.add(PROTOCOL.MQTT);
                                Log.e(TAG,"mqtt addeddd ");
                            }
                            break;
                        case VPN:
                            Log.e(TAG,"switch is VPN "+ status+
                                    "<!>equality of ssids "+ (devices.get(a).getSsid().equals(currentSSID))+
                                    "<!>ip != nulL? " + (devices.get(a).getIp() != null)+
                                    "<!>mqttConnected " + (mqttConnected)+
                                    "<!>devices.get(a).getTopic()!= null? "+ (devices.get(a).getTopic()!= null)+
                                    "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                            );
                            if (MainActivityViewModel.connection.getValue() != null )
                                Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                            if (devices.get(a).getSsid().equals(currentSSID) && devices.get(a).getIp() != null) {
                                protocols.add(PROTOCOL.HTTP);
                                protocols.add(PROTOCOL.UDP);
                                Log.e(TAG,"http addeddd ");
                            }
                            if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                                protocols.add(PROTOCOL.MQTT);
                                Log.e(TAG,"mqtt addeddd");
                            }
                            break;
                        case NOT_CONNECTED:
                            Log.e(TAG,"switch in not connected");
//                    if (!devices.get(a).getSsid().equals(AppConstants.UNKNOWN_SSID) && devices.get(a).getStyle().equals(AppConstants.DEVICE_STANDALONE_STYLE))
//                        protocols.add(PROTOCOL.STANDALONE);
                            Log.e(TAG,"STANDALONESTANDALONEcomputeCommunicationRoutes ");
                            break;
                    }
                    cr.put(devices.get(a).getChipId(),protocols);
                }
//        communicationRoutes.clear();
                communicationRoutes.putAll(cr);
                Log.e(TAG,"Finishing Computing: ");
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Object>() {
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

    }

    public String sendMessage(Device device){
        Log.e(TAG, "getting routes: " + communicationRoutes);
//        for (int a = 0;a<devices.size();a++)
//            if (device.getChipId().equals(devices.get(a).getChipId())) {
//                if (communicationRoutes.get(device.getChipId()).size() == 0)
//                    return "NONE";
//                else return communicationRoutes.get(a).get(0).toString();
//            }
        List<PROTOCOL> p = communicationRoutes.get(device.getChipId());
        if (p != null) {
            if (p.size() == 0)
                return "NONE";
            else
                return p.get(0).toString();
        }return "NONE";
    }

    public List<PROTOCOL> getListOfAvailableRouts(String chipId){
        Log.e(TAG, "MTD Communication Routes: for " +chipId+" "+ communicationRoutes.get(chipId));
        return communicationRoutes.get(chipId);
    }


}
