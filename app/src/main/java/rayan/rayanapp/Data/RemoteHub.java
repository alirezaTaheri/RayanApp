package rayan.rayanapp.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class RemoteHub extends BaseDevice{
    @SerializedName("chip_id")
    private String chipId;
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String name,version,topic;
    @SerializedName("ap_ssid")
    private String ssid;
    @SerializedName("ap_mac")
    private String mac;
    @SerializedName("user_id")
    private String creatorId;
    @SerializedName("group_id")
    private String groupId;
    private boolean accessible,visibility,favorite;

    public RemoteHub() {
    }

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
        this.favorite = remoteHub.isFavorite();
    }

    @NonNull
    public String getChipId() {
        return chipId;
    }

    public void setChipId(@NonNull String chipId) {
        this.chipId = chipId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "RemoteHub{" +
//                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pos='" + getPosition() + '\'' +
                ", grPos='" + getInGroupPosition()+ '\'' ;
//                ", groupId='" + groupId + '\'' +
//                '}';
    }
}
