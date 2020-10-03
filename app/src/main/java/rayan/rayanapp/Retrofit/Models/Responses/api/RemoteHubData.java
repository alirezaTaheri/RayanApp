package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Data.RemoteHub;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class RemoteHubData {
//    @SerializedName("user")
//    @Expose
//    private User user;

    @SerializedName("message")
    @Expose
    private String message;

//    @SerializedName("data")
    @SerializedName("remoteHub")
    @Expose
    private RemoteHub remotes;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("count")
    @Expose
    private int count;

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RemoteHub getRemoteHub() {
        return remotes;
    }

    public void setRemotes(RemoteHub remotes) {
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
//                "user=" + user +
                ", message='" + message + '\'' +
                ", remotes=" + remotes +
                ", token='" + token + '\'' +
                ", count=" + count +
                '}';
    }
}
