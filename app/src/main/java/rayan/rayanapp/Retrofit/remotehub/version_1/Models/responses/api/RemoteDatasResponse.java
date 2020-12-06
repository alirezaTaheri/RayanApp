package rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Status;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class RemoteDatasResponse {

    @SerializedName("data")
    @Expose
    private RemoteDatasData data;

    @SerializedName("status")
    @Expose
    private Status status;

    public RemoteDatasData getData() {
        return data;
    }

    public void setData(RemoteDatasData data) {
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
