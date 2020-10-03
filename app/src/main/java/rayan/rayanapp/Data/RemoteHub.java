package rayan.rayanapp.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;

//@SuppressLint("ParcelCreator")
@Entity
public class RemoteHub extends BaseDevice implements Parcelable {
    @SerializedName("chip_id")
    private String chipId;
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String username, password;
    private String name,version;
    @SerializedName("topic")
    @Expose
    @Embedded(prefix = "topic_")
    private Topic topic;
    @SerializedName("ap_ssid")
    private String ssid;
    @SerializedName("ap_mac")
    private String mac;
    @SerializedName("user_id")
    private String creatorId;
    @SerializedName("group_id")
    private String groupId;
    private boolean accessible,visibility;
    private String secret,ip,statusWord,header,style;
    @SerializedName("device_pass")
    @Expose
    private String devicePassword;
    @Ignore
    private List<Remote> remotes;

    public RemoteHub(){}

    public RemoteHub(RemoteHub remoteHub) {
        super(remoteHub);
        this.chipId = remoteHub.getChipId();
        this.id = remoteHub.getId();
        this.name = remoteHub.getName();
        this.version = remoteHub.getVersion();
        this.topic = remoteHub.getTopic();
        this.ssid = remoteHub.getSsid();
        this.mac = remoteHub.getMac();
        this.creatorId = remoteHub.getCreatorId();
        this.groupId = remoteHub.getGroupId();
        this.accessible = remoteHub.isAccessible();
        this.visibility = remoteHub.isVisibility();
        this.secret = remoteHub.getSecret();
        this.ip = remoteHub.getIp();
        this.statusWord = remoteHub.getStatusWord();
        this.header = remoteHub.getHeader();
        this.style = remoteHub.getStyle();
        this.username = remoteHub.getUsername();
        this.password = remoteHub.getPassword();
        this.devicePassword = remoteHub.getDevicePassword();
    }

    protected RemoteHub(Parcel in) {
        chipId = in.readString();
        id = in.readString();
        username = in.readString();
        password = in.readString();
        name = in.readString();
        version = in.readString();
        topic = in.readParcelable(Topic.class.getClassLoader());
        ssid = in.readString();
        mac = in.readString();
        creatorId = in.readString();
        groupId = in.readString();
        accessible = in.readByte() != 0;
        visibility = in.readByte() != 0;
        secret = in.readString();
        ip = in.readString();
        statusWord = in.readString();
        header = in.readString();
        style = in.readString();
        devicePassword = in.readString();
        remotes = in.createTypedArrayList(Remote.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chipId);
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(version);
        dest.writeParcelable(topic, flags);
        dest.writeString(ssid);
        dest.writeString(mac);
        dest.writeString(creatorId);
        dest.writeString(groupId);
        dest.writeByte((byte) (accessible ? 1 : 0));
        dest.writeByte((byte) (visibility ? 1 : 0));
        dest.writeString(secret);
        dest.writeString(ip);
        dest.writeString(statusWord);
        dest.writeString(header);
        dest.writeString(style);
        dest.writeString(devicePassword);
        dest.writeTypedList(remotes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemoteHub> CREATOR = new Creator<RemoteHub>() {
        @Override
        public RemoteHub createFromParcel(Parcel in) {
            return new RemoteHub(in);
        }

        @Override
        public RemoteHub[] newArray(int size) {
            return new RemoteHub[size];
        }
    };

    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatusWord() {
        return statusWord;
    }

    public void setStatusWord(String statusWord) {
        this.statusWord = statusWord;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }

    public List<Remote> getRemotes() {
        return remotes;
    }

    public void setRemotes(List<Remote> remotes) {
        this.remotes = remotes;
    }

    @Override
    public String toString() {
        return "RemoteHub{" +
                "chipId='" + chipId + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", topic=" + topic +
                ", ssid='" + ssid + '\'' +
                ", mac='" + mac + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", accessible=" + accessible +
                ", visibility=" + visibility +
                ", secret='" + secret + '\'' +
                ", ip='" + ip + '\'' +
                ", statusWord='" + statusWord + '\'' +
                ", header='" + header + '\'' +
                ", style='" + style + '\'' +
                ", devicePassword='" + devicePassword + '\'' +
                ", remotes=" + remotes +
                '}';
    }
}
