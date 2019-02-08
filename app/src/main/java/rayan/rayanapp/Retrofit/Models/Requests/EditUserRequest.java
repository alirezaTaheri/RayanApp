package rayan.rayanapp.Retrofit.Models.Requests;

import rayan.rayanapp.Retrofit.Models.Responses.UserInfo;

public class EditUserRequest {

    private UserInfo info;
    private String password;

    public EditUserRequest(UserInfo info, String password) {
        this.info = info;
        this.password = password;
    }
}