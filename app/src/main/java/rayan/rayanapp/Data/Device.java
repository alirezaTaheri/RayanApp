package rayan.rayanapp.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Util.AppConstants;

@Entity
public class Device extends BaseDevice implements Parcelable {
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
    @SerializedName("device_pass")
    @Expose
    private String devicePassword;
    private String ip;
    private String password;
    private boolean locallyAccessibility;
    private boolean onlineAccessibility;
    private String statusWord;
    private String secret;
    private boolean hidden;
    private String header = AppConstants.UNKNOWN_HEADER;

    public Device() {
    }

    public Device(Device device) {
        super(device);
        this.pin1 = device.getPin1();
        this.chipId = device.getChipId();
        this.name1 = device.getName1();
        this.id = device.getId();
        this.type = device.getType();
        this.style = device.getStyle();
        this.ssid = device.getSsid();
        this.username = device.getUsername();
        this.topic = device.getTopic();
        this.groupId = device.getGroupId();
        this.pin1 = device.getPin1();
        this.pin2 = device.getPin2();
        this.secret = device.getSecret();
        this.ip = device.getIp();
        this.devicePassword = device.getDevicePassword();
        this.statusWord = device.getStatusWord();
        this.hidden = device.isHidden();
        this.locallyAccessibility = device.isLocallyAccessibility();
        this.onlineAccessibility = device.isOnlineAccessibility();
        this.header = device.getHeader();
    }

    public String getHeader() {
        return header;
    }


    public Device(@NonNull String chipId, String name1, String id, String type, String username, Topic topic, String groupId, String secret) {
        this.chipId = chipId;
        this.name1 = name1;
        this.id = id;
        this.type = type;
        this.username = username;
        this.topic = topic;
        this.groupId = groupId;
        this.setPin1("off");
        this.setPin2("off");
        this.secret = secret;
        this.statusWord = "0";
    }


    protected Device(Parcel in) {
        chipId = in.readString();
        name1 = in.readString();
        name2 = in.readString();
        pin1 = in.readString();
        pin2 = in.readString();
        id = in.readString();
        type = in.readString();
        username = in.readString();
        topic = in.readParcelable(Topic.class.getClassLoader());
        groupId = in.readString();
        style = in.readString();
        ssid = in.readString();
        devicePassword = in.readString();
        ip = in.readString();
        password = in.readString();
        locallyAccessibility = in.readByte() != 0;
        onlineAccessibility = in.readByte() != 0;
        statusWord = in.readString();
        secret = in.readString();
        hidden = in.readByte() != 0;
        header = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chipId);
        dest.writeString(name1);
        dest.writeString(name2);
        dest.writeString(pin1);
        dest.writeString(pin2);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(username);
        dest.writeParcelable(topic, flags);
        dest.writeString(groupId);
        dest.writeString(style);
        dest.writeString(ssid);
        dest.writeString(devicePassword);
        dest.writeString(ip);
        dest.writeString(password);
        dest.writeByte((byte) (locallyAccessibility ? 1 : 0));
        dest.writeByte((byte) (onlineAccessibility ? 1 : 0));
        dest.writeString(statusWord);
        dest.writeString(secret);
        dest.writeByte((byte) (hidden ? 1 : 0));
        dest.writeString(header);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
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

    public boolean isLocallyAccessibility() {
        return locallyAccessibility && getIp()!=null;
    }

    public String getStatusWord() {
        return statusWord;
    }

    public void setStatusWord(String statusWord) {
        this.statusWord = statusWord;
    }

    public void setLocallyAccessibility(boolean locallyAccessibility) {
        this.locallyAccessibility = locallyAccessibility;
    }

    public boolean isOnlineAccessibility() {
        return onlineAccessibility ;
//        return true ;
    }

    public void setOnlineAccessibility(boolean onlineAccessibility) {
        this.onlineAccessibility = onlineAccessibility;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "{" +
//                "chipId='" + chipId + '\'' +
                "baseType='" + getDeviceType() + '\'' +
                "posin='" + getPosition() + '\'' +
                "grouppos='" + getInGroupPosition() + '\'' +
                ", name1='" + name1 + '\'' +
////                ", name2='" + name2 + '\'' +
//                ", pin1='" + pin1 + '\'' +
//                ", pin2='" + pin2 + '\'' +
//                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
//                ", username='" + username + '\'' +
//                ", topic=" + topic +
//                ", groupId='" + groupId + '\'' +
//                ", style='" + style + '\'' +
//                ", ssid='" + ssid + '\'' +
//                ", devicePassword='" + devicePassword + '\'' +
                ", ip='" + ip + '\'' +
//                ", password='" + password + '\'' +
//                ", favorite=" + favorite +
//                ", locallyAccessibility=" + locallyAccessibility +
//                ", onlineAccessibility=" + onlineAccessibility +
//                ", statusWord='" + statusWord + '\'' +
//                ", secret='" + secret + '\'' +
//                ", hidden=" + hidden +
//                ", Header=" + header +
                '}';
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public boolean isReady4Mqtt(){
        return topic.getTopic() != null;
    }
}
