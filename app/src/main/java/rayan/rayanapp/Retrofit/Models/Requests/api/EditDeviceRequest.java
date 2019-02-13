package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

public class EditDeviceRequest {
    private String user_id;
    private String group_id;
    private String name;
    private String type;

    public EditDeviceRequest(String user_id, String group_id, String name, String type) {
        this.user_id = user_id;
        this.group_id = group_id;
        this.name = name;
        this.type = type;
    }

}
