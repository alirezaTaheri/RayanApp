package rayan.rayanapp.Data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PhoneContact implements Parcelable {
    private String id;
    private String name;
    private String number;
    private Bitmap image;
    private boolean isSelected;

    public PhoneContact(String id, String name, String number, Bitmap image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public PhoneContact(){}
    protected PhoneContact(Parcel in) {
        id = in.readString();
        name = in.readString();
        number = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", numbers='" + number + '\'' +
                ", selected=" + isSelected +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumbers() {
        return number;
    }

    public void setNumbers(String numbers) {
        this.number = numbers;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

