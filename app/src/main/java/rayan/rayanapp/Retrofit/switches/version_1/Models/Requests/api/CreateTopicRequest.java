package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

public class CreateTopicRequest {
    private String user_id;
    private String group_id;
    private String code_10;
    private String domain;

    public CreateTopicRequest(String user_id, String group_id, String code_10, String domain) {
        this.user_id = user_id;
        this.group_id = group_id;
        this.code_10 = code_10;
        this.domain = domain;
    }
}
