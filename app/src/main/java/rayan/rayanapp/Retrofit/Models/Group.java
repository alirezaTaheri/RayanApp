package rayan.rayanapp.Retrofit.Models;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Util.DeviceDataConverter;
import rayan.rayanapp.Util.UserDataConverter;

public class Group {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("admins")
    @Expose
    @Ignore
    private List<ResponseUser> admins;
    @SerializedName("users")
    @Expose
    @Ignore
    private List<ResponseUser> allUsers;
    @TypeConverters(UserDataConverter.class)
    private List<User> humanUsers;
    @TypeConverters(DeviceDataConverter.class)
    private List<Device> devices;

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

    public List<ResponseUser> getAdmins() {
        return admins;
    }

    public void setAdmins(List<ResponseUser> admins) {
        this.admins = admins;
    }

    public List<ResponseUser> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<ResponseUser> allUsers) {
        this.allUsers = allUsers;
    }

    public List<User> getHumanUsers() {
        return humanUsers;
    }

    public void setHumanUsers(List<User> humanUsers) {
        this.humanUsers = humanUsers;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", admins=" + admins +
                ", allUsers=" + allUsers +
                ", humanUsers=" + humanUsers +
                ", devices=" + devices +
                '}';
    }
}
