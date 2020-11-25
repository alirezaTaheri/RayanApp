package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

public class EditDeviceRequest {
    private String user_id;
    private String group_id;
    private String name;
    private String type,ssid;

    public EditDeviceRequest(String user_id, String group_id, String name, String type, String ssid) {
        this.user_id = user_id;
        this.group_id = group_id;
        this.name = name;
        this.type = type;
        this.ssid = ssid;
    }

}
