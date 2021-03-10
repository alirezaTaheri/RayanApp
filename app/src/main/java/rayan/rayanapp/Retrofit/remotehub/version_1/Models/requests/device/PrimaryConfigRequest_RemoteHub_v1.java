package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device;

import rayan.rayanapp.Retrofit.switches.version_2.Models.requests.device.BaseRequest;
import rayan.rayanapp.Util.AppConstants;

public class PrimaryConfigRequest_RemoteHub_v1 extends BaseRequest{
    private String ap_ssid, ap_pass, node_name, mqtt_host, mqtt_tcp_port, mqtt_ssl_port, mqtt_username, mqtt_pass, mqtt_topic, node_style, secret;

    public PrimaryConfigRequest_RemoteHub_v1() {
    }

    public PrimaryConfigRequest_RemoteHub_v1(String ssid, String pwd, String hostname, String mqtthost, String mqttSslPort, String mqttTcpPort, String mqtttopic, String mqttuser, String mqttpass, String style, String secret) {
        this.ap_ssid = ssid;
        this.ap_pass = pwd;
        this.node_name = hostname;
        this.mqtt_host = mqtthost;
        this.mqtt_ssl_port = mqttSslPort;
        this.mqtt_tcp_port = mqttTcpPort;
        this.mqtt_topic = mqtttopic;
        this.mqtt_username = mqttuser;
        this.mqtt_pass = mqttpass;
        this.node_style = style;
        this.secret = secret;
        this.setCmd(AppConstants.NEW_DEVICE_config);
    }
}