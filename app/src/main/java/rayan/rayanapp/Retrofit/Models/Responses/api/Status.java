package rayan.rayanapp.Retrofit.Models.Responses.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class Status {
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("code")
    @Expose
    private String code;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ConnectionStatus{" +
                "description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
