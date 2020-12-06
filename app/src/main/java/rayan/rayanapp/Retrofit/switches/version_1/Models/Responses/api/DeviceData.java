package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Data.Device;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class DeviceData {
    @SerializedName("device")
    @Expose
    private Device device;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("group")
    @Expose
    private Group group;
    @SerializedName("token")
    @Expose
    private String token;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "device=" + device +
                ", message='" + message + '\'' +
                ", group=" + group +
                ", token='" + token + '\'' +
                '}';
    }
}
