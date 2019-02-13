package rayan.rayanapp.Retrofit.Models.Requests.api;

public class ChangePasswordRequest {

    private String password;
    private String new_password;
    private String username;

    public ChangePasswordRequest(String password, String new_password, String username) {
        this.password = password;
        this.new_password = new_password;
        this.username = username;
    }
}
