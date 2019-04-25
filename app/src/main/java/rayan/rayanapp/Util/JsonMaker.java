package rayan.rayanapp.Util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rayan.rayanapp.App.RayanApplication;

public class JsonMaker {

    public JSONObject getJson(String cmd, List<String> values){
        JSONObject jsonObject = new JSONObject();
        String src = RayanApplication.getPref().getId();
        try {
            switch (cmd){
            case AppConstants.SETTINGS:
                    jsonObject.put("cmd",AppConstants.SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.END_SETTINGS:
                    jsonObject.put("cmd",AppConstants.END_SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.MQTT:
                    jsonObject.put("cmd",AppConstants.END_SETTINGS);
                    jsonObject.put("src", src);
                break;
            case AppConstants.ON_1:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("cmd",AppConstants.ON_1);
                    jsonObject.put("src", src);
                break;
            case AppConstants.ON_2:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("cmd",AppConstants.ON_2);
                    jsonObject.put("src", src);
                break;
            case AppConstants.OFF_1:
                if (values.get(1).equals(Boolean.toString(false)))
                    jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("cmd",AppConstants.OFF_1);
                    jsonObject.put("src", src);
                break;
            case AppConstants.OFF_2:
                if (values.get(1).equals(Boolean.toString(false)))
                jsonObject.put("excmd", AppConstants.NO_HTTP);
                    jsonObject.put("stword", values.get(0));
                    jsonObject.put("cmd",AppConstants.OFF_2);
                    jsonObject.put("src", src);
                break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
