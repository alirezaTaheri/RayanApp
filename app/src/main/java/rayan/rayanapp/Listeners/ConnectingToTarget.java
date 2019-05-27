package rayan.rayanapp.Listeners;

import rayan.rayanapp.Data.AccessPoint;

public interface ConnectingToTarget{
    void connecting(AccessPoint targerSSID);
    void successful(String targetSSID);
    void failure();
    void searching();
    void idle();
    void connectToSame(String targetSSID);
}
