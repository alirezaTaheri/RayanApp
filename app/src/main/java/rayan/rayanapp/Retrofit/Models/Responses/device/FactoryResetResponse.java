package rayan.rayanapp.Retrofit.Models.Responses.device;

public class FactoryResetResponse extends DeviceBaseResponse{
    private String stword;

    public String getStword() {
        return stword;
    }

    @Override
    public String toString() {
        return "Ready4SettingsResponse{" +
                "stword='" + stword + '\'' +
                ", src='" + src + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
