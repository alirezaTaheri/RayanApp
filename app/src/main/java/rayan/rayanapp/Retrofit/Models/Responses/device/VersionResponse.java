package rayan.rayanapp.Retrofit.Models.Responses.device;

public class VersionResponse extends DeviceBaseResponse{
    private String type, nodetype, sv, fv, hv, mand, serial;

    public String getType() {
        return type;
    }

    public String getNodetype() {
        return nodetype;
    }

    public String getSv() {
        return sv;
    }

    public String getFv() {
        return fv;
    }

    public String getHv() {
        return hv;
    }

    public String getMand() {
        return mand;
    }

    public String getSerial() {
        return serial;
    }
}
