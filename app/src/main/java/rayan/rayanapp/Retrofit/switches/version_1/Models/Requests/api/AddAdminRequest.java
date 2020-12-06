package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

import java.util.List;

public class AddAdminRequest {
    private List<String> users_id;
    private String group_id;

    public AddAdminRequest(List<String> users_id, String group_id) {
        this.users_id = users_id;
        this.group_id = group_id;
    }
}
