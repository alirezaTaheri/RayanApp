package rayan.rayanapp.Retrofit.Models;

import com.google.gson.annotations.SerializedName;

public class Topic {
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
}
