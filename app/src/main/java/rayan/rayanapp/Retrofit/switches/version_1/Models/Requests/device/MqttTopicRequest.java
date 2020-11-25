package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.device;

import rayan.rayanapp.Util.AppConstants;

public class MqttTopicRequest extends BaseRequest{
    private String host,user,pass,topic,port;

    public MqttTopicRequest(String host, String user, String pass, String topic, String port) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.topic = topic;
        this.port = port;
        this.setCmd(AppConstants.SET_TOPIC_MQTT);
    }
}
