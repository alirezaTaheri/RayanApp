package rayan.rayanapp.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class RemoteData implements Parcelable {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String model, button, type, brand, frequency;
    @SerializedName("main_frame")
    private String mainFrame;
    private String remoteId;

    public RemoteData(@NonNull String id, String model, String button, String type, String brand, String frequency, String mainFrame, String remoteId) {
        this.id = id;
        this.model = model;
        this.button = button;
        this.type = type;
        this.brand = brand;
        this.frequency = frequency;
        this.mainFrame = mainFrame;
        this.remoteId = remoteId;
    }

    protected RemoteData(Parcel in) {
        id = in.readString();
        model = in.readString();
        button = in.readString();
        type = in.readString();
        brand = in.readString();
        frequency = in.readString();
        mainFrame = in.readString();
        remoteId = in.readString();
    }

    public static final Creator<RemoteData> CREATOR = new Creator<RemoteData>() {
        @Override
        public RemoteData createFromParcel(Parcel in) {
            return new RemoteData(in);
        }

        @Override
        public RemoteData[] newArray(int size) {
            return new RemoteData[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(String mainFrame) {
        this.mainFrame = mainFrame;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    @Override
    public String toString() {
        return "RemoteData{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", button='" + button + '\'' +
                ", type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", frequency='" + frequency + '\'' +
                ", mainFrame='" + mainFrame + '\'' +
                ", remoteId='" + remoteId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(model);
        dest.writeString(button);
        dest.writeString(type);
        dest.writeString(brand);
        dest.writeString(frequency);
        dest.writeString(mainFrame);
        dest.writeString(remoteId);
    }
}
