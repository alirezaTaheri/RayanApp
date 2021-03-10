package rayan.rayanapp.Retrofit.switches.version_2.Models.responses.device;

import java.util.ArrayList;

public class AllFilesListResponse extends DeviceBaseResponse {

    private ArrayList<String> files=new ArrayList<>();

    public AllFilesListResponse(ArrayList<String> files) {
        this.files = files;
    }
    public ArrayList<String> getFiles() {
        return files;
    }
}
