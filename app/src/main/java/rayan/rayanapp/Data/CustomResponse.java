package rayan.rayanapp.Data;

import okhttp3.ResponseBody;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.device.Ready4SettingsResponse;
import retrofit2.Response;

public class CustomResponse {
    public ResponseBody responseBody;
    public Response<Ready4SettingsResponse> response;

    @Override
    public String toString() {
        return "CustomResponse{" +
                "responseBody=" + responseBody +
                ", response=" + response +
                '}';
    }
}
