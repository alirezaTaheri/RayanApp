package rayan.rayanapp.Helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.DeviceCommunicationParams;
import rayan.rayanapp.Mqtt.MqttClientService;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MessageTransmissionDecider {

    private final String TAG = this.getClass().getSimpleName();
    public enum ConnectionStatus {
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
    public enum Status {
        NORMAL,
        MODIFIED
    }

    private String currentSSID;
    private boolean mqttConnected;
    private ConnectionStatus connectionStatus;
    private Status status;
    private List<Device> devices;
    private Map<String ,List<PROTOCOL>> communicationRoutes;
    private Map<String ,Pair<DeviceCommunicationParams, List<PROTOCOL>>> routes;
    private Context context;

    public MessageTransmissionDecider(Context context) {
        communicationRoutes = new HashMap<>();
        devices = new ArrayList<>();
        routes = new HashMap<>();
        switch (NetworkUtil.getConnectivityStatusString(context)){
            case "WIFI":
                connectionStatus = ConnectionStatus.WIFI;
                break;
            case "NOT_CONNECTED":
                connectionStatus = ConnectionStatus.NOT_CONNECTED;
                break;
            case "MOBILE":
                connectionStatus = ConnectionStatus.MOBILE;
                break;
            case "VPN":
                connectionStatus = ConnectionStatus.VPN;
                break;
        }
        this.context = context;
        Log.e(TAG, "I am created Going to Compute...Devices: " + devices + "\nConnectionStatus: " + connectionStatus);
        currentSSID = AppConstants.NULL_SSID;
//        computeCommunicationRoutes(devices);
    }

    public void updateMqttStatus(boolean mqttConnected){
        if (this.mqttConnected != mqttConnected){
            this.mqttConnected = mqttConnected;
            Log.e(TAG, "mqtt ConnectionStatus Changed Going to Compute...");
            this.status = Status.MODIFIED;
//            computeCommunicationRoutes(this.devices);
        }
    }


    public void setDevices(List<Device> devices){
        Log.e(TAG,"Updating Devices " +devices);
        this.devices = devices;
        Log.e(TAG, "List of devices changed Going to Compute...");
//        computeCommunicationRoutes(devices);
    }

    public List<PROTOCOL> computeRoutes(Device device){
//        Observable.create(new ObservableOnSubscribe<Object>() {
//            @Override
//            public void subscribe(ObservableEmitter<Object> e) throws Exception {
//                for (int a = 0;a<MessageTransmissionDecider.this.devices.size();a++)
//                    if (devices.get(a).getChipId().equals(device.getChipId()))
//                        devices.set(a, device);
                List<PROTOCOL> protocols = new ArrayList<>();
                Log.e(TAG, "start computing CurrentSSID: "+currentSSID+"for device: " + device);
                switch (connectionStatus){
                    case WIFI:
                        Log.e(TAG,"switch is wifi "+ connectionStatus +
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>!device.getIp().equals(AppConstants.UNKNOWN_IP)? "+ (!device.getIp().equals(AppConstants.UNKNOWN_IP))+
                                "<!>mqttConnected "+ (mqttConnected)+
                                "<!>devices.get(a).getTopic()!= null? "+ (device.getTopic()!= null)+
                                "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                        );
                        if (MainActivityViewModel.connection.getValue() != null )
                            Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                        if (device.getSsid().equals(currentSSID) && !device.getIp().equals(AppConstants.UNKNOWN_IP)) {
                            protocols.add(PROTOCOL.HTTP);
                            protocols.add(PROTOCOL.UDP);
                            Log.e(TAG,"http addeddd ");
                        }
                        if (mqttConnected && device.getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                            protocols.add(PROTOCOL.MQTT);
                            Log.e(TAG,"mqtt addeddd");
                        }
                        if (mqttConnected && device.getTopic()!= null && MqttClientService.getMqttClientInstance((Application) context.getApplicationContext()) != null && MqttClientService.getMqttClientInstance((Application) context.getApplicationContext()).getConnection().isConnected()){
                            protocols.add(PROTOCOL.MQTT);
                            Log.e(TAG,"mqtt addeddd");
                        }
                        break;
                    case MOBILE:
                        Log.e(TAG,"switch in mobile "+
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>!device.getIp().equals(AppConstants.UNKNOWN_IP)? "+ (!device.getIp().equals(AppConstants.UNKNOWN_IP))+
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
                        Log.e(TAG,"switch is VPN "+ connectionStatus +
                                "<!>equality of ssids "+ (device.getSsid().equals(currentSSID))+
                                "<!>!device.getIp().equals(AppConstants.UNKNOWN_IP)? "+ (!device.getIp().equals(AppConstants.UNKNOWN_IP))+
                                "<!>mqttConnected "+ (mqttConnected)+
                                "<!>devices.get(a).getTopic()!= null? "+ (device.getTopic()!= null)+
                                "<!>MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null )
                        );
                        if (MainActivityViewModel.connection.getValue() != null)
                            Log.e(TAG,"MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                        if (device.getSsid().equals(currentSSID) && !device.getIp().equals(AppConstants.UNKNOWN_IP)) {
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
//                communicationRoutes.put(device.getChipId(),protocols);
                routes.put(device.getChipId(), Pair.create(new DeviceCommunicationParams(device.getSsid(), currentSSID, device.getIp(), mqttConnected), protocols));
                return protocols;
//            }
//        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
//        .subscribe(new Observer<Object>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Object o) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

    }

    public void updateStatus(ConnectionStatus status){
        Log.e(TAG,"Updating ConnectionStatus: "+ status+" Going to Compute...? " + !status.equals(this.connectionStatus));
        if (!status.equals(this.connectionStatus)) {
            this.connectionStatus = status;
//            computeCommunicationRoutes(this.devices);
        }
    }

    public void setCurrentSSID(String currentSSID) {
        if (currentSSID == null)
            this.currentSSID = AppConstants.NULL_SSID;
        else
            this.currentSSID = currentSSID;
    }

    private void computeCommunicationRoutes(List<Device> devices){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Log.e(TAG,"Starting Computing for "+devices.size()+" Devices: Current SSID:" + currentSSID +" ConnectionStatus: " + connectionStatus);
                Map<String ,List<PROTOCOL>> cr = new HashMap<>();
                for (int a = 0;a<devices.size();a++){
                    Log.e(TAG,"-------------------------------------------------------------------------------");
                    List<PROTOCOL> protocols = new ArrayList<>();
                    Log.e(TAG,"Checking This Device: " + devices.get(a));
                    switch (connectionStatus){
                        case WIFI:
                            Log.e(TAG,"switch is wifi "+ connectionStatus +
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
                            Log.e(TAG,"switch is VPN "+ connectionStatus +
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

    public String requestForSendMessage(Device device){
        List<PROTOCOL> p = new ArrayList<>();
        Log.e(TAG, "Current state is: [currentSsid: " +currentSSID +", connectionStatus: " + connectionStatus+", mqttConnected: "+mqttConnected+" ]");
        Log.e(TAG, "DeviceState is: " + routes.get(device.getChipId()));
        if (routes.get(device.getChipId()) == null){
            this.devices.add(device);
            p = computeRoutes(device);
        }
        else if (!Objects.requireNonNull(routes.get(device.getChipId())).first.getIp().equals(device.getIp()) ||
                !Objects.requireNonNull(routes.get(device.getChipId())).first.getDeviceSsid().equals(device.getSsid()) ||
                !Objects.requireNonNull(routes.get(device.getChipId())).first.getCurrentSsid().equals(currentSSID) ||
                !Objects.requireNonNull(routes.get(device.getChipId())).first.isMqttConnected() == mqttConnected){
            p = computeRoutes(device);
        }
        else p = routes.get(device.getChipId()).second;
        if (p != null) {
            if (p.size() == 0)
                return "NONE";
            else
                return p.get(0).toString();
        }return "NONE";
    }

    public List<PROTOCOL> getListOfAvailableRouts(String chipId){
//        Log.e(TAG, "MTD Communication Routes: for " +chipId+" "+ communicationRoutes.get(chipId));
        Log.e(TAG, "MTD Communication Routes: for " +chipId+" "+ routes.get(chipId));
        return routes.get(chipId).second;
    }


}
