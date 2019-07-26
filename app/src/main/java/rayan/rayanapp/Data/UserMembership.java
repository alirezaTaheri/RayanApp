package rayan.rayanapp.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class UserMembership {
    @PrimaryKey
    @NonNull
    private String membershipId;
    private String groupId;
    private String userId;
    private String userType;

    public UserMembership(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
        setMembershipId(groupId+"/"+userId);
    }

    @NonNull
    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(@NonNull String membershipId) {
        this.membershipId = membershipId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserMembership{" +
                "membershipId='" + membershipId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}

