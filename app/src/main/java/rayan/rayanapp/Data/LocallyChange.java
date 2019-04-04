package rayan.rayanapp.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LocallyChange {
    @PrimaryKey
    @NonNull
    private String id;
    private String jsonRequest;
    private String type;
    private String ip;
    public enum Type {
        POSITION,
        FAVOURITE,
        NAME_API,
        NAME_DEVICE
    }

    public LocallyChange() {
    }

    public LocallyChange(@NonNull String id, String jsonRequest, Type type, String ip) {
        this.id = id;
        this.jsonRequest = jsonRequest;
        this.type = type.toString();
        this.ip = ip;
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

    public String getIp() {
        return ip;
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

    public void setIp(String ip) {
        this.ip = ip;
    }
}
