package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Data.RemoteHub;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class RemoteHubsData {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("items")
    @Expose
    private List<RemoteHub> remotes;
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("count")
    @Expose
    private int count;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RemoteHub> getRemotes() {
        return remotes;
    }

    public void setRemotes(List<RemoteHub> remotes) {
        this.remotes = remotes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RemoteHubsData{" +
                ", message='" + message + '\'' +
                ", remotes=" + remotes +
                ", token='" + token + '\'' +
                ", count=" + count +
                '}';
    }
}
