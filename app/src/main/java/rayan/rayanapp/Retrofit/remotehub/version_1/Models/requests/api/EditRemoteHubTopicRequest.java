package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api;

public class EditRemoteHubTopicRequest {
    private String remote_hub_id;
    private String group_id;
    private String name;
    private String type,ssid;

    public EditRemoteHubTopicRequest(String user_id, String group_id, String name, String type, String ssid) {
        this.remote_hub_id = user_id;
        this.group_id = group_id;
        this.name = name;
        this.type = type;
        this.ssid = ssid;
    }

}
