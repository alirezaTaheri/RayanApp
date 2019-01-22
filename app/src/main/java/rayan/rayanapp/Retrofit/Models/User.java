package rayan.rayanapp.Retrofit.Models;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("registered")
    @Expose
    private String registered;

    @SerializedName("info")
    @Expose
    private UserInfo userInfo;

    private String groupId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("role")
    @Expose
    private String role;

    public User(@NonNull String id, String username, String registered, UserInfo userInfo, String groupId, String role) {
        this.id = id;
        this.username = username;
        this.registered = registered;
        this.userInfo = userInfo;
        this.groupId = groupId;
        this.role = role;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
