package com.watchwomen.gymstarsilver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AppPreferences {

    public static String firstLaunch = null;
    private static final String APP_SHARED_PREFS = "com.watch.gymstasilver";
    public String url = "https://www.gymstarpro.com/restapi.php?";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private static AppPreferences instance;

    public static AppPreferences getInstance(){
        if(instance instanceof AppPreferences){
            return instance;
        }
        else{
            instance=new AppPreferences(GymStarSilver.getInstance().getApplicationContext());
            return instance;
        }
    }

    public AppPreferences(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public void setLoginStatus(boolean b){
        prefsEditor.putBoolean("login_status", b);
        prefsEditor.commit();
    }

public void setOfflineStatus(boolean b){
        prefsEditor.putBoolean("offline_status", b);
        prefsEditor.commit();
    }
    public boolean getAppLaunch(){
        return appSharedPrefs.getBoolean("app_launch", false);
    }

    public void setAppLaunch(boolean b){
        prefsEditor.putBoolean("app_launch", b);
        prefsEditor.commit();
    }

    public boolean getLoginStatus(){
        return appSharedPrefs.getBoolean("login_status", false);
    }
    
    
    public boolean getOfflineStatus(){
        return appSharedPrefs.getBoolean("offline_status", false);
    }

    //Saving receptinist details after login.
    public void setuserdetails(String Userid,  String UtokenID,  String ComanyId,  String CompanyName, String StoreId, String StoreName){
        prefsEditor.putString("Userid", Userid);
        prefsEditor.putString("UtokenID", UtokenID);
        prefsEditor.putString("ComanyId", ComanyId);
        prefsEditor.putString("CompanyName", CompanyName);
        prefsEditor.putString("StoreId", StoreId);
        prefsEditor.putString("StoreName", StoreName);
        prefsEditor.commit();
    }

    public int getRunningTime() {
        int val = appSharedPrefs.getInt("runningtime", 0);
        return val;
    }

    public int getTotalRunningTime() {
        int val = appSharedPrefs.getInt("totalRunningTime", 0);
        return val;
    }

    public void setRunningTime(int value) {
        prefsEditor.putInt("runningtime", value);
        prefsEditor.commit();
    }

    public void setTotalRunningTime(int value) {
        prefsEditor.putInt("totalRunningTime", value);
        prefsEditor.commit();
    }

    public String getLastTotalTime() {
        String user = appSharedPrefs.getString("lasttotaltime", "0");
        return user;
    }

    public void setLastTotalTime(String time) {
        if (time == "null")
            time = "0";
        prefsEditor.putString("lasttotaltime", time);
        prefsEditor.commit();
    }

    public void setLastRespTime(String time) {
        if (time == "null")
            time = "0";
        prefsEditor.putString("lastresptime", time);
        prefsEditor.commit();
    }

    public String getLastRespTime() {

        String user = appSharedPrefs.getString("lastresptime", "0");
        return user;
    }

   /* public  String getuserdetails(){
         appSharedPrefs.getString("Userid", "");
        appSharedPrefs.getString("UtokenID", "");
        appSharedPrefs.getString("ComanyId", "");
        appSharedPrefs.getString("CompanyName", "");
        appSharedPrefs.getString("StoreId", "");
        appSharedPrefs.getString("StoreName", "");
       return appSharedPrefs;

    }*/


    public String getUserId(){
        String user = appSharedPrefs.getString("UserID", "");
        return user;
    }

    public String getUtokenID(){
        return appSharedPrefs.getString("UtokenID", "");
    }

    public String getComanyId(){
        return appSharedPrefs.getString("ComanyId", "");
    }
    public String getCompanyName(){
        return appSharedPrefs.getString("CompanyName", "");
    }
    public String getStoreId(){
        return appSharedPrefs.getString("StoreId", "");
    }
    public String getStoreName(){
        return appSharedPrefs.getString("StoreName", "");
    }

    public String getmob(){
        return appSharedPrefs.getString("time_stamp", "");
    }

    public void setmob(String time_stamp){
        prefsEditor.putString("time_stamp", time_stamp);
        prefsEditor.commit();
    }

    public void setAccesToken(String AccesToken){
        prefsEditor.putString("AccesToken", AccesToken);

        prefsEditor.commit();
    }

    public void setUserID(String UserID){
        prefsEditor.putString("UserID", UserID);

        prefsEditor.commit();
    }
    public String getAccesToken(){
        return appSharedPrefs.getString("AccesToken", "");
    }

}