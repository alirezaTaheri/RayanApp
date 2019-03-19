package rayan.rayanapp.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    private enum PROTOCOL{
        HTTP,
        UDP,
        MQTT,
        STANDALONE
    }
    private String currentSSID;
    private boolean mqttConnected;
    private Status status;
    private List<Device> devices;
    private List<List<PROTOCOL>> communicationRoutes;


    public MessageTransmissionDecider(Context context, List<Device> devices) {
        communicationRoutes = new ArrayList<>();
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
            List<PROTOCOL> protocols = new ArrayList<>();
            Log.e("I am MTD:","Checking This Device: " + devices.get(a));
            switch (status){
                case WIFI:
                    if (devices.get(a).getSsid().equals(currentSSID) && devices.get(a).getIp() != null) {
                        Log.e("I am MTD:","WIFIWIFIWIFI "+ status);
                        protocols.add(PROTOCOL.UDP);
                        protocols.add(PROTOCOL.HTTP);
                        Log.e("I am MTD:","computeCommunicationRoutes connected to same ssid ");
                    }
                    if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection.getValue() != null && MainActivityViewModel.connection.getValue().isConnected()){
                        protocols.add(PROTOCOL.MQTT);
                        Log.e("I am MTD:","computeCommunicationRoutes in wifi mode mqttconnected true ");
                    }
                    Log.e("I am MTD:","computeCommunicationRoutes in wifi mode: ");
                    break;
                case MOBILE:
                    if (mqttConnected && devices.get(a).getTopic()!= null && MainActivityViewModel.connection != null && MainActivityViewModel.connection.getValue().isConnected()){
                        protocols.add(PROTOCOL.MQTT);
                        Log.e("I am MTD:","mqttConnectedMQTTMQTTMQTT ");
                    }
                    Log.e("I am MTD:","computeCommunicationRoutes ");
                    break;
                case NOT_CONNECTED:
                    if (!devices.get(a).getSsid().equals(AppConstants.UNKNOWN_SSID))
                        protocols.add(PROTOCOL.STANDALONE);
                    Log.e("I am MTD:","STANDALONESTANDALONEcomputeCommunicationRoutes ");
                    break;
            }
            communicationRoutes.add(protocols);
        }
        Log.e("I am MTD:","Finishing Computing: ");
    }

    public String sendMessage(Device device){
        Log.e("I am MTD", "MTD Communication Routes: " + communicationRoutes);
        for (int a = 0;a<devices.size();a++)
            if (device.getChipId().equals(devices.get(a).getChipId())) {
                if (communicationRoutes.get(a).size() == 0)
                    return "NONE";
                else return communicationRoutes.get(a).get(0).toString();
            }
        return null;
    }


}
