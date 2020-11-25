package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

public class RegisterUserRequest {
    private String username;
    private String password;
    private String email;
    public RegisterUserRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


}
