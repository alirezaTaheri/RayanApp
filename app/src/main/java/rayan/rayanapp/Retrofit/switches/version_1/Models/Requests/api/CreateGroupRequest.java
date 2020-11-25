package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

import java.util.List;

public class CreateGroupRequest {
    private String name;
    private List<String> users_id;
    private List<String> mobiles;

    public CreateGroupRequest(String name, List<String> mobiles) {
        this.name = name;
        this.mobiles = mobiles;
    }

    public CreateGroupRequest(String name, List<String> users_id, List<String> mobiles) {
        this.name = name;
        this.users_id = users_id;
        this.mobiles = mobiles;
    }
}
