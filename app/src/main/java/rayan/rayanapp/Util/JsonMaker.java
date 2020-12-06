package rayan.rayanapp.Util;

import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rayan.rayanapp.App.RayanApplication;

public class JsonMaker {

    public JSONObject getJSON(String cmd, List<String> values){
        JSONObject jsonObject = new JSONObject();
        JSONObject lcJsonObject = new JSONObject();
        String src = RayanApplication.getPref().getId();
        try {
            switch (cmd){
            case AppConstants.SETTINGS:
                    jsonObject.put("result",AppConstants.SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.END_SETTINGS:
                    jsonObject.put("result",AppConstants.END_SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.MQTT:
                    jsonObject.put("result",AppConstants.END_SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.ON_1:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.ON_1);
                    jsonObject.put("src", src);
                break;
            case AppConstants.ON_2:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.ON_2);
                    jsonObject.put("src", src);
                break;
            case AppConstants.OFF_1:
                if (values.get(1).equals(Boolean.toString(false)))
                    jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.OFF_1);
                    jsonObject.put("src", src);
                break;
            case AppConstants.OFF_2:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.OFF_2);
                    jsonObject.put("src", src);
                break;
                case AppConstants.ON_1_ON_2:
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.ON_1);
                    jsonObject.put("src", src);
                    lcJsonObject.put("stword", values.get(0));
                    lcJsonObject.put("result", AppConstants.ON_2);
                    lcJsonObject.put("src", src);
                    jsonObject.put("lc", lcJsonObject);
                    break;
                case AppConstants.ON_1_OFF_2:
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.ON_1);
                    jsonObject.put("src", src);
                    lcJsonObject.put("stword", values.get(0));
                    lcJsonObject.put("result", AppConstants.OFF_2);
                    lcJsonObject.put("src", src);
                    jsonObject.put("lc", lcJsonObject);
                    break;
                case AppConstants.OFF_1_OFF_2:
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.OFF_1);
                    jsonObject.put("src", src);
                    lcJsonObject.put("stword", values.get(0));
                    lcJsonObject.put("result", AppConstants.OFF_2);
                    lcJsonObject.put("src", src);
                    jsonObject.put("lc", lcJsonObject);
                    break;
                case AppConstants.OFF_1_ON_2:
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("result",AppConstants.OFF_1);
                    jsonObject.put("src", src);
                    lcJsonObject.put("stword", values.get(0));
                    lcJsonObject.put("result", AppConstants.ON_2);
                    lcJsonObject.put("src", src);
                    jsonObject.put("lc", lcJsonObject);
                    break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JsonObject getJson(String cmd, List<String> values){
        JsonObject jsonObject = new JsonObject();
        JsonObject lcJsonObject = new JsonObject();
        String src = RayanApplication.getPref().getId();
        try {
            switch (cmd){
                case AppConstants.ON_1_ON_2:
                    jsonObject.addProperty("stword", values.get(0));
                    jsonObject.addProperty("result",AppConstants.ON_1);
                    jsonObject.addProperty("src", src);
                    lcJsonObject.addProperty("stword", values.get(0));
                    lcJsonObject.addProperty("result", AppConstants.ON_2);
                    lcJsonObject.addProperty("src", src);
                    jsonObject.add("lc", lcJsonObject);
                    break;
                case AppConstants.ON_1_OFF_2:
                    jsonObject.addProperty("stword", values.get(0));
                    jsonObject.addProperty("result",AppConstants.ON_1);
                    jsonObject.addProperty("src", src);
                    lcJsonObject.addProperty("stword", values.get(0));
                    lcJsonObject.addProperty("result", AppConstants.OFF_2);
                    lcJsonObject.addProperty("src", src);
                    jsonObject.add("lc", lcJsonObject);
                    break;
                case AppConstants.OFF_1_OFF_2:
                    jsonObject.addProperty("stword", values.get(0));
                    jsonObject.addProperty("result",AppConstants.OFF_1);
                    jsonObject.addProperty("src", src);
                    lcJsonObject.addProperty("stword", values.get(0));
                    lcJsonObject.addProperty("result", AppConstants.OFF_2);
                    lcJsonObject.addProperty("src", src);
                    jsonObject.add("lc", lcJsonObject);
                    break;
                case AppConstants.OFF_1_ON_2:
                    jsonObject.addProperty("stword", values.get(0));
                    jsonObject.addProperty("result",AppConstants.OFF_1);
                    jsonObject.addProperty("src", src);
                    lcJsonObject.addProperty("stword", values.get(0));
                    lcJsonObject.addProperty("result", AppConstants.ON_2);
                    lcJsonObject.addProperty("src", src);
                    jsonObject.add("lc", lcJsonObject);
                    break;
                }
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
