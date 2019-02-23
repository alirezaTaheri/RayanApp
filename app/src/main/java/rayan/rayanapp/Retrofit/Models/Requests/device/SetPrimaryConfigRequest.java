package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class SetPrimaryConfigRequest extends BaseRequest{
    private String ssid;
    private String pwd;
    private String hostname;
    private String mqtthost;
    private String mqttport;
    private String mqtttopic;
    private String mqttuser;
    private String mqttpass;
    private String hpwd;

    public SetPrimaryConfigRequest() {
    }

    public SetPrimaryConfigRequest(String ssid, String pwd, String hostname, String mqtthost, String mqttport, String mqtttopic, String mqttuser, String mqttpass, String hpwd) {
        this.ssid = ssid;
        this.pwd = pwd;
        this.hostname = hostname;
        this.mqtthost = mqtthost;
        this.mqttport = mqttport;
        this.mqtttopic = mqtttopic;
        this.mqttuser = mqttuser;
        this.mqttpass = mqttpass;
        this.hpwd = hpwd;
        this.setCmd(AppConstants.NEW_DEVICE_config);
    }
}
