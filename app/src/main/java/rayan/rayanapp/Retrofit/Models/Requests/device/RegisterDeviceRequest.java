package rayan.rayanapp.Retrofit.Models.Requests.device;

public class RegisterDeviceRequest {
    private String username;
    private String device_name;
    private String device_type;

    public RegisterDeviceRequest(String username, String device_name, String device_type) {
        this.username = username;
        this.device_name = device_name;
        this.device_type = device_type;
    }

}
