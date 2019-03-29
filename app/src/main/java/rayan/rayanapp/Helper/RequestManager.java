package rayan.rayanapp.Helper;

import java.util.HashMap;
import java.util.Map;

import rayan.rayanapp.Data.Device;

public class RequestManager {
    private Map<String, String> lastRequest;

    public RequestManager() {
        lastRequest = new HashMap<>();
    }
    private void registerRequest(Device device, String message){
        lastRequest.put(device.getChipId(), message);
    }
    private void getLastRequet(Device device){
        lastRequest.get(device.getChipId());
    }
}
