package rayan.rayanapp.Retrofit.Models.Responses.api;

import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    private String contactName;
    private boolean selected;
    @SerializedName("registered")
    @Expose
    private String registered;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("info")
    @Expose
    private UserInfo userInfo;

    private String groupId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("role")
    @Expose
    private String role;
    private String contactNameOnPhone;



    protected User(Parcel in) {
        id = in.readString();
        username = in.readString();
        contactName = in.readString();
        selected = in.readByte() != 0;
        registered = in.readString();
        groupId = in.readString();
        type = in.readString();
        role = in.readString();
        email= in.readString();
        contactNameOnPhone=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public User(){}
    public User(@NonNull String id, String username, String registered, UserInfo userInfo, String groupId, String role, String email) {
        this.id = id;
        this.username = username;
        this.registered = registered;
        this.userInfo = userInfo;
        this.groupId = groupId;
        this.role = role;
        this.email=email;
        this.contactNameOnPhone=contactNameOnPhone;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNameOnPhone() {
        return contactNameOnPhone;
    }

    public void setContactNameOnPhone(String contactNameOnPhone) {
        this.contactNameOnPhone = contactNameOnPhone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", contactName='" + contactName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(contactName);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(registered);
        dest.writeString(groupId);
        dest.writeString(type);
        dest.writeString(role);
        dest.writeString(email);
        dest.writeString(contactNameOnPhone);
    }
}
