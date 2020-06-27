package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class ApiGroupsResponseDataV3 {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("items")
    @Expose
    private List<Group> groups;

    @SerializedName("count")
    @Expose
    private int count;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ApiGroupsResponseDataV3{" +
                "message='" + message + '\'' +
                ", groups=" + groups +
                ", count=" + count +
                '}';
    }
}
