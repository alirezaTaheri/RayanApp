package rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.device;

public class RemoteHubBaseResponse {
    protected String result,version,src,error,STWORD;

    public RemoteHubBaseResponse() {
    }

    public RemoteHubBaseResponse(String result) {
        this.result = result;
    }

    public String getSrc() {
        return src;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSTWORD() {
        return STWORD;
    }

    public void setSTWORD(String STWORD) {
        this.STWORD = STWORD;
    }

    @Override
    public String toString() {
        return "RemoteHubBaseResponse{" +
                "result='" + result + '\'' +
                ", version='" + version + '\'' +
                ", src='" + src + '\'' +
                ", error='" + error + '\'' +
                ", STWORD='" + STWORD + '\'' +
                '}';
    }
}
