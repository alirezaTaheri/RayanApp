package rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class RemoteHubsData {
    @SerializedName("user")
    @Expose
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RemoteHub> getRemoteHubs() {
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
                "user=" + user +
                ", message='" + message + '\'' +
                ", remotes=" + remotes +
                ", token='" + token + '\'' +
                ", count=" + count +
                '}';
    }
}
