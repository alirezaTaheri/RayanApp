package rayan.rayanapp.Persistance;

import android.content.Context;
import android.content.SharedPreferences;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.Models.UserInfo;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private  final String PREF_NAME = "RayanSmartHome";
    private  int PRIVATE_MODE = 0;
    private  PrefManager prefManager;
    private final String KEY_PROTOCOL = "KEY_PROTOCOL";
    private final String KEY_TOKEN = "KEY_TOKEN";
    private final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private final String KEY_ID = "KEY_ID";
    private final String KEY_USERNAME = "KEY_USERNAME";
    private final String KEY_PASSWORD = "KEY_PASSWORD";
    private final String KEY_LOCAL_BROADCAST_ADDRESS = "KEY_LOCAL_BROADCAST_ADDRESS";
    public PrefManager(){
        pref = RayanApplication.getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveToken(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken()
    {
        return pref.getString(KEY_TOKEN,null);
    }

    public void saveProtocol(String access){
        editor.putString(KEY_PROTOCOL, access);
        editor.commit();
    }

    public String getProtocol()
    {
        return pref.getString(KEY_PROTOCOL,null);
    }

    public String getUsername()
    {
        return pref.getString(KEY_USERNAME,null);
    }

    public String getPassword()
    {
        return pref.getString(KEY_PASSWORD,null);
    }

    public String getId() {
        return pref.getString(KEY_ID,null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void createSession(String id,String username, String password, UserInfo userInfo){
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void saveLocalBroadcastAddress(String localBroadcastAddress){
        editor.putString(KEY_LOCAL_BROADCAST_ADDRESS, localBroadcastAddress);
        editor.commit();
    }

    public String getLocalBroadcastAddress(){
        return pref.getString(KEY_LOCAL_BROADCAST_ADDRESS,null);
    }
}
