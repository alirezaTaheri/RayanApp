package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class BaseResponse {

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("status")
    @Expose
    private Status status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
