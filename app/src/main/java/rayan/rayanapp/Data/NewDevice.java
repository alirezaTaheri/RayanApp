package rayan.rayanapp.Data;

import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;

public class NewDevice {

    private String id,type,name,ssid,accessPointName;
    private int toggleCount;
    private String pwd;
    private Topic topic;
    private String mqttUser;
    private String mqttPass;
    private String hpwd;
    private String username;
    private String password;
    private Group group;
    private String chip_id;
    private String pin1;
    private String pin2;
    private NodeStatus status;
    private int networkId;
    public enum NodeStatus {
            IDLE,
            NEW
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public String getPin1() {
        return pin1;
    }

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public String getPin2() {
        return pin2;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    public String getChip_id() {
        return chip_id;
    }

    public void setChip_id(String chip_id) {
        this.chip_id = chip_id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NewDevice{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", ssid='" + ssid + '\'' +
                ", accessPointName='" + accessPointName + '\'' +
                ", toggleCount=" + toggleCount +
                ", pwd='" + pwd + '\'' +
                ", topic=" + topic +
                ", mqttUser='" + mqttUser + '\'' +
                ", mqttPass='" + mqttPass + '\'' +
                ", hpwd='" + hpwd + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", group=" + group +
                ", chip_id='" + chip_id + '\'' +
                ", pin1='" + pin1 + '\'' +
                ", pin2='" + pin2 + '\'' +
                ", status=" + status +
                '}';
    }
}
