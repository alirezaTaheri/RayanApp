package rayan.rayanapp.Persistance;

import android.content.SharedPreferences;

import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.UserInfo;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private  final String PREF_NAME = "RayanSmartHome";
    private  int PRIVATE_MODE = 0;
    private  PrefManager prefManager;

   // private final String KEY_GROUPSACTIVITY_FIRSTLAUNCH = "KEY_GROUPSACTIVITY_FIRSTLAUNCH";
    private final String KEY_NODE_SOUND = "KEY_NODE_SOUND";
    private final String KEY_APPLICATION_LANGUAGE = "KEY_APPLICATION_LANGUAGE";
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
    private final String KEY_MQTT_SSL = "KEY_MQTT_SSL";
    private final String KEY_TOKEN_VALID = "KEY_TOKEN";
    private final String KEY_CURRENT_SHOWING_GROUP = "KEY_CURRENT_SHOWING_GROUP";
    private final String KEY_SELECTED_GROUP_ALL_ON_SCENARIO = "KEY_SELECTED_GROUP_ALL_ON_SCENARIO";
    private final String KEY_SELECTED_GROUP_ALL_OFF_SCENARIO = "KEY_SELECTED_GROUP_ALL_OFF_SCENARIO";
    private final String KEY_PRIVACY_POLICY_ACCEPTED = "KEY_PRIVACY_POLICY_ACCEPTED";

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

    public void saveCurrentShowingGroup(String token){
        editor.putString(KEY_CURRENT_SHOWING_GROUP, token);
        editor.commit();
    }

    public String getCurrentShowingGroup() {
        return pref.getString(KEY_CURRENT_SHOWING_GROUP,null);
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

    public boolean isPrivacyPolicyAccepted() {
        return pref.getBoolean(KEY_PRIVACY_POLICY_ACCEPTED,false);
    }

    public void setPrivacyPolicyAccepted(){
        editor.putBoolean(KEY_PRIVACY_POLICY_ACCEPTED, true);
        editor.commit();
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

    public void setMqttSsl(boolean ssl){
        editor.putBoolean(KEY_MQTT_SSL,ssl);
        editor.commit();
    }
    public Boolean isMqttSsl(){
        return pref.getBoolean(KEY_MQTT_SSL,true);
    }

    public void setTokenValidity(boolean validity){
        editor.putBoolean(KEY_TOKEN_VALID,validity);
        editor.commit();
    }
    public Boolean isTokenValid(){
        return pref.getBoolean(KEY_TOKEN_VALID,false);
    }

    public void setBottomNavigationIndexKey(Integer bottomNavigationIndexKey){
        editor.putInt(KEY_BOTTOM_NAVIGATION_INDEX,bottomNavigationIndexKey);
        editor.commit();
    }
    public Integer getBottomNavigationIndexKey(){
        return pref.getInt(KEY_BOTTOM_NAVIGATION_INDEX,1);
    }
public void setIsNodeSoundOn(Boolean nodeSoundOn){
    editor.putBoolean(KEY_NODE_SOUND, nodeSoundOn);
    editor.commit();
}
    public Boolean getIsNodeSoundOn(){
        return pref.getBoolean(KEY_NODE_SOUND,true);
    }

    public void setApplicationLanguage(String language){
        editor.putString(KEY_APPLICATION_LANGUAGE, language);
        editor.commit();
    }
    public String getApplicationLanguage(){
        return pref.getString(KEY_APPLICATION_LANGUAGE,"HARD");
    }

    public void setSelectedGroupAllOnScenario(String groupId){
        editor.putString(KEY_SELECTED_GROUP_ALL_ON_SCENARIO, groupId);
        editor.commit();
    }
    public String getSelectedGroupAllOnScenario(){
        return pref.getString(KEY_SELECTED_GROUP_ALL_ON_SCENARIO, null);
    }

    public void setSelectedGroupAllOffScenario(String groupId){
        editor.putString(KEY_SELECTED_GROUP_ALL_OFF_SCENARIO, groupId);
        editor.commit();
    }
    public String getSelectedGroupAllOffScenario(){
        return pref.getString(KEY_SELECTED_GROUP_ALL_OFF_SCENARIO, null);
    }

}
