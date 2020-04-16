package rayan.rayanapp.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;
import rayan.rayanapp.Util.AppConstants;

@Entity
public class Remote  extends BaseDevice{
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String name,topic;
    @SerializedName("user_id")
    private String creatorId;
    @SerializedName("main_data_id")
    private String remoteDataId;
    @SerializedName("remote_hub_id")
    private String remoteHubId;
    private String type;
    private boolean accessible,learned,visibility,favorite;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getRemoteDataId() {
        return remoteDataId;
    }

    public void setRemoteDataId(String remoteDataId) {
        this.remoteDataId = remoteDataId;
    }

    public String getRemoteHubId() {
        return remoteHubId;
    }

    public void setRemoteHubId(String remoteHubId) {
        this.remoteHubId = remoteHubId;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "Remote{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", remoteDataId='" + remoteDataId + '\'' +
                ", remoteHubId='" + remoteHubId + '\'' +
                ", accessible=" + accessible +
                ", learned=" + learned +
                ", visibility=" + visibility +
                ", favorite=" + favorite +
                '}';
    }
}
