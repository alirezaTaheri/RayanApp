package rayan.rayanapp.Retrofit.Models.Requests;


public class EditGroupRequest {
    private String name;
    private String group_id;

    public EditGroupRequest(String name, String group_id) {
        this.name = name;
        this.group_id = group_id;
    }
}
