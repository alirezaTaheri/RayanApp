package rayan.rayanapp.Retrofit.Models.Responses.api;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.Nullable;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Util.dataConverter.DeviceDataConverter;
import rayan.rayanapp.Util.dataConverter.UserDataConverter;

@Entity
public class Group implements Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("remote_hubs")
    @Ignore
    private List<RemoteHub> remoteHubs;
    @Ignore
    private List<BaseDevice> baseDevices;
//    @SerializedName("admins")
//    @Expose
//    @Ignore
//    private List<ResponseUser> admins;
//    @SerializedName("users")
//    @Expose
//    @Ignore
//    private List<ResponseUser> allUsers;

    @SerializedName("users")
    @Expose
    @TypeConverters(UserDataConverter.class)
    private List<User> humanUsers;
    @SerializedName("admins")
    @Expose
    @TypeConverters(UserDataConverter.class)
    private List<User> admins;
    @SerializedName("devices")
    @Expose
    @TypeConverters(DeviceDataConverter.class)
    private List<Device> devices;
    public Group(){}

    protected Group(Parcel in) {
        id = in.readString();
        name = in.readString();
        secret = in.readString();
        remoteHubs = in.createTypedArrayList(RemoteHub.CREATOR);
        humanUsers = in.createTypedArrayList(User.CREATOR);
        admins = in.createTypedArrayList(User.CREATOR);
        devices = in.createTypedArrayList(Device.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(secret);
        dest.writeTypedList(remoteHubs);
        dest.writeTypedList(humanUsers);
        dest.writeTypedList(admins);
        dest.writeTypedList(devices);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public List<BaseDevice> getBaseDevices() {
        return baseDevices;
    }

    public void setBaseDevices(List<BaseDevice> baseDevices) {
        this.baseDevices = baseDevices;
    }

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

    public List<User> getAdmins() {
        return admins;
    }

    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<RemoteHub> getRemoteHubs() {
        return remoteHubs;
    }

    public void setRemoteHubs(List<RemoteHub> remoteHubs) {
        this.remoteHubs = remoteHubs;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", secret='" + secret + '\'' +
                ", remoteHubs=" + remoteHubs +
                ", baseDevices=" + baseDevices +
                ", humanUsers=" + humanUsers +
                ", admins=" + admins +
                ", devices=" + devices +
                '}';
    }

}
