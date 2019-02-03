package rayan.rayanapp.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Retrofit.Models.Topic;

@Entity
public class Device {
    @PrimaryKey
    @NonNull
    @SerializedName("chip_id")
    @Expose
    private String chipId;
    @SerializedName("device_name")
    @Expose
    private String name1;
    private String name2;
    private String pin1 = "off";
    private String pin2 = "off";
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("device_type")
    @Expose
    private String type;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("topic")
    @Expose
    @Embedded(prefix = "topic_")
    private Topic topic;
    private String groupId;
    private String style;
    @SerializedName("ssid")
    @Expose
    private String ssid;
    private String ip;
    private String password;
    private boolean ready4Mqtt;
    private boolean favorite;

    public Device(@NonNull String chipId, String name1, String id, String type, String username, Topic topic, String groupId) {
        this.chipId = chipId;
        this.name1 = name1;
        this.id = id;
        this.type = type;
        this.username = username;
        this.topic = topic;
        this.groupId = groupId;
        this.favorite = false;
        this.setPin1("off");
        this.setPin2("off");
    }

    @NonNull
    public String getChipId() {
        return chipId;
    }

    public void setChipId(@NonNull String chipId) {
        this.chipId = chipId;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isReady4Mqtt() {
        return ready4Mqtt;
    }

    public void setReady4Mqtt(boolean ready4Mqtt) {
        this.ready4Mqtt = ready4Mqtt;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Device{" +
                "chipId='" + chipId + '\'' +
                ", name1='" + name1 + '\'' +
//                ", name2='" + name2 + '\'' +
                ", pin1='" + pin1 + '\'' +
//                ", pin2='" + pin2 + '\'' +
//                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
//                ", username='" + username + '\'' +
//                ", topic=" + topic +
//                ", groupId='" + groupId + '\'' +
//                ", style='" + style + '\'' +
//                ", ssid='" + ssid + '\'' +
//                ", ip='" + ip + '\'' +
//                ", password='" + password + '\'' +
//                ", ready4Mqtt=" + ready4Mqtt +
                ", favorite=" + favorite +
                '}';
    }
}
