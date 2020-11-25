package rayan.rayanapp.Data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class LocallyChange {
    @PrimaryKey
    @NonNull
    private String id;
    private String jsonRequest;
    private String type;
    private String chipId;
    public enum Type {
        POSITION,
        FAVOURITE,
        NAME_API,
        NAME_DEVICE
    }

    public LocallyChange() {
    }

    public LocallyChange(@NonNull String id, String jsonRequest, Type type, String chipId) {
        this.id = id;
        this.jsonRequest = jsonRequest;
        this.type = type.toString();
        this.chipId = chipId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getJsonRequest() {
        return jsonRequest;
    }

    public String getType() {
        return type;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setJsonRequest(String jsonRequest) {
        this.jsonRequest = jsonRequest;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChipId() {
        return chipId;
    }

    public void setChipId(String chipId) {
        this.chipId = chipId;
    }

    @Override
    public String toString() {
        return "LocallyChange{" +
                "id='" + id + '\'' +
                ", jsonRequest='" + jsonRequest + '\'' +
                ", type='" + type + '\'' +
                ", chipId='" + chipId + '\'' +
                '}';
    }
}
