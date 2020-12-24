package rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.device;

import rayan.rayanapp.App.RayanApplication;

public class BaseRequest {
    private String STWORD,src;

    public BaseRequest(String STWORD) {
        this.STWORD = STWORD;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSTWORD() {
        return STWORD;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
    }
}
