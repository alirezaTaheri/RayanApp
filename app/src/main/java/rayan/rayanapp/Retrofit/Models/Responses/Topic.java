package rayan.rayanapp.Retrofit.Models.Responses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Topic implements Parcelable {
    @SerializedName("topic")
    private String topic;
    @SerializedName("type")
    private String type;
    @SerializedName("domain")
    private String domain;
    @SerializedName("name")
    private String name;
    @SerializedName("_id")
    private String id;
    public Topic(){}
    protected Topic(Parcel in) {
        topic = in.readString();
        type = in.readString();
        domain = in.readString();
        name = in.readString();
        id = in.readString();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topic='" + topic + '\'' +
                ", type='" + type + '\'' +
                ", domain='" + domain + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topic);
        dest.writeString(type);
        dest.writeString(domain);
        dest.writeString(name);
        dest.writeString(id);
    }
}
