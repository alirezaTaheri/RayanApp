package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

import rayan.rayanapp.Data.Remote;

public class AddRemoteRequest {
    private Remote remote;
    private String remoteHub_id;

    public AddRemoteRequest(Remote remote) {
        this.remote = remote;
        this.remoteHub_id = remote.getRemoteHubId();
    }
}
