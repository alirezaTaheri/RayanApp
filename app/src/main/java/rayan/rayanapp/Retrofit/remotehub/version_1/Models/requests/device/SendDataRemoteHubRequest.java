package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device;

import rayan.rayanapp.App.RayanApplication;

public class SendDataRemoteHubRequest extends BaseRequest {
    String raw_data;

    public SendDataRemoteHubRequest(String STWORD, String msg) {
        super(STWORD);
        setSrc(RayanApplication.getPref().getId());
        this.raw_data = msg;
    }

}
