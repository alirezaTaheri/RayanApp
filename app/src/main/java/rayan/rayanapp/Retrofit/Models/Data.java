package rayan.rayanapp.Retrofit.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class Data {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("groups")
    @Expose
    private List<Group> groups;
    @SerializedName("token")
    @Expose
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Data{" +
                "user=" + user +
                ", groups=" + groups +
                ", token='" + token + '\'' +
                '}';
    }
}
