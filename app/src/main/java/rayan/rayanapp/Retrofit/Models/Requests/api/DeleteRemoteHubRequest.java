package rayan.rayanapp.Retrofit.Models.Requests.api;

public class DeleteRemoteHubRequest {
    private String remote_hub_id;
    private String group_id;

    public DeleteRemoteHubRequest(String remote_hub_id, String group_id) {
        this.remote_hub_id = remote_hub_id;
        this.group_id = group_id;
    }
}
