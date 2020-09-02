package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceToGroupRequest {
    private List<String> devices_id;
    private String group_id;

    public AddDeviceToGroupRequest(String user_id, String group_id) {
        devices_id = new ArrayList<>();
        devices_id.add(user_id);
        this.group_id = group_id;
    }
}
