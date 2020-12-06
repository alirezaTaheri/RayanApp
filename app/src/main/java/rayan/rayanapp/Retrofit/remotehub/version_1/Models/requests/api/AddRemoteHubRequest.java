package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api;

import rayan.rayanapp.Data.NewDevice;
import rayan.rayanapp.Data.RemoteHub;

public class AddRemoteHubRequest {
    private String username,password,chip_id,name,version,ap_ssid,ap_mac;
    private boolean accessible,visible,favorite;
    public AddRemoteHubRequest(RemoteHub remoteHub) {
        this.name = remoteHub.getName();
        this.version=remoteHub.getVersion();
        this.ap_ssid=remoteHub.getSsid();
        this.ap_mac=remoteHub.getMac();
        this.visible=remoteHub.isVisibility();
        this.accessible=remoteHub.isAccessible();
        this.username = remoteHub.getUsername();
        this.password = remoteHub.getPassword();
        this.favorite = remoteHub.isFavorite();
        this.chip_id = remoteHub.getChipId();
    }

    public AddRemoteHubRequest(NewDevice newDevice){
        this.name = newDevice.getName();
        this.version=newDevice.getVersion();
        this.ap_ssid=newDevice.getSsid();
        this.ap_mac=newDevice.getMac();
        this.visible=newDevice.isVisible();
        this.accessible=newDevice.isAccessible();
        this.username = newDevice.getUsername();
        this.password = newDevice.getPassword();
        this.favorite = newDevice.isFavorite();
        this.chip_id = newDevice.getChip_id();
    }

}
