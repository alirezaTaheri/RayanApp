package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

public class EditDeviceTopicRequest {
    private String device_id;
    private String group_id;
    private String name;
    private String type,ssid;

    public EditDeviceTopicRequest(String user_id, String group_id, String name, String type, String ssid) {
        this.device_id = user_id;
        this.group_id = group_id;
        this.name = name;
        this.type = type;
        this.ssid = ssid;
    }

}
