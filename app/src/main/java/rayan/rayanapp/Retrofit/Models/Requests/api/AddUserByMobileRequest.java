package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

public class AddUserByMobileRequest {
    private List<String> mobiles;
    private String group_id;

    public AddUserByMobileRequest(List<String> mobiles, String group_id) {
        this.mobiles = mobiles;
        this.group_id = group_id;
    }
}
