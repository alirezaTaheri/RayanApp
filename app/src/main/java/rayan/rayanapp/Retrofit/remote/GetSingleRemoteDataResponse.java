package rayan.rayanapp.Retrofit.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Status;

public class GetSingleRemoteDataResponse {
    @SerializedName("data")
    @Expose
    private RemoteDataData data;

    @SerializedName("status")
    @Expose
    private Status status;

    public class RemoteDataData{
        @SerializedName("item")
        RemoteData remoteData;

        public RemoteData getRemoteData() {
            return remoteData;
        }

        @Override
        public String toString() {
            return "RemoteDataData{" +
                    "remoteData=" + remoteData +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RemoteDataResponse{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }

    public RemoteDataData getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }
}
