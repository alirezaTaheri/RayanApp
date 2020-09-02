package rayan.rayanapp.Retrofit.Models.Requests.api;

public class DeleteDeviceRequest {
    private String device_id;
    private String group_id;

    public DeleteDeviceRequest(String user_id, String group_id) {
        this.device_id = user_id;
        this.group_id = group_id;
    }
}
