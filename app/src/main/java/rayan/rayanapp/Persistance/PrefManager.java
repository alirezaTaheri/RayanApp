package rayan.rayanapp.Persistance;

import android.content.SharedPreferences;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.Models.Responses.api.UserInfo;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private  final String PREF_NAME = "RayanSmartHome";
    private  int PRIVATE_MODE = 0;
    private  PrefManager prefManager;

   // private final String KEY_GROUPSACTIVITY_FIRSTLAUNCH = "KEY_GROUPSACTIVITY_FIRSTLAUNCH";
    private final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    private final String KEY_APPLICATION_LANGUAGE = "KEY_APPLICATION_LANGUAGE";
    private final String KEY_THEME = "KEY_THEME";
    private final String KEY_PROTOCOL = "KEY_PROTOCOL";
    private final String KEY_EMAIL = "KEY_EMAIL";
    private final String KEY_NAME = "KEY_NAME";
    private final String KEY_GENDER = "KEY_GENDER";
    private final String KEY_TOKEN = "KEY_TOKEN";
    private final String KEY_IS_GROUP_ADMIN = "KEY_IS_GROUP_ADMIN";
    private final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private final String KEY_ID = "KEY_ID";
    private final String KEY_BOTTOM_NAVIGATION_INDEX = "Bottom_Navigation";
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

    public void createSession(String id,String username, String password, UserInfo userInfo,String email){
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_NAME,userInfo.getName());
        editor.putString(KEY_GENDER,userInfo.getGender());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL,email);
        editor.commit();
    }

    public void removeSession(){
        editor.remove(KEY_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_NAME);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_EMAIL);
      //  editor.remove(KEY_GROUPSACTIVITY_FIRSTLAUNCH);
        editor.commit();
    }



    public void saveLocalBroadcastAddress(String localBroadcastAddress){
        editor.putString(KEY_LOCAL_BROADCAST_ADDRESS, localBroadcastAddress);
        editor.commit();
    }

    public String getLocalBroadcastAddress(){
        return pref.getString(KEY_LOCAL_BROADCAST_ADDRESS,null);
    }


    public void setThemeKey(Integer themeKey){
        editor.putInt(KEY_THEME,themeKey);
        editor.commit();
    }
    public Integer getThemeKey(){
        return pref.getInt(KEY_THEME,0);
    }

    public void setNameKey(String name){
        editor.putString(KEY_NAME,name);
        editor.commit();
    }
    public void setGenderKey(String gender){
        editor.putString(KEY_GENDER,gender);
        editor.commit();
    }
    public String getNameKey(){
        return pref.getString(KEY_NAME,null);
    }
    public String getGenderKey(){
        return pref.getString(KEY_GENDER,"chose");
    }

    public void setEmailKey(String email){
        editor.putString(KEY_EMAIL,email);
        editor.commit();
    }
    public String getEmailKey(){
        return pref.getString(KEY_EMAIL,null);
    }

    public void setIsGroupAdminKey(boolean isGroupAdminKey){
        editor.putBoolean(KEY_IS_GROUP_ADMIN,isGroupAdminKey);
        editor.commit();
    }
    public Boolean getIsGroupAdminKey(){
        return pref.getBoolean(KEY_IS_GROUP_ADMIN,false);
    }

    public void setBottomNavigationIndexKey(Integer bottomNavigationIndexKey){
        editor.putInt(KEY_BOTTOM_NAVIGATION_INDEX,bottomNavigationIndexKey);
        editor.commit();
    }
    public Integer getBottomNavigationIndexKey(){
        return pref.getInt(KEY_BOTTOM_NAVIGATION_INDEX,1);
    }
public void setIsNotificationOn(Boolean isNotificationOn){
    editor.putBoolean(KEY_NOTIFICATION, isNotificationOn);
    editor.commit();
}
    public Boolean getIsNotificationOn(){
        return pref.getBoolean(KEY_NOTIFICATION,true);
    }

    public void setApplicationLanguage(String language){
        editor.putString(KEY_APPLICATION_LANGUAGE, language);
        editor.commit();
    }
    public String getApplicationLanguage(){
        return pref.getString(KEY_APPLICATION_LANGUAGE,"HARD");
    }

//    public void setIsGroupsActivityFirstLunch(Boolean isGroupsActivityFirstLunch){
//        editor.putBoolean(KEY_GROUPSACTIVITY_FIRSTLAUNCH, isGroupsActivityFirstLunch);
//        editor.commit();
//    }
//    public Boolean getIsGroupsActivityFirstLunch(){
//        return pref.getBoolean(KEY_GROUPSACTIVITY_FIRSTLAUNCH,true);
//    }

}
