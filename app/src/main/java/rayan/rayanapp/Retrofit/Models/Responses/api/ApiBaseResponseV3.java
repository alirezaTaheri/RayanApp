package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class ApiBaseResponseV3<T> {

    @SerializedName("data")
    @Expose
    private T data;

    @SerializedName("status")
    @Expose
    private Status status;

    public T getData() {
        return data;
    }

    public void setData(T data) {
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
        return "ApiBaseResponseV3{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}
