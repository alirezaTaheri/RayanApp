package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

public class DeleteUserRequest {
    private String user_id;
    private String group_id;

    public DeleteUserRequest(String user_id, String group_id) {
        this.user_id = user_id;
        this.group_id = group_id;
    }
}
