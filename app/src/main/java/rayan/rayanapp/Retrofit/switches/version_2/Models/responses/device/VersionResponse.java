package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

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

    @Override
    public String toString() {
        return "VersionResponse{" +
                "type='" + type + '\'' +
                ", nodetype='" + nodetype + '\'' +
                ", sv='" + sv + '\'' +
                ", fv='" + fv + '\'' +
                ", hv='" + hv + '\'' +
                ", mand='" + mand + '\'' +
                ", serial='" + serial + '\'' +
                '}';
    }
}
