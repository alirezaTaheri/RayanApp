package rayan.rayanapp.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Topic;
import rayan.rayanapp.Util.dataConverter.StringListConverter;

@Entity
public class Remote extends BaseDevice implements Parcelable{
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String name;
    @SerializedName("topic")
    @Expose
    @Embedded(prefix = "topic_")
    private Topic topic;
    @SerializedName("user_id")
    private String creatorId;
    @SerializedName("main_data_ids")
    @Expose
    @TypeConverters(StringListConverter.class)
    private List<String> remoteDatas;
    @SerializedName("remote_hub_id")
    private String remoteHubId;
    private String type;
    private String groupId;
    private boolean accessible,learned,visibility;
    private String model;
    private String brand;

    public Remote() {
    }

    public Remote(Remote remote) {
        super(remote);
        this.id = remote.getId();
        this.name = remote.getName();
        this.topic = remote.getTopic();
        this.creatorId = remote.getCreatorId();
        this.remoteDatas = remote.getRemoteDatas();
        this.remoteHubId = remote.getRemoteHubId();
        this.type = remote.getType();
        this.groupId = remote.getGroupId();
        this.accessible = remote.isAccessible();
        this.learned = remote.isLearned();
        this.visibility = remote.isVisibility();
        this.brand = remote.getBrand();
        this.model= remote.getModel();
    }

    protected Remote(Parcel in) {
        id = in.readString();
        name = in.readString();
        topic = in.readParcelable(Topic.class.getClassLoader());
        creatorId = in.readString();
        remoteDatas = in.createStringArrayList();
        remoteHubId = in.readString();
        type = in.readString();
        groupId = in.readString();
        accessible = in.readByte() != 0;
        learned = in.readByte() != 0;
        visibility = in.readByte() != 0;
        model = in.readString();
        brand = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(topic, flags);
        dest.writeString(creatorId);
        dest.writeStringList(remoteDatas);
        dest.writeString(remoteHubId);
        dest.writeString(type);
        dest.writeString(groupId);
        dest.writeByte((byte) (accessible ? 1 : 0));
        dest.writeByte((byte) (learned ? 1 : 0));
        dest.writeByte((byte) (visibility ? 1 : 0));
        dest.writeString(model);
        dest.writeString(brand);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Remote> CREATOR = new Creator<Remote>() {
        @Override
        public Remote createFromParcel(Parcel in) {
            return new Remote(in);
        }

        @Override
        public Remote[] newArray(int size) {
            return new Remote[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getRemoteDatas() {
        return remoteDatas;
    }

    public void setRemoteDatas(List<String> remoteDatas) {
        this.remoteDatas = remoteDatas;
    }

    public String getRemoteHubId() {
        return remoteHubId;
    }

    public void setRemoteHubId(String remoteHubId) {
        this.remoteHubId = remoteHubId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        this.learned = learned;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Remote{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topic=" + topic +
                ", creatorId='" + creatorId + '\'' +
                ", remoteDatas=" + remoteDatas +
                ", remoteHubId='" + remoteHubId + '\'' +
                ", type='" + type + '\'' +
                ", groupId='" + groupId + '\'' +
                ", accessible=" + accessible +
                ", learned=" + learned +
                ", visibility=" + visibility +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
