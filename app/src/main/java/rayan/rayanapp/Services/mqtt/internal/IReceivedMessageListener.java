package rayan.rayanapp.Services.mqtt.internal;


import rayan.rayanapp.Services.mqtt.model.ReceivedMessage;

public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}