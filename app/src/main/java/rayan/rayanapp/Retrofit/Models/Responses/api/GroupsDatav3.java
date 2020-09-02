package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class GroupsDatav3 {
    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("items")
    @Expose
    private List<Group> groups;
    @SerializedName("token")
    @Expose
    private String token;

    private int count;

    public int getCount() {
        return count;
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
        return "GroupsData{" +
                "user=" + user +
                ", message='" + message + '\'' +
                ", groups=" + groups +
                ", token='" + token + '\'' +
                ", count=" + count +
                '}';
    }
}
