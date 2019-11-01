package rayan.rayanapp.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
public class RayanUtils {

    public static <T> T convertToObject(Type type, String message){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.fromJson(message, type);
    }
}
