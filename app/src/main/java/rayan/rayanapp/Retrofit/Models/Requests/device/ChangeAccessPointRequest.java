package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class ChangeAccessPointRequest extends BaseRequest{
    private String hname,pwd,ssid,style;

    public ChangeAccessPointRequest(String hname, String pwd, String ssid, String style) {
        this.hname = hname;
        this.pwd = pwd;
        this.ssid = ssid;
        this.style = style;
        this.setCmd(AppConstants.CHANGE_WIFI);
    }
}
