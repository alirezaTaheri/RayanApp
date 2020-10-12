package rayan.rayanapp.Retrofit.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class SetPrimaryConfigRequest extends BaseRequest{
    private String ap_ssid, ap_pass, node_name, mqtt_host, mqtt_port, mqtt_username, mqtt_pass, mqtt_topic, node_style, secret;

    public SetPrimaryConfigRequest() {
    }

    public SetPrimaryConfigRequest(String ssid, String pwd, String hostname, String mqtthost, String mqttport, String mqtttopic, String mqttuser, String mqttpass, String style, String secret) {
        this.ap_ssid = ssid;
        this.ap_pass = pwd;
        this.node_name = hostname;
        this.mqtt_host = mqtthost;
        this.mqtt_port = mqttport;
        this.mqtt_topic = mqtttopic;
        this.mqtt_username = mqttuser;
        this.mqtt_pass = mqttpass;
        this.node_style = style;
        this.secret = secret;
        this.setCmd(AppConstants.NEW_DEVICE_config);
    }
}
