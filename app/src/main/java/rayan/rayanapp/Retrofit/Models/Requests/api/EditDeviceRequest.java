package rayan.rayanapp.Retrofit.Models.Requests.api;

import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;

public class EditDeviceRequest {
    private String name,type,ap_ssid,sp_mac,group_id,_id,comment,status;
    private boolean favorite;
    private Topic topic;
    private int position, group_position, favorite_position;

    public EditDeviceRequest(String name, String type, String ap_ssid, String sp_mac, String group_id, String _id, String comment, String status) {
        this.name = name;
        this.type = type;
        this.ap_ssid = ap_ssid;
        this.sp_mac = sp_mac;
        this.group_id = group_id;
        this._id = _id;
        this.comment = comment;
        this.status = status;
    }
}
