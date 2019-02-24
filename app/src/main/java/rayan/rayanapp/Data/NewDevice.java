package rayan.rayanapp.Data;

import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;

public class NewDevice {

    private String id;
    private String type;
    private String name;
    private int toggleCount;
    private String ssid;
    private String pwd;
    private Topic topic;
    private String mqttUser;
    private String mqttPass;
    private String hpwd;
    private String username;
    private String password;
    private String groupId;
    private String chip_id;

    public String getChip_id() {
        return chip_id;
    }

    public void setChip_id(String chip_id) {
        this.chip_id = chip_id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getToggleCount() {
        return toggleCount;
    }

    public String getSsid() {
        return ssid;
    }

    public String getPwd() {
        return pwd;
    }

    public Topic getTopic() {
        return topic;
    }

    public String getMqttUser() {
        return mqttUser;
    }

    public String getMqttPass() {
        return mqttPass;
    }

    public String getHpwd() {
        return hpwd;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setToggleCount(int toggleCount) {
        this.toggleCount = toggleCount;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setMqttUser(String mqttUser) {
        this.mqttUser = mqttUser;
    }

    public void setMqttPass(String mqttPass) {
        this.mqttPass = mqttPass;
    }

    public void setHpwd(String hpwd) {
        this.hpwd = hpwd;
    }
}
