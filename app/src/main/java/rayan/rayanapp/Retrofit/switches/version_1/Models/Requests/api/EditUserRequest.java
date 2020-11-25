package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.UserInfo;

public class EditUserRequest {

    private UserInfo info;
    private String password;

    public EditUserRequest(UserInfo info, String password) {
        this.info = info;
        this.password = password;
    }
}