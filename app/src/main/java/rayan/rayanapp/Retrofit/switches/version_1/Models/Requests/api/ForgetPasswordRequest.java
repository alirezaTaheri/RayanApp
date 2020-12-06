package rayan.rayanapp.Retrofit.switches.version_1.Models.Requests.api;

public class ForgetPasswordRequest {
    private String mobile;
    private String email;

    public ForgetPasswordRequest(String mobile, String email) {
        this.mobile = mobile;
        this.email = email;
    }
}
