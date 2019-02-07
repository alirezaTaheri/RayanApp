package rayan.rayanapp.Util.dataConverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import rayan.rayanapp.Retrofit.Models.Responses.User;


/**
 * Created by alireza321 on 21/12/2018.
 */

public class UserDataConverter {
    @TypeConverter
    public String fromCountryLangList(List<User> countryLang) {
        if (countryLang == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {}.getType();
        return gson.toJson(countryLang, type);
    }

    @TypeConverter
    public List<User> toCountryLangList(String countryLangString) {
        if (countryLangString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {}.getType();
        return gson.fromJson(countryLangString, type);

    }
}
