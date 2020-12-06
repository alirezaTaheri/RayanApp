package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api;

public class CreateTopicRemoteHubRequest {
    private String remote_hub_id;
    private String group_id;
    private String code_10;
    private String domain;

    public CreateTopicRemoteHubRequest(String remote_hub_id, String group_id, String code_10, String domain) {
        this.remote_hub_id = remote_hub_id;
        this.group_id = group_id;
        this.code_10 = code_10;
        this.domain = domain;
    }
}
