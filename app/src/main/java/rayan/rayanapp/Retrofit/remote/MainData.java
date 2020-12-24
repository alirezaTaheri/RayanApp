package rayan.rayanapp.Retrofit.remote;

import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.RemoteData;

public class MainData {
    private String model, button, type, brand, frequency, main_frame, _id;

    public MainData(Button button) {
        this.model = button.getModel();
        this.button = button.getName();
        this.type = button.getType();
        this.brand = button.getBrand();
        this.frequency = button.getFrequency();
        this.main_frame = button.getSignal();
    }
    public MainData(RemoteData remoteData) {
        this.model = remoteData.getModel();
        this.button = remoteData.getButton();
        this.type = remoteData.getType();
        this.brand = remoteData.getBrand();
        this.frequency = remoteData.getFrequency();
        this.main_frame = remoteData.getMainFrame();
        this._id = remoteData.getId();
    }

    public String getModel() {
        return model;
    }

    public String getButton() {
        return button;
    }

    public String getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getMain_frame() {
        return main_frame;
    }
}
