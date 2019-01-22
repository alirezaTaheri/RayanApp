package rayan.rayanapp.Util;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import rayan.rayanapp.Data.Device;

/**
 * Created by alireza321 on 21/12/2018.
 */

public class DeviceDataConverter {
    @TypeConverter
    public String fromCountryLangList(List<Device> countryLang) {
        if (countryLang == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    public List<Device> toCountryLangList(String countryLangString) {
        if (countryLangString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Device>>() {}.getType();
        return gson.fromJson(countryLangString, type);
    }
}
