package rayan.rayanapp.Listeners;

public interface MqttStatus{
    void mqttConnecting();
    void mqttConnected();
    void mqttDisconnected();
    void mqttError();
}