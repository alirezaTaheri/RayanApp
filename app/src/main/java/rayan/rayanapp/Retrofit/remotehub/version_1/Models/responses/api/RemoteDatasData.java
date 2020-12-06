package rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.User;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class RemoteDatasData {
    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("items")
    @Expose
    private List<RemoteData> remotes;
    @SerializedName("token")
    @Expose
    private String token;

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RemoteData> getRemoteDatas() {
        return remotes;
    }

    public void setRemoteDatas(List<RemoteData> remotes) {
        this.remotes = remotes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RemoteDatasData{" +
                "user=" + user +
                ", message='" + message + '\'' +
                ", remotes=" + remotes +
                ", token='" + token + '\'' +
                ", count=" + count +
                '}';
    }
}
