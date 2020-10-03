package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.ArrayList;
import java.util.List;

public class AddRemoteHubToGroupRequest {
    private List<String> remote_hubs_id;
    private String group_id;

    public AddRemoteHubToGroupRequest(String remote_hubs_id, String group_id) {
        this.remote_hubs_id = new ArrayList<>();
        this.remote_hubs_id.add(remote_hubs_id);
        this.group_id = group_id;
    }
}
