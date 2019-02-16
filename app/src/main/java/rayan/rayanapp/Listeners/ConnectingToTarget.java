package rayan.rayanapp.Listeners;

public interface ConnectingToTarget{
    void connecting(String targerSSID);
    void successful();
    void failure();
    void searching();
    void idle();
    void connectToSame();
}
