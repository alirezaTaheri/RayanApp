package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api;

import rayan.rayanapp.Data.Remote;

public class AddRemoteRequest {
    private Remote remote;
    private String remoteHub_id;

    public AddRemoteRequest(Remote remote) {
        this.remote = remote;
        this.remoteHub_id = remote.getRemoteHubId();
    }
}
