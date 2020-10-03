package rayan.rayanapp.Retrofit.Models.Requests.api;

import rayan.rayanapp.Data.RemoteHub;

public class EditRemoteHubRequest {
    private String name,version,ap_ssid,ap_mac,_id;
    private boolean accessible,visible;

    public EditRemoteHubRequest(RemoteHub remoteHub) {
        this.name = remoteHub.getName();
        this.version=remoteHub.getVersion();
        this.ap_ssid=remoteHub.getSsid();
        this.ap_mac=remoteHub.getMac();
        this._id=remoteHub.getId();
        this.visible=remoteHub.isVisibility();
        this.accessible=remoteHub.isAccessible();
    }

    public EditRemoteHubRequest(String name,String version,String ap_ssid,String ap_mac,String _id){
        this.name = name;
        this.version=version;
        this.ap_ssid=ap_ssid;
        this.ap_mac=ap_mac;
        this._id=_id;
    }

}
