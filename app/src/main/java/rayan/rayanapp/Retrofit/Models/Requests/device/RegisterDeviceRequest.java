package rayan.rayanapp.Retrofit.Models.Requests.device;

public class RegisterDeviceRequest {
    private String username;
    private String device_name;
    private String device_type;
    private String ap_ssid, ap_mac;

    public RegisterDeviceRequest(String username, String device_name, String device_type) {
        this.username = username;
        this.device_name = device_name;
        this.device_type = device_type;
    }

    public String getUsername() {
        return username;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getDevice_type() {
        return device_type;
    }
}
