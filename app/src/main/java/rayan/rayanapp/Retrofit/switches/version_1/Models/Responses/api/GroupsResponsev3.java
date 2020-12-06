package rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class GroupsResponsev3 {

    @SerializedName("data")
    @Expose
    private GroupsDatav3 data;

    @SerializedName("status")
    @Expose
    private Status status;

    public GroupsDatav3 getData() {
        return data;
    }

    public void setData(GroupsDatav3 data) {
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "DeviceBaseResponse{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}