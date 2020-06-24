package rayan.rayanapp.Retrofit.Models.Requests.api;

public class EditRemoteHubRequest {
    private String name,version,ap_ssid,ap_mac,_id;
    private boolean accessible,visibility;
    public EditRemoteHubRequest(String name,String version,String ap_ssid,String ap_mac,String _id,boolean accessible,boolean visibility) {
        this.name = name;
        this.version=version;
        this.ap_ssid=ap_ssid;
        this.ap_mac=ap_mac;
        this._id=_id;
        this.visibility=visibility;
        this.accessible=accessible;
    }

}
