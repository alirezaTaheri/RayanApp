package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.NetworkUtil;
import rayan.rayanapp.ViewModels.MainActivityViewModel;

public class MessageTransmissionDecider {

    public enum Status {
        NOT_CONNECTED,
        WIFI,
        MOBILE
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
        }
        Log.e("I am MTD:", "I am created Devices: " + devices + "\nStatus: " + status);
    }

    public void updateMqttStatus(boolean mqttConnected){
        if (this.mqttConnected != mqttConnected){
            this.mqttConnected = mqttConnected;
            computeCommunicationRoutes();
        }
    }

    public void updateDevices(List<Device> devices){
        Log.e("I am MTD:","Updating Devices " +devices);
        this.devices = devices;
        computeCommunicationRoutes();
    }
    public void updateStatus(Status status){
        Log.e("I am MTD:","Updating Status: "+ status);
        if (!status.equals(this.status)) {
            this.status = status;
//            computeCommunicationRoutes();
        }
    }

    public void setCurrentSSID(String currentSSID) {
        this.currentSSID = currentSSID;
    }

    private void computeCommunicationRoutes(){
        Log.e("I am MTD:","Starting Computing: Current SSID:" + currentSSID);
        communicationRoutes.clear();
        for (int a = 0;a<devices.size();a++){
            Log.e("I am MTD:","-------------------------------------------------------------------------------");
            List<PROTOCOL> protocols = new ArrayList<>();
            Log.e("I am MTD:","Checking This Device: " + devices.get(a));
            switch (status){
                case WIFI:
                    Log.e("I am MTD:","switch is wifi "+ status);
                    Log.e("I am MTD:","equality of ssids "+ (devices.get(a).getSsid().equals(currentSSID)));
                    Log.e("I am MTD:","ip != nulL? "+ (devices.get(a).getIp() != null));
                    Log.e("I am MTD:","mqttConnected "+ (mqttConnected));
                    Log.e("I am MTD:","devices.get(a).getTopic()!= null? "+ (devices.get(a).getTopic()!= null));
                    Log.e("I am MTD:","MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null ));
                    if (MainActivityViewModel.connection.getValue() != null )
                    Log.e("I am MTD:","MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                    if (devices.get(a).getSsid().equals(currentSSID) && devices.get(a).getIp() != null) {
                        Log.e("I am MTD:","switch is wifi "+ status);
                        protocols.add(PROTOCOL.HTTP);
                        protocols.add(PROTOCOL.UDP);
                        Log.e("I am MTD:","http addeddd ");
                    }
                    if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                        protocols.add(PROTOCOL.MQTT);
                        Log.e("I am MTD:","mqtt addeddd");
                    }
                    break;
                case MOBILE:
                    Log.e("I am MTD:","switch in mobile ");
                    Log.e("I am MTD:","equality of ssids "+ (devices.get(a).getSsid().equals(currentSSID)));
                    Log.e("I am MTD:","ip nulL? "+ (devices.get(a).getIp() != null));
                    Log.e("I am MTD:","mqttConnected "+ (mqttConnected));
                    Log.e("I am MTD:","devices.get(a).getTopic()!= null? "+ (devices.get(a).getTopic()!= null));
                    Log.e("I am MTD:","MainActivityViewModel.connection.getValue() != null ? "+ (MainActivityViewModel.connection.getValue() != null ));
                    if (MainActivityViewModel.connection.getValue() != null )
                    Log.e("I am MTD:","MainActivityViewModel.connection.getValue().isConnected()? "+ (MainActivityViewModel.connection.getValue().isConnected()));
                    if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected()){
                        protocols.add(PROTOCOL.MQTT);
                        Log.e("I am MTD:","mqtt addeddd ");
                    }
                    break;
                case NOT_CONNECTED:
                    Log.e("I am MTD:","switch in not connected");
//                    if (!devices.get(a).getSsid().equals(AppConstants.UNKNOWN_SSID) && devices.get(a).getStyle().equals(AppConstants.DEVICE_STANDALONE_STYLE))
//                        protocols.add(PROTOCOL.STANDALONE);
                    Log.e("I am MTD:","STANDALONESTANDALONEcomputeCommunicationRoutes ");
                    break;
            }
            communicationRoutes.put(devices.get(a).getChipId(),protocols);
        }
        Log.e("I am MTD:","Finishing Computing: ");
    }

    public String sendMessage(Device device){
        Log.e("I am MTD", "MTD Communication Routes: " + communicationRoutes);
//        for (int a = 0;a<devices.size();a++)
//            if (device.getChipId().equals(devices.get(a).getChipId())) {
//                if (communicationRoutes.get(device.getChipId()).size() == 0)
//                    return "NONE";
//                else return communicationRoutes.get(a).get(0).toString();
//            }
        List<PROTOCOL> p = communicationRoutes.get(device.getChipId());
        if (p.size() == 0)
            return "NONE";
        else
        return p.get(0).toString();
    }

    public List<PROTOCOL> getListOfAvailableRouts(String chipId){
        Log.e("I am MTD", "MTD Communication Routes: for " +chipId+" "+ communicationRoutes.get(chipId));
        return communicationRoutes.get(chipId);
    }


}
