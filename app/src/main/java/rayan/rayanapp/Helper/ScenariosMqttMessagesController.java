package rayan.rayanapp.Helper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScenariosMqttMessagesController {
    Map<String, Boolean> devicesResponse;
    Map<String, JSONObject> devicesLastMessage;
    public ScenariosMqttMessagesController() {
        devicesResponse = new HashMap<>();
        devicesLastMessage = new HashMap<>();
    }

    public void messageSent(String chipId){
        devicesResponse.put(chipId, false);
    }

    public void responseReceived(String chipId, JSONObject message){
        devicesResponse.put(chipId, true);
        devicesLastMessage.put(chipId, message);
    }

    public JSONObject getLastMessageOfDevice(String chipId){
        return devicesLastMessage.get(chipId);
    }

    public boolean isReceivedResponse(String chipId){
        return devicesResponse.get(chipId);
    }

}
