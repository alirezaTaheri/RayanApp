package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class SetPrimaryConfigRequest extends BaseRequest{
    private String ssid,pwd,hostname,mqtthost,mqttport,mqttuser,mqttpass,mqtttopic,hpwd, style, secret;

    public SetPrimaryConfigRequest() {
    }

    public SetPrimaryConfigRequest(String ssid, String pwd, String hostname, String mqtthost, String mqttport, String mqtttopic, String mqttuser, String mqttpass, String hpwd, String style, String secret) {
        this.ssid = ssid;
        this.pwd = pwd;
        this.hostname = hostname;
        this.mqtthost = mqtthost;
        this.mqttport = mqttport;
        this.mqtttopic = mqtttopic;
        this.mqttuser = mqttuser;
        this.mqttpass = mqttpass;
        this.hpwd = hpwd;
        this.style = style;
        this.secret = secret;
        this.setCmd(AppConstants.NEW_DEVICE_config);
    }
}
