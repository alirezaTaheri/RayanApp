package rayan.rayanapp.Retrofit.Models.Requests;

public class ForgetPasswordRequest {
    private String mobile;
    private String email;

    public ForgetPasswordRequest(String mobile, String email) {
        this.mobile = mobile;
        this.email = email;
    }
}
