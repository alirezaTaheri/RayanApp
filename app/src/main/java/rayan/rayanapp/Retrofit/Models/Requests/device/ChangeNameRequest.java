package rayan.rayanapp.Retrofit.Models.Requests.device;

public class ChangeNameRequest extends BaseRequest{
    private String name;

    public ChangeNameRequest(String name) {
        this.name = name;
        super.setCmd("CHANGE_HNAME");
    }
}
