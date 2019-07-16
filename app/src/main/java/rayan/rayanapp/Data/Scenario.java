package rayan.rayanapp.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import rayan.rayanapp.Util.dataConverter.DeviceDataConverter;

@Entity
public class Scenario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(DeviceDataConverter.class)
    private List<Device> devices;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Scenario(String name, List<Device> devices) {
        this.name = name;
        this.devices = devices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "name='" + name + '\'' +
                ", devices=" + devices +
                '}';
    }
}
