package rayan.rayanapp.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Button implements Parcelable {
    private String name, signal, type, model, brand, frequency;

    public Button() {
    }
    public Button(RemoteData remoteData){
        this.name = remoteData.getButton();
        this.signal = remoteData.getMainFrame();
        this.type = remoteData.getType();
        this.model = remoteData.getModel();
        this.brand = remoteData.getBrand();
        this.frequency = remoteData.getFrequency();
    }

    public Button(String name, String signal, String type) {
        this.name = name;
        this.signal = signal;
        this.type = type;
        this.frequency = "38000";
    }


    protected Button(Parcel in) {
        name = in.readString();
        signal = in.readString();
        type = in.readString();
        model = in.readString();
        brand = in.readString();
        frequency = in.readString();
    }

    public static final Creator<Button> CREATOR = new Creator<Button>() {
        @Override
        public Button createFromParcel(Parcel in) {
            return new Button(in);
        }

        @Override
        public Button[] newArray(int size) {
            return new Button[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(signal);
        dest.writeString(type);
        dest.writeString(model);
        dest.writeString(brand);
        dest.writeString(frequency);
    }
}
