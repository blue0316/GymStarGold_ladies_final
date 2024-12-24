package com.watchwomen.gymstarsilver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static java.io.File.separator;

public class DatabaseTransactions extends SQLiteOpenHelper {

    private static String DB_NAME = "gymstarpro_app.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 4;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseTransactions(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        this.getReadableDatabase();
    }


    public ArrayList getAllCotacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();
        Cursor res = db.rawQuery("select (id ||' : '||user_login || ' : ' || user_email || ' : '|| display_name) as fullname from wp_users ORDER BY id DESC LIMIT 5", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0))
                array_list.add(res.getString(res.getColumnIndex("fullname")));
            res.moveToNext();
        }

        return array_list;
    }


    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public JSONObject getAthleteRoutines(String userID) throws JSONException {

        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        JSONArray routineNameArray = new JSONArray();

        Cursor res;
        res = db.rawQuery("SELECT DISTINCT (TRIM(workoutroutinename) || '' || REPLACE (scheduleworkoutroutine,',','')) as wr, TRIM(workoutroutinename) as workoutroutinename, REPLACE (scheduleworkoutroutine,',','') as scheduleworkoutroutine FROM `station_log` WHERE user_id = ? AND workoutroutinename != '' ORDer BY scheduleworkoutroutine, created_at", new String[]{userID});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                JSONObject item = new JSONObject();
                item.put("routine_display_name", res.getString(res.getColumnIndex("wr")));
                item.put("routine_name",  res.getString(res.getColumnIndex("workoutroutinename")));
                item.put("schedule",  res.getString(res.getColumnIndex("scheduleworkoutroutine")));
                routineNameArray.put(item);
            }
            res.moveToNext();
        }
         res.close();


        JSONArray routineArray = new JSONArray();


        Cursor res1 = db.rawQuery("SELECT s.id, s.muscle_id, TRIM(s.workoutroutinename) as workoutroutinename, (REPLACE(s.scheduleworkoutroutine,',','')) as scheduleworkoutroutine, s.sheduleweedDays, s.station_number, s.sheduleDay, a.app_muscle_name FROM `station_log` s,  `app_muscle_post_mapping` a WHERE s.user_id = ? AND s.workoutroutinename != ''  AND a.wp_post_cat_id = s.muscle_id order by s.scheduleworkoutroutine, s.sheduleDay", new String[]{userID});
        res1.moveToFirst();
        while (res1.isAfterLast() == false) {
            if ((res1 != null) && (res1.getCount() > 0)) {
                JSONObject item = new JSONObject();
                item.put("id", res1.getString(res1.getColumnIndex("id")));
                item.put("muscle_id",  res1.getString(res1.getColumnIndex("muscle_id")));
                item.put("routine_name",  res1.getString(res1.getColumnIndex("workoutroutinename")));
                item.put("schedule_workout_routine",  res1.getString(res1.getColumnIndex("scheduleworkoutroutine")));
                item.put("schedule_week_days",  res1.getString(res1.getColumnIndex("sheduleweedDays")));
                item.put("station_number",  res1.getString(res1.getColumnIndex("station_number")));
                item.put("muscle_name",  res1.getString(res1.getColumnIndex("app_muscle_name")));
                item.put("schedule_day",  res1.getString(res1.getColumnIndex("sheduleDay")));
                routineArray.put(item);
            }
            res1.moveToNext();
        }
        res1.close();

        json.put("status", "success");
        json.put("routine_names", routineNameArray);
        json.put("routine_records", routineArray);
        return json;
    }

    public JSONObject getSystemLogRoutines(String userID, String selectedRoutine, String selectedSchedule) throws JSONException{

        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray routineListArray = new JSONArray();

        Cursor res;
        res = db.rawQuery("SELECT s.id, s.muscle_id, s.workoutroutinename, s.scheduleworkoutroutine, s.station_number, s.sheduleweedDays, s.sheduleDay, a.app_muscle_name FROM `station_log` s, `app_muscle_post_mapping` a WHERE user_id = ? AND Trim(s.workoutroutinename) = ? AND (REPLACE(s.scheduleworkoutroutine, ',','')) = ? AND a.wp_post_cat_id = s.muscle_id order by CAST(s.sheduleDay AS INTEGER)", new String[]{userID, selectedRoutine, selectedSchedule});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                JSONObject item = new JSONObject();
                item.put("id", res.getString(res.getColumnIndex("id")));
                item.put("muscle_id",  res.getString(res.getColumnIndex("muscle_id")));
                item.put("station_number",  res.getString(res.getColumnIndex("station_number")));
                item.put("routine_nos",  res.getString(res.getColumnIndex("scheduleworkoutroutine")));
                item.put("muscle_name",  res.getString(res.getColumnIndex("app_muscle_name")));
                item.put("week_days",  res.getString(res.getColumnIndex("sheduleweedDays")));
                item.put("routine_name",  res.getString(res.getColumnIndex("workoutroutinename")));
                item.put("schedule_day",  res.getString(res.getColumnIndex("sheduleDay")));
                routineListArray.put(item);
            }
            res.moveToNext();
        }
        res.close();


        json.put("status", "success");
        json.put("routine_list", routineListArray);
        return json;

    }

    public JSONObject getMaxHighest(String userId) throws JSONException{

        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        JSONArray stationsListArray = new JSONArray();
        JSONArray muscleTypesArray = new JSONArray();
        JSONArray valuesArray = new JSONArray();

        String maxValueMaxWeight = "0";
        String maxValueMaxWeightHTotal = "0";
        String maxValueMaxWeight24 = "0";
        String maxValueMaxEffort7 = "0";
        String maxValueMaxEffortmg24 = "0";
        String maxValueMaxEffortmg7 = "0";


        String maxStationMaxWeight = "0";
        String maxStationMaxWeightHTotal = "0";
        String maxStationMaxWeight24 = "0";
        String maxStationMaxEffort7 = "0";
        String maxStationMaxEffortmg24 = "0";
        String maxStationMaxEffortmg7 = "0";


        JSONObject item = new JSONObject();
        item.put("maxMuscleMaxWeight", "");
        item.put("maxMuscleMaxWeight_HTotal", "");
        item.put("maxMuscleMaxWeight_24", "");
        item.put("maxMuscleMaxEffort_7", "");
        item.put("maxMuscleMaxEffort_mg_24", "");
        item.put("maxMuscleMaxEffort_mg_7", "");
        muscleTypesArray.put(item);

        Cursor res;
        res = db.rawQuery("select d.*, a.app_muscle_name from dailyrecord d join app_muscle_post_mapping a ON a.wp_post_cat_id = d.muscle_type where Weight=(select MAX(Weight) from dailyrecord where user_id=?) and user_id=? LIMIT 1", new String[]{userId, userId});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {

                String tmpName = "";
                if(res.getString(res.getColumnIndex("app_muscle_name")) == "Gluteus Maxim(Butt)"){
                    tmpName = "Glutes";
                }else{
                    tmpName = res.getString(res.getColumnIndex("app_muscle_name"));
                }

                maxStationMaxWeight = tmpName;
                maxValueMaxWeight = res.getString(res.getColumnIndex("Weight"));
            }
            res.moveToNext();
        }
        res.close();


        Cursor res1;
        res1 = db.rawQuery("select d.*, a.app_muscle_name from dailyrecord d join app_muscle_post_mapping a on a.wp_post_cat_id = d.muscle_type where total_weight = (select MAX(total_weight) from dailyrecord where user_id=?) and user_id=? LIMIT 1", new String[]{userId, userId});
        res1.moveToFirst();
        while (res1.isAfterLast() == false) {
            if ((res1 != null) && (res1.getCount() > 0)) {

                String tmpName = "";
                if(res1.getString(res1.getColumnIndex("app_muscle_name")) == "Gluteus Maxim(Butt)"){
                    tmpName = "Glutes";
                }else{
                    tmpName = res1.getString(res1.getColumnIndex("app_muscle_name"));
                }

                maxStationMaxWeightHTotal = tmpName;
                maxValueMaxWeightHTotal = res1.getString(res1.getColumnIndex("total_weight"));
            }
            res1.moveToNext();
        }
        res1.close();


        Cursor res2;
        res2 = db.rawQuery("SELECT max(gymtotal) as tw, substr(strftime('%Y', datetime(date)),3, 2) || strftime('/%d/%m', datetime(date)) as date, station_no, muscle_type gt FROM `dailyrecord` where user_id =?", new String[]{userId});
        res2.moveToFirst();
        while (res2.isAfterLast() == false) {
            if ((res2 != null) && (res2.getCount() > 0)) {

                maxStationMaxWeight24 = res2.getString(res2.getColumnIndex("date"));
                maxValueMaxWeight24 = res2.getString(res2.getColumnIndex("tw"));
            }
            res2.moveToNext();
        }
        res2.close();

        Cursor res3;
        res3 = db.rawQuery("select sum(gt) as tw, date, substr(strftime('%Y', datetime(start_date)),3, 2) || strftime('/%d/%m', datetime(start_date)) as start_date, substr(strftime('%Y', datetime(end_date)),3, 2) || strftime('/%d/%m', datetime(end_date)) as end_date from(SELECT max(gymtotal) as gt, date, station_no, muscle_type, start_date, end_date from dailyrecord d , tbl_year_weeks y where user_id = ? AND d.date BETWEEN y.start_date AND y.end_Date group by date) A  group by end_date order by tw desc LIMIT 1", new String[]{userId});
        res3.moveToFirst();
        while (res3.isAfterLast() == false) {
            if ((res3 != null) && (res3.getCount() > 0)) {

                maxStationMaxEffort7 = res3.getString(res3.getColumnIndex("start_date")) + "- " + res3.getString(res3.getColumnIndex("end_date"));
                maxValueMaxEffort7 = res3.getString(res3.getColumnIndex("tw"));
            }
            res3.moveToNext();
        }
        res3.close();


        Cursor res4;
        res4 = db.rawQuery("select sum(tw) as tw, substr(strftime('%Y',date),3, 2) || strftime('/%m/%d', date) as date, group_concat(DISTINCT station_no) as station_no, muscle_type, term_taxonomy_id, ampm.name as musclegroup from (select sum(tw) as tw, date, group_concat(DISTINCT station_no) as station_no, muscle_type, object_id, term_taxonomy_id from (select day_date, d.muscle_type, d.user_id, max(d.total_weight) as tw, d.date, d.station_no from tbl_year_days y JOIN dailyrecord d ON d.date = y.day_date WHERE d.user_id = ? group by date, machineno) A JOIN `wp_term_relationships` wtr ON wtr.object_id = muscle_type group by date, muscle_type) B JOIN `wp_terms` ampm ON ampm.term_id = term_taxonomy_id group by date, term_taxonomy_id order by tw desc LIMIT 1", new String[]{userId});
        res4.moveToFirst();
        while (res4.isAfterLast() == false) {
            if ((res4 != null) && (res4.getCount() > 0)) {

                maxStationMaxEffortmg24 = res4.getString(res4.getColumnIndex("musclegroup")) + "\n" + res4.getString(res4.getColumnIndex("date"));;
                maxValueMaxEffortmg24 = res4.getString(res4.getColumnIndex("tw"));
            }
            res4.moveToNext();
        }
        res4.close();


        Cursor res5;
        res5 = db.rawQuery("select sum(tw) as tw, (substr(strftime(\"%Y\",start_date),3, 2) || strftime(\"/%m/%d\",start_date)) as start_date, substr(strftime('%Y',end_date),3, 2) || strftime('/%m/%d',end_date) as end_date, group_concat(DISTINCT station_no) as station_no, muscle_type, term_taxonomy_id, ampm.name as musclegroup from (select sum(tw) as tw, start_date, end_date, group_concat(DISTINCT station_no) as station_no , muscle_type, object_id, term_taxonomy_id  from (select start_date, end_date, d.muscle_type, d.user_id, max(d.total_weight) as tw, d.date, d.station_no from tbl_year_weeks y JOIN dailyrecord d ON d.date BETWEEN y.start_date AND y.end_date WHERE d.user_id =  ? group by date, station_no) A JOIN `wp_term_relationships` wtr ON wtr.object_id = muscle_type group by date, muscle_type) B JOIN `wp_terms` ampm ON ampm.term_id = term_taxonomy_id group by end_date,term_taxonomy_id order by tw desc LIMIT 1", new String[]{userId});
        res5.moveToFirst();
        while (res5.isAfterLast() == false) {
            if ((res5 != null) && (res5.getCount() > 0)) {

                maxStationMaxEffortmg7 = res5.getString(res5.getColumnIndex("musclegroup")) + "\n" + res5.getString(res5.getColumnIndex("start_date")) + "- "  + res5.getString(res5.getColumnIndex("end_date"));
                maxValueMaxEffortmg7 = res5.getString(res5.getColumnIndex("tw"));
            }
            res5.moveToNext();
        }
        res5.close();

        JSONObject item2 = new JSONObject();
        item2.put("maxStationMaxWeight", maxStationMaxWeight);
        item2.put("maxStationMaxWeight_HTotal", maxStationMaxWeightHTotal);
        item2.put("maxStationMaxWeight_24", maxStationMaxWeight24);
        item2.put("maxStationMaxEffort_7", maxStationMaxEffort7);
        item2.put("maxStationMaxEffort_mg_24", maxStationMaxEffortmg24);
        item2.put("maxStationMaxEffort_mg_7", maxStationMaxEffortmg7);
        stationsListArray.put(item2);


        JSONObject item3 = new JSONObject();
        item3.put("maxValueMaxWeight", maxValueMaxWeight);
        item3.put("maxValueMaxWeight_HTotal", maxValueMaxWeightHTotal);
        item3.put("maxValueMaxWeight_24", maxValueMaxWeight24);
        item3.put("maxValueMaxEffort_7", maxValueMaxEffort7);
        item3.put("maxValueMaxEffort_mg_24", maxValueMaxEffortmg24);
        item3.put("maxValueMaxEffort_mg_7", maxValueMaxEffortmg7);
        valuesArray.put(item3);

        json.put("status", "success");
        json.put("stations", item2);
        json.put("muscle_types", muscleTypesArray);
        json.put("values", item3);

        return json;
    }

    public JSONObject getSystemLogRoutines(String userId, String muscleType) throws JSONException{

        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();

        JSONArray stationListArray = new JSONArray();

        Cursor res;
        res = db.rawQuery("SELECT s.disclosure_support,s.station_number `mid`,s.muscle_type_info,s.favourite_title,s.fav FROM `station_log` s left join wp_posts p on(s.muscle_id=p.id) where s.user_id=? and p.post_name=?", new String[]{userId, muscleType});
        if(res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    JSONObject item = new JSONObject();
                    item.put("id", res.getString(res.getColumnIndex("mid")));
                    item.put("title1", res.getString(res.getColumnIndex("muscle_type_info")));
                    item.put("title2", res.getString(res.getColumnIndex("favourite_title")));
                    item.put("favorite", res.getString(res.getColumnIndex("fav")));
                    item.put("disclosure_support", res.getString(res.getColumnIndex("disclosure_support")));
                    stationListArray.put(item);
                }
                res.moveToNext();
            }
            res.close();
        }else{
            json.put("status", "no data found");
        }


        json.put("data", stationListArray);
        return json;

    }

    public JSONObject addGymRecord(String user_id, String station_no, String muscle_name, String reps, String weight, String timezone, String current_date, String current_time, String previous_date)  throws JSONException{

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject json = new JSONObject();
        JSONArray stationListArray = new JSONArray();

        String expiryDate = "";
        String membershipName = "";
        String set_number = "";

        long lastInsertId = 0;

        String last_date ="";
        String last_weight ="";
        String last_reps_display ="";
        String last_calorie_display ="";
        String last_total_weight ="";
        String last_gymstarTotal ="";

        String todays_weight ="";
        String todays_reps_display ="";
        String today_calorie_display ="";
        String todays_total_weight ="";
        String todays_gymstarTotal ="";

        String str= user_id +"-"+ station_no +"-"+ muscle_name +"-"+ reps +"-"+ weight +"-"+ timezone +"-"+ current_date +"-"+ current_time +"-"+ previous_date;
        Log.d("params", str);
        JSONObject user_membership_details = get_membership_details(user_id);
        JSONArray jsonArray = user_membership_details.getJSONArray("data");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            expiryDate = (String) item.getString("enddate");
            membershipName = (String) item.getString("name");

        }


        if (user_id == "" || muscle_name == "" || station_no == "" || timezone == "")
        {
            json.put("'status'", "0");
            json.put("'message'", "User ID or Muscle Name or Station No or Timezone is missing in param");
        }
        else if (reps == "")
        {
            json.put("'status'", "0");
            json.put("'message'", "Reps cannot be left blank");
        }
        else if (weight == "")
        {
            json.put("'status'", "0");
            json.put("'message'", "Weight cannot be left blank");
        }
        else if (Integer.valueOf(weight) > 999)
        {
            json.put("'status'", "0");
            json.put("'message'", "Weight should be less then 1000");
        }
        else if (Integer.valueOf(reps) > 99)
        {
            json.put("'status'", "0");
            json.put("'message'", "Reps should be less then 100");
        }else {

            if(muscle_name == "XTraM"){ muscle_name = "XTra Muscle"; }

            String muscle_id = get_muscle_id(muscle_name);

            String machineno = get_new_machine_for_new_entry(user_id,muscle_id,station_no,current_date);

            set_number = get_new_set_id_muscle_type(user_id,machineno,current_date,station_no, muscle_id);

            int calorie_converter = (Integer.valueOf(reps)/5);

            int t_calorie = get_last_calorie(user_id,muscle_id,station_no,current_date);
            int calorie =  t_calorie   + calorie_converter;

            int  total_reps = 0;
            int total_weight =0;

            String total_calorie = "0";
            String gymtotal = "0";

            String  t_total_reps = "0";
            String t_total_weight ="0";
            String t_last_calorie ="0";

            Cursor res, res1, res2, res3,res10;
            if(muscle_id != ""){

               // String qry = "select total_weight, total_reps, gymtotal from  dailyrecord where user_id = "+user_id+" and muscle_type = "+muscle_id+" and station_no = "+station_no+" and date="+current_date+" order by id desc LIMIT 1";
               // Log.d("qry1 ", qry);
                res = db.rawQuery("select total_weight, total_reps, gymtotal from  dailyrecord where user_id = ? and muscle_type = ? and station_no = ? and date=? order by id desc LIMIT 1", new String[]{user_id,muscle_id,station_no,current_date});
            } else {
                //String qry = "select total_weight, total_reps, gymtotal from  dailyrecord where user_id = "+user_id+" and station_no = "+station_no+" and date="+current_date+" order by id desc LIMIT 1";
               // Log.d("qry2 ", qry);
                res = db.rawQuery("select total_weight, total_reps, gymtotal from  dailyrecord where user_id = ? and station_no = ? and date=? order by id desc LIMIT 1", new String[]{user_id,station_no,current_date});
            }

            res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    t_total_reps = res.getString(res.getColumnIndex("total_reps"));
                    t_total_weight = res.getString(res.getColumnIndex("total_weight"));
                }
                res.moveToNext();
            }
            res.close();
            total_reps = Integer.valueOf(t_total_reps) + Integer.valueOf(reps);

            total_weight = Integer.valueOf(t_total_weight) + (Integer.valueOf(weight) * Integer.valueOf(reps));

            t_last_calorie = get_last_total_calorie(user_id,current_date);

            total_calorie = String.valueOf(Integer.valueOf(t_last_calorie) + calorie_converter);

            String t_gymtotal = "0";
            res1 = db.rawQuery("select  gymtotal from  dailyrecord where user_id = ? and date = ? order by id desc LIMIT 1", new String[]{user_id,current_date});
            res1.moveToFirst();
            while (res1.isAfterLast() == false) {
                if ((res1 != null) && (res1.getCount() > 0)) {
                    t_gymtotal = res1.getString(res1.getColumnIndex("gymtotal"));
                }
                res1.moveToNext();
            }
            res1.close();
            //Log.d("gymtotal_par",t_gymtotal +":"+weight+":"+reps);
            gymtotal = String.valueOf(Integer.valueOf(t_gymtotal) + (Integer.valueOf(weight) * Integer.valueOf(reps)));

            String tstatus = "";String tt = ""; String repstime = "";

            //res2 = db.rawQuery("INSERT INTO dailyrecord(muscle_type,station_no,set_number,reps,calorie,Weight,date,user_id,status,timezone,machineno,total_reps,total_calorie,total_weight,gymtotal,time,tt,repstime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new String[]{muscle_id,station_no,set_number,reps,String.valueOf(calorie),weight,current_date,user_id,tstatus,timezone,machineno,String.valueOf(total_reps),total_calorie,String.valueOf(total_weight),gymtotal,current_time,tt,repstime});

            ContentValues newValues = new ContentValues();
            newValues.put("muscle_type", muscle_id);
            newValues.put("station_no", station_no);
            newValues.put("set_number", set_number);
            newValues.put("reps", reps);
            newValues.put("calorie", calorie);
            newValues.put("Weight", weight);
            newValues.put("date", current_date);
            newValues.put("user_id", user_id);
            newValues.put("status", tstatus);
            newValues.put("timezone", timezone);
            newValues.put("machineno", machineno);
            newValues.put("total_reps", total_reps);
            newValues.put("total_calorie", total_calorie);
            newValues.put("total_weight", total_weight);
            newValues.put("gymtotal", gymtotal);
            newValues.put("time", current_time);
            newValues.put("tt", tt);
            newValues.put("repstime", repstime);

           // Log.d("gymtotal:" , gymtotal);
            lastInsertId = db.insert("dailyrecord", null, newValues);

            //lastInsertId =  db.insert("INSERT INTO main.dailyrecord(muscle_type,station_no,set_number,reps,calorie,Weight,date,user_id,status,timezone,machineno,total_reps,total_calorie,total_weight,gymtotal,time,tt,repstime) VALUES ('"+muscle_id+"','\"+station_no+\"','\"+set_number+\"','\"+reps+\"','\"+calorie+\"','\"+weight+\"','\"+current_date+\"','\"+user_id+\"','\"+tstatus+\"','\"+timezone+\"','\"+machineno+\"','\"+total_reps+\"','\"+total_calorie+\"','\"+total_weight+\"','\"+gymtotal+\"','\"+current_time+\"','\"+tt+\"','\"+repstime+\"');")
         /*   lastInsertId =  db.insert("INSERT INTO main.dailyrecord(muscle_type,station_no,set_number,reps,calorie,Weight,date,user_id,status,timezone,machineno,total_reps,total_calorie," +
                    "total_weight,gymtotal,time,tt,repstime) VALUES ('"+muscle_id+"','"+station_no+"','"+set_number+"','"+reps+"','"+calorie+"','"+weight+"','"+current_date+"','"+user_id+"','"
                    +tstatus+"','"+timezone+"','"+machineno+"','"+total_reps+"','"+total_calorie+"','"+total_weight+"','"+gymtotal+"','"+current_time+"','"+tt+"','"+repstime+"');")*/

            /*String seq = "0";

            res10 = db.rawQuery("SELECT name, seq from SQLITE_SEQUENCE where name = ?", new String[]{"dailyrecord"});
            res10.moveToFirst();
            while (res10.isAfterLast() == false) {
                if ((res10 != null) && (res10.getCount() > 0)) {
                    seq = res10.getString(res10.getColumnIndex("seq"));
                }
                res10.moveToNext();
            }

            lastInsertId = Integer.valueOf(seq);*/

            Log.d("lastInsertId",String.valueOf(lastInsertId));
            //lastInsertId = 999;
            String t_tt = "0";
            res3 = db.rawQuery("select count(*) tt from station_log where user_id=? and station_number=? and muscle_id =?", new String[]{user_id,station_no,muscle_id});
            res3.moveToFirst();
            while (res3.isAfterLast() == false) {
                if ((res3 != null) && (res3.getCount() > 0)) {
                    t_tt = res3.getString(res3.getColumnIndex("tt"));
                }
                res3.moveToNext();
            }
            res3.close();

            if(t_tt.equals("0"))
            {

                ContentValues stationValues = new ContentValues();
                stationValues.put("muscle_id", muscle_id);
                stationValues.put("user_id", user_id);
                stationValues.put("station_number", station_no);
                stationValues.put("muscle_type_info", "");
                stationValues.put("favourite_title", "");
                stationValues.put("disclosure_support", "");
                stationValues.put("workoutroutinename", "");
                stationValues.put("scheduleworkoutroutine", "");
                stationValues.put("sheduleDay", "");
                stationValues.put("sheduleweedDays", "");
                stationValues.put("fav", "0");
                stationValues.put("status", "0");
                stationValues.put("IsImported", "0");

                lastInsertId = db.insert("station_log", null, stationValues);

                //res2 = db.rawQuery("INSERT INTO station_log(muscle_id,user_id,station_number) VALUES (?,?,?)", new String[]{muscle_id,user_id,station_no});

                Log.d("Insert StationLog: " , "success : " + lastInsertId);

                //res2.close();
            }else{
                Log.d("station qry", "INSERT INTO station_log(muscle_id,user_id,station_number) VALUES ("+muscle_id+","+user_id+","+station_no+")");
            }

            Log.d("Insert Status: " , "success");


             last_date = get_last_date(user_id, machineno, muscle_id,current_date,station_no);

            if (last_date.equals(""))
            {
                last_date = previous_date;
            }



             last_weight = get_total_weight_by_set(user_id,set_number,machineno,last_date,station_no, muscle_id);

             todays_weight = get_total_weight_by_set(user_id,set_number,machineno,current_date,station_no, muscle_id);

            String last_reps = get_reps_by_set(user_id,set_number,machineno,last_date,station_no, muscle_id);
            String total_reps_by_muscle = get_total_reps_by_muscle_type(user_id,machineno,last_date,set_number,station_no, muscle_id);

             last_reps_display = last_reps + "/" + total_reps_by_muscle;

            String todays_reps = get_reps_by_set(user_id,set_number,machineno,current_date,station_no, muscle_id);
            String today_total_reps_by_muscle = get_total_reps_by_muscle_type(user_id,machineno,current_date,set_number,station_no, muscle_id);

            todays_reps_display = todays_reps + "/" + today_total_reps_by_muscle;

            int last_calorie = get_last_calorie(user_id,muscle_id,station_no,last_date);
            String total_last_calorie = get_last_total_calorie(user_id,last_date);

             last_calorie_display = String.valueOf(last_calorie) + "/" + total_last_calorie;

            int todays_calorie = get_last_calorie(user_id,muscle_id,station_no,current_date);
            String today_total_calorie = get_last_total_calorie(user_id,current_date);

            today_calorie_display = String.valueOf(todays_calorie) + "/" + today_total_calorie;

             last_total_weight = get_total_weight_by_muscle_type(user_id,machineno,last_date,set_number,station_no, muscle_id);

            todays_total_weight = get_total_weight_by_muscle_type(user_id,machineno,current_date,set_number,station_no, muscle_id);

            String newset = "";
            last_gymstarTotal = get_lastresults_gymStar(user_id,last_date,current_date,newset,station_no, muscle_id);

            todays_gymstarTotal = get_weight_gymStar(user_id,current_date,set_number,machineno,station_no, muscle_id);

            //Log.d("todays_total_weight ", todays_total_weight);

        }


        JSONArray last_results = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("last_set", set_number);
        item.put("date", last_date);
        item.put("weight", last_weight);
        item.put("reps", last_reps_display);
        item.put("calorie", last_calorie_display);
        item.put("total", last_total_weight);
        item.put("gymstar_total", last_gymstarTotal);
        last_results.put(item);


        JSONArray today_results = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("last_set", set_number);
        item1.put("date", current_date);
        item1.put("weight", todays_weight);
        item1.put("reps", todays_reps_display);
        item1.put("calorie", today_calorie_display);
        item1.put("total", todays_total_weight);
        item1.put("gymstar_total", todays_gymstarTotal);
        item1.put("current_membership", membershipName);
        item1.put("expiry_date", expiryDate);
        today_results.put(item);

        json.put("status", "1");
        json.put("new_set", set_number);
        json.put("last_set_id", lastInsertId);
        json.put("last_results", item);
        json.put("today_results", item1);

        //Log.d("JSON Output:", json.toString(4));
        return json;
    }

    private String get_weight_gymStar(String user_id, String current_date, String set_number, String machineno, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String totalWeight = "";
        String ttotalWeight = "";
        int set = Integer.valueOf(set_number);
        Cursor res = null;

        String  str = "select  gymtotal from  dailyrecord where user_id = "+user_id+" and date = "+current_date+" and set_number = "+set_number+" and station_no = "+station_no+" and muscle_type = "+muscle_id+" order by id desc";

        Log.d("gymtotal: ", str);
            res = db.rawQuery("select  gymtotal from  dailyrecord where user_id = ? and date = ? and set_number = ? and station_no = ? and muscle_type = ? order by id desc", new String[]{user_id, current_date, set_number, station_no, muscle_id});
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    ttotalWeight = res.getString(res.getColumnIndex("gymtotal"));
                }
                res.moveToNext();
            }
            res.close();

        Log.d("ttotalWeight: ", ttotalWeight);

        if (ttotalWeight.equals(""))
        {
            /*if(set >= 1)
            {
                set = set - 1;

                return get_weight_gymStar(user_id,current_date,set_number,machineno,station_no,muscle_id);
            }
            else
            {
                return "0";
            }*/
            return "0";
        }
        else
        {
            return ttotalWeight;
        }
    }

    private String get_lastresults_gymStar(String user_id, String last_date, String current_date, String newset, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0, lastCount = 0;
        String ttotalWeight = "", gTotal = "", lastresult ="";
        String  set = newset;

        Cursor res,res1,res2,res3;
        res = db.rawQuery("SELECT count(*) get_count FROM dailyrecord WHERE date = ? AND user_id = ?", new String[]{current_date,user_id});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                count = res.getInt(res.getColumnIndex("get_count"));
            }
            res.moveToNext();
        }
        res.close();

        if (count == 0)
        {

            res1 = db.rawQuery("select  reps*Weight as gymtotal from  dailyrecord where user_id = ? and date = ? and set_number = ? and station_no = ? and muscle_type = ? order by id desc", new String[]{user_id,last_date,newset,station_no,muscle_id});
            res1.moveToFirst();
            while (res1.isAfterLast() == false) {
                if ((res1 != null) && (res1.getCount() > 0)) {
                    gTotal = res1.getString(res1.getColumnIndex("gymtotal"));
                }
                res1.moveToNext();
            }
            res1.close();

            if (gTotal != "")
            {
                lastresult = gTotal;
                return lastresult;
            }
		    else
            {
                return "0";
            }
        }else{

            res2 = db.rawQuery("SELECT * FROM dailyrecord WHERE date = ? AND user_id = ? ORDER BY id ASC", new String[]{current_date,user_id});
            res2.moveToFirst();
            while (res2.isAfterLast() == false) {
                if ((res2 != null) && (res2.getCount() > 0)) {
                    String machineno = "";
                    String lastdate = get_last_date(user_id, machineno, res2.getString(res2.getColumnIndex("muscle_type")),current_date,res2.getString(res2.getColumnIndex("station_no")));

                    res3 = db.rawQuery("select  reps*Weight as gymtotal from  dailyrecord where user_id = ? and date = ? and set_number = ? and station_no = ? and muscle_type = ? order by id desc", new String[]{user_id,lastdate,res2.getString(res2.getColumnIndex("set_number")),res2.getString(res2.getColumnIndex("station_no")),res2.getString(res2.getColumnIndex("muscle_type"))});
                    res3.moveToFirst();
                    while (res3.isAfterLast() == false) {
                        if ((res3 != null) && (res3.getCount() > 0)) {
                            lastCount = lastCount + res3.getInt(res3.getColumnIndex("gymtotal"));
                        }
                        res3.moveToNext();
                    }
                    res3.close();
                }
                res2.moveToNext();
            }
            res2.close();

            lastresult = ""+ String.valueOf(lastCount)+"";
            return lastresult;
        }
   }

    private String get_total_weight_by_muscle_type(String user_id, String machineno, String last_date, String set_number, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String totalWeight = "";
        String ttotalWeight = "";
        int set = Integer.valueOf(set_number);

        Cursor res;
        res = db.rawQuery("select total_weight from  dailyrecord where user_id = ? and station_no = ? and muscle_type = ? and date = ? and set_number = ? order by id desc", new String[]{user_id,station_no,muscle_id,last_date,set_number});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                ttotalWeight = res.getString(res.getColumnIndex("total_weight"));
            }
            res.moveToNext();
        }
        res.close();

        if (ttotalWeight.equals(""))
        {
           if(set >= 1)
            {
                set = set - 1;
                return get_total_weight_by_muscle_type(user_id,machineno,machineno,String.valueOf(set),station_no, muscle_id);
            }
            else
            {
                return "0";
            }

        }
	  else
        {
            return ttotalWeight;
        }
    }

    private String get_total_reps_by_muscle_type(String user_id, String machineno, String last_date, String set_number, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String totalReps = "";
        String ttotalReps = "";
        String str = "select total_reps from  dailyrecord where user_id = "+user_id+" and station_no = "+station_no+" and muscle_type = '"+muscle_id+"' and date = '"+last_date+"' and set_number = '"+set_number+"' order by id desc LIMIT 1";

            Cursor res;
            res = db.rawQuery("select total_reps from  dailyrecord where user_id = ? and station_no = ? and muscle_type = ? and date = ? and set_number = ? order by id desc LIMIT 1", new String[]{user_id, station_no, muscle_id, last_date, set_number});

                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    if ((res != null) && (res.getCount() > 0)) {
                        ttotalReps = res.getString(res.getColumnIndex("total_reps"));
                        Log.d("ttotalReps", ttotalReps);
                    }
                    res.moveToNext();
                }

            res.close();

            if (ttotalReps.equals("0") || ttotalReps.equals("")) {
                totalReps = "0";
            } else {
                totalReps = ttotalReps;
            }

            return totalReps;
    }

    private String get_total_weight_by_set(String user_id, String set_number, String machineno, String last_date, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String totalWeight = "";
        String tweight = "";

        Cursor res;
        res = db.rawQuery("select Weight from  dailyrecord where user_id = ? and set_number = ? and station_no = ? and muscle_type = ? and date = ?", new String[]{user_id,set_number,station_no,muscle_id,last_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                tweight = res.getString(res.getColumnIndex("Weight"));
            }
            res.moveToNext();
        }
        res.close();

        String t_total = get_reps_by_set(user_id,set_number,machineno,last_date,station_no, muscle_id);
        if(tweight.equals("0") || tweight.equals("")){
            totalWeight = "0";
        }else{
            totalWeight = String.valueOf(Integer.valueOf(t_total) * Integer.valueOf(tweight));
        }
        return totalWeight;

    }


    private String get_reps_by_set(String user_id,String set,String machineno,String current_date,String station_no, String muscle_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String reps = "";
        String treps = "";

        Cursor res;
        res = db.rawQuery("select reps from  dailyrecord where user_id = ? and set_number = ? and station_no = ? and muscle_type = ? and date = ?", new String[]{user_id,set,station_no,muscle_id,current_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                treps = res.getString(res.getColumnIndex("reps"));
            }
            res.moveToNext();
        }
        res.close();

        if(treps.equals("0") || treps.equals("")){
            reps = "0";
        }else{
            reps = treps;
        }
        return reps;
    }


    private String get_last_date(String user_id, String machineno, String muscle_id, String current_date, String station_no) {

        SQLiteDatabase db = this.getReadableDatabase();

        String maxdate = "";
        String tmaxdate = "";

        Cursor res;
        res = db.rawQuery("SELECT IFNULL(max(date), '') as maxdate FROM dailyrecord WHERE muscle_type = ? AND station_no = ? AND user_id = ? AND date < ?", new String[]{muscle_id,station_no,user_id,current_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                tmaxdate = res.getString(res.getColumnIndex("maxdate"));
            }
            res.moveToNext();
        }
        res.close();

        if(tmaxdate.equals("0") || tmaxdate.equals("")){
            maxdate = "";
        }else{
            maxdate = tmaxdate;
        }
        return maxdate;

    }

    private String get_last_total_calorie(String user_id, String current_date) {

        SQLiteDatabase db = this.getReadableDatabase();

        String calorie = "0";
        String tcalorie = "0";

        Cursor res;
        res = db.rawQuery("select total_calorie from  dailyrecord where user_id = ? and date=? order by id desc limit 1", new String[]{user_id,current_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                tcalorie = res.getString(res.getColumnIndex("total_calorie"));
            }
            res.moveToNext();
        }
        res.close();

        if(tcalorie.equals("0") || tcalorie.equals("")){
            calorie = "0";
        }else{
            calorie = tcalorie;
        }
        return calorie;
    }

    private int get_last_calorie(String user_id, String muscle_id, String station_no, String current_date) {

        SQLiteDatabase db = this.getReadableDatabase();

        int calorie = 0;
        String tcalorie = "0";

        Cursor res;
        res = db.rawQuery("select calorie from  dailyrecord where user_id = ? and muscle_type = ? and station_no = ? and date= ? order by id desc limit 1", new String[]{user_id,muscle_id,station_no,current_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                tcalorie = res.getString(res.getColumnIndex("calorie"));
            }
            res.moveToNext();
        }
        res.close();

        if(tcalorie.equals("0") || tcalorie.equals("")){
            calorie = 0;
        }else{
            calorie = Integer.valueOf(tcalorie);
        }

        return calorie;
    }

    private String get_new_set_id_muscle_type(String user_id, String machineno, String current_date, String station_no, String muscle_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String setId = "0";
        String tset = "0";

        //Log.d("Query:" , "select max(set_number) tset from  dailyrecord where user_id ="+user_id+" and station_no = "+station_no+" and muscle_type = "+muscle_id+" and date = "+current_date);
        Cursor res;
        res = db.rawQuery("select IFNULL(max(set_number),0) as tset from  dailyrecord where user_id = ? and station_no = ? and muscle_type = ? and date = ?", new String[]{user_id,station_no,muscle_id,current_date});
        res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    tset = res.getString(res.getColumnIndex("tset"));
                }
                res.moveToNext();
            }
            res.close();

            setId = String.valueOf(Integer.valueOf(tset)  + 1);

       // Log.d("setId:" , setId);
        return setId;
    }

    private String get_new_machine_for_new_entry(String user_id, String muscle_id, String station_no, String current_date) {

        SQLiteDatabase db = this.getReadableDatabase();

        String machineNo = "0";
        String tset = "0";
        int machno = 0;
        String newMachno = "0";

         Log.d("Thiyag Params:", user_id + "-"+ muscle_id + "-" + station_no + "-" +  current_date);
        Cursor res, res1, res2;
        res = db.rawQuery("select IFNULL(max(set_number), 0) as tset from  dailyrecord where user_id = ? and muscle_type = ? and station_no = ? and date = ? LIMIT 1", new String[]{user_id,muscle_id,station_no,current_date});

        String qry = "select IFNULL(max(set_number), 0) tset from  dailyrecord where user_id = '"+user_id+"' and muscle_type = '"+muscle_id+"' and station_no = '"+station_no+"' and date = '"+current_date+"' LIMIT 1";
        //Log.d("first query ", qry);

        if (res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                   // Log.d("machineno ", "first query");
                    tset = res.getString(res.getColumnIndex("tset"));
                }
                res.moveToNext();
            }
            res.close();
        }


        if(tset.equals("0")){

            res1 = db.rawQuery("select IFNULL(max(machineno),0) as machno from  dailyrecord where user_id=? and date=? LIMIT 1", new String[]{user_id,current_date});
            res1.moveToFirst();
                while (res1.isAfterLast() == false) {
                    if ((res1 != null) && (res1.getCount() > 0)) {
                     //   Log.d("machineno ", "second query");
                        machno = res1.getInt(res1.getColumnIndex("machno"));
                    }
                    res1.moveToNext();
                }
                res1.close();
                newMachno = String.valueOf((machno + 1));
            machineNo = newMachno;

        }else{

            res2 = db.rawQuery("select machineno  from  dailyrecord where user_id=? and muscle_type=? and station_no=? and date=? LIMIT 1", new String[]{user_id,muscle_id,station_no,current_date});
            res2.moveToFirst();
            while (res2.isAfterLast() == false) {
                if ((res2 != null) && (res2.getCount() > 0)) {
                 //   Log.d("machineno ", "third query");
                    machno = res2.getInt(res2.getColumnIndex("machineno"));
                }
                res2.moveToNext();
            }
            res2.close();
            machineNo = String.valueOf(machno);
        }

        //Log.d("machineNo ", machineNo);
        return machineNo;
    }


    public JSONObject get_membership_details(String userId) throws JSONException {

        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray stationListArray = new JSONArray();

        Cursor res;
        res = db.rawQuery("SELECT `wp_pmpro_membership_levels`.name, IFNULL(DATE(`wp_pmpro_memberships_users`.`enddate`),'0000-00-00') as enddate  FROM `wp_pmpro_memberships_users`,`wp_pmpro_membership_levels` WHERE `wp_pmpro_memberships_users`.`membership_id` = `wp_pmpro_membership_levels`.`id` AND `user_id` = ? ORDER BY `wp_pmpro_memberships_users`.`id` DESC LIMIT 1", new String[]{userId});


        res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    JSONObject item = new JSONObject();
                    item.put("name", res.getString(res.getColumnIndex("name")));
                    item.put("enddate", res.getString(res.getColumnIndex("enddate")));
                    stationListArray.put(item);
                }
                res.moveToNext();
            }
        res.close();

        json.put("data", stationListArray);
        return json;
    }


    public String get_muscle_id(String muscle_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String muscleId = "0";

        Cursor res;
        res = db.rawQuery("SELECT app_muscle_name, wp_post_cat_id, wp_category FROM app_muscle_post_mapping WHERE UPPER(app_muscle_name) = UPPER(?)", new String[]{muscle_name});
        if (res.moveToFirst()) {
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    muscleId = res.getString(res.getColumnIndex("wp_post_cat_id"));
                }
                res.moveToNext();
            }
        } else {
            if (muscleId == "0") {
                res = db.rawQuery("SELECT id FROM wp_posts WHERE post_title = ?", new String[]{muscle_name});
                if (res.moveToFirst()) {
                    while (res.isAfterLast() == false) {
                        if ((res != null) && (res.getCount() > 0)) {
                            muscleId = res.getString(res.getColumnIndex("id"));
                        }
                        res.moveToNext();
                    }
                }
            }
        }
        res.close();

        return muscleId;
    }

    public JSONObject getLastResults(String user_id, String station_no, String muscle_name, String current_date, String current_time, String previous_date)  throws JSONException{

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject json = new JSONObject();
        JSONArray stationListArray = new JSONArray();
        String expiryDate = "", membershipName = "", muscle_id="", machineno ="", lastset ="", set ="", newset="", last_date="";
        String last_weight = "", total_last_weight = "", last_weight_display = "", last_reps="", total_reps="", reps_display="";
        String calorie_display = "", last_calorie = "", total_calorie = "", today_reps_display = "", todays_reps = "", total_today_reps = "", last_total ="", todays_total = "";
        String today_calorie_display = "", todays_calorie = "", total_today_calorie = "", last_gymstarTotal = "", todays_gymstarTotal ="";
        String tt = "", repstime = "0";
        String todays_weight_display = "", todays_weight = "", todays_total_weight = "";

        Boolean timer = false;

        JSONObject user_membership_details = get_membership_details(user_id);
        JSONArray jsonArray = user_membership_details.getJSONArray("data");
        int length = jsonArray.length();
        if(length > 0) {
            for (int i = 0; i < length; i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                expiryDate = (String) item.getString("enddate");
                membershipName = (String) item.getString("name");

            }
        }else{
            expiryDate = "0000-00-00";
            membershipName = "";
        }

        if (user_id.equals("") || muscle_name.equals("") || station_no.equals(""))
        {
            json.put("status", "0");
            json.put("message", "User ID or Muscle Name or Station No is missing in param");

        }
        else {

            if (muscle_name == "XTraM") {
                muscle_name = "XTra Muscle";
            }

            muscle_id = get_muscle_id(muscle_name);

            machineno = get_new_machine_for_new_entry(user_id, muscle_id, station_no, current_date);

            set = get_current_set_id_muscle_type(user_id, machineno, current_date, station_no, muscle_id);

            if (set != "") {
                lastset = set;
            } else {
                lastset = "0";
            }

            set = get_new_set_id_muscle_type(user_id, machineno, current_date, station_no, muscle_id);

            if (set != "") {
                newset = set;
            } else {
                newset = "1";
            }

            last_date = get_last_date(user_id, machineno, muscle_id, current_date, station_no);


            if (last_date == null || last_date.equals("")) {
                last_date = "";
            }


            last_weight = get_weight_by_set(user_id, newset, machineno, last_date, station_no, muscle_id);
            total_last_weight = get_total_weight_by_set(user_id, newset, machineno, last_date, station_no, muscle_id);

            last_weight_display = last_weight + "/" + total_last_weight;

            todays_weight = get_weight_by_set(user_id, newset, machineno, current_date, station_no, muscle_id);
            todays_total_weight = get_total_weight_by_set(user_id, newset, machineno, current_date, station_no, muscle_id);

            todays_weight_display = todays_weight + "/" + todays_total_weight;

            last_reps = get_reps_by_set(user_id, newset, machineno, last_date, station_no, muscle_id);
            total_reps = get_total_reps_by_muscle_type(user_id, machineno, last_date, newset, station_no, muscle_id);

            Log.d("total_reps", total_reps);
            reps_display = last_reps + "/" + total_reps;
            Log.d("reps_display", reps_display);

            last_calorie = String.valueOf(get_last_calorie(user_id, muscle_id, station_no, last_date));
            total_calorie = get_last_total_calorie(user_id, last_date);

            calorie_display = last_calorie + "/" + total_calorie;


            todays_reps = get_reps_by_set(user_id, newset, machineno, current_date, station_no, muscle_id);
            total_today_reps = get_total_reps_by_muscle_type(user_id, machineno, current_date, newset, station_no, muscle_id);

            today_reps_display = todays_reps + "/" + total_today_reps;

            last_total = get_total_weight_by_muscle_type(user_id, machineno, last_date, newset, station_no, muscle_id);

            todays_total = get_total_weight_by_muscle_type(user_id, machineno, current_date, newset, station_no, muscle_id);

            todays_calorie = String.valueOf(get_last_calorie(user_id, muscle_id, station_no, current_date));
            total_today_calorie = get_last_total_calorie(user_id, current_date);

            today_calorie_display = todays_calorie + "/" + total_today_calorie;

            last_gymstarTotal = get_lastresults_gymStar(user_id, last_date, current_date, newset, station_no, muscle_id);

            todays_gymstarTotal = get_weight_gymStar(user_id, current_date, newset, machineno, station_no, muscle_id);

            Cursor res;
            res = db.rawQuery("Select * from  `dailyrecord` where `user_id` = ? and `station_no` = ?  and `set_number` = ? and `date` =  ? AND `muscle_type` = ? order by id desc", new String[]{user_id, station_no, newset, last_date, muscle_id});
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    tt = res.getString(res.getColumnIndex("tt"));
                    repstime = res.getString(res.getColumnIndex("repstime"));
                }
                res.moveToNext();
            }

            if (tt.equals("") || tt == null) {
                tt = "0.00";
            }

            if (repstime.equals("") || repstime == null) {
                repstime = "0.00";
            }
            res.close();


            //Log.d("DB muscle: ", "-"+muscle_name.trim()+"-");
            String mus = muscle_name.trim();
            if (mus.equals("CrossTrain")) {
                timer = true;
            } else if (mus.equals("XTraM") || mus.equals("XTra Muscle")) {
                timer = true;
            }

        }

            JSONObject last_results = new JSONObject();
            last_results.put("last_set", newset);
            last_results.put("date", last_date);
            last_results.put("weight", last_weight_display);
            last_results.put("reps", reps_display);
            last_results.put("calorie", calorie_display);
            last_results.put("total", last_total);
            last_results.put("gymstar_total", last_gymstarTotal);


            JSONObject today_results = new JSONObject();
            today_results.put("last_set", newset);
            today_results.put("date", current_date);
            today_results.put("weight", todays_weight_display);
            today_results.put("reps", today_reps_display);
            today_results.put("calorie", today_calorie_display);
            today_results.put("total", todays_total);
            today_results.put("gymstar_total", todays_gymstarTotal);


            json.put("new_set", newset);
            json.put("last_results", last_results);
            json.put("today_results", today_results);
            json.put("current_membership", membershipName);
            json.put("expiry_date", expiryDate);
            json.put("total_time", tt);
            json.put("repstime", repstime);
            json.put("timer", timer);

           // Log.d("DB timer: ", String.valueOf(timer));

        return json;
    }

    public JSONObject getRoutineGymTotal(String user_id)  throws JSONException{

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject json = new JSONObject();
        JSONArray stationListArray = new JSONArray();

        String gymTotal = "0";


            Cursor res;
            res = db.rawQuery("SELECT gymtotal FROM `dailyrecord` where user_id = ? and date = date('now') order by gymtotal desc limit 1", new String[]{user_id});
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    gymTotal = res.getString(res.getColumnIndex("gymtotal"));
                }
                res.moveToNext();
            }

            res.close();

          json.put("gymTotal", gymTotal);

        return json;
    }

    private String get_weight_by_set(String user_id, String newset, String machineno, String last_date, String station_no, String muscle_id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String setWeight = "0";

        Cursor res;
        res = db.rawQuery("select Weight from  dailyrecord where user_id = ? and set_number = ? and station_no = ? and muscle_type = ? and date = ?", new String[]{user_id,newset,station_no,muscle_id,last_date});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                setWeight = res.getString(res.getColumnIndex("Weight"));
            }
            res.moveToNext();
        }

        if(setWeight.equals("") || setWeight == null){
            setWeight = "0";
        }

        res.close();

        return setWeight;
    }

    private String get_current_set_id_muscle_type(String user_id, String machineno, String current_date, String station_no, String muscle_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String setId = "0";

        Cursor res;
        res = db.rawQuery("select max(set_number) tset from  dailyrecord where user_id = ? and station_no = ? and muscle_type = ? and date = ?", new String[]{user_id,station_no,muscle_id,current_date});
        res.moveToFirst();
            while (res.isAfterLast() == false) {
                if ((res != null) && (res.getCount() > 0)) {
                    setId = res.getString(res.getColumnIndex("tset"));
                }
                res.moveToNext();
            }
        res.close();

        return setId;
    }


    public void  setExpiryDate(String user_id) {
        SQLiteDatabase db = this.getReadableDatabase();


        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put("enddate", "2019-08-31 12:00:00");
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(user_id)};

        db.update("wp_pmpro_memberships_users", dataToInsert, where, whereArgs);

    }

    public JSONObject updateGymRecord(String set_id, String tt, String repstime)   throws JSONException{

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject json = new JSONObject();
        set_id = set_id.trim();
        String tt_time = tt.trim();
        String reps_time = repstime.trim();
        Boolean recordExist = false;



        String[] reps_time_split = reps_time.split("\\.");


        int len = reps_time_split.length;
        repstime = reps_time_split[0].replace(':', '.');

        String[] tt_time_split = tt_time.split("\\.");
        tt_time = tt_time_split[0].replace(':', '.');

        if (set_id.equals(""))
        {
            json.put("status", "0");
            json.put("message", "Set id is missing in param");
        }
        else if (repstime.equals(""))
        {
            json.put("status", "0");
            json.put("message", "Reps time cannot be left blank");
        }
        else if (tt_time.equals(""))
        {
            json.put("status", "0");
            json.put("message", "Total Time cannot be left blank");
        }
        else
        {
            ContentValues newValues = new ContentValues();
            newValues.put("tt", tt_time);
            newValues.put("repstime", repstime);

            int status = db.update("dailyrecord", newValues, "id" + " = ? ",  new String[]{set_id});

            if(status == 0){
                json.put("status", "1");
            }else{
                Cursor res;
                res = db.rawQuery("SELECT * FROM dailyrecord WHERE id=? AND tt=? AND repstime = ?", new String[]{set_id,tt_time,reps_time});
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    if ((res != null) && (res.getCount() > 0)) {
                        recordExist = true;
                        break;
                    }
                    res.moveToNext();
                }
                res.close();
            }

            if(recordExist){
                json.put("status", "1");
            }else{
                json.put("status", "0");
                json.put("message", "Error in updating set info");
            }
        }


       return json;

    }

    public JSONObject getMaxWeightByMuscle(String userid, String muscleName, String station_no) throws JSONException{

        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject json = new JSONObject();


        String muscle_name = muscleName.trim();
        station_no = station_no.trim();

        String maxWeight = "", maxWeightStationNo = "";
        String maxWeightH_Total = "", maxWeightH_TotalStationNo = "";
        String maxEffort24 = "", maxEffort24StationNo = "";
        String maxEffort7 = "", maxEffort7StationNo = "";

        if(muscle_name == "XTraM"){ muscle_name = "XTra Muscle"; }
        String muscle_id = get_muscle_id(muscle_name);


        Cursor res;
        res = db.rawQuery("select * from dailyrecord where Weight = (select MAX(Weight) from dailyrecord where user_id=? and muscle_type = ? and station_no = ?) and user_id=? and muscle_type = ?", new String[]{userid,muscle_id,station_no,userid,muscle_id});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                maxWeight = res.getString(res.getColumnIndex("Weight"));
                maxWeightStationNo = res.getString(res.getColumnIndex("station_no"));
            }
            res.moveToNext();
        }
        res.close();


        Cursor res1;
        res1 = db.rawQuery("select * from dailyrecord where total_weight  = (select MAX(total_weight) from dailyrecord where user_id= ? and muscle_type = ? and station_no = ?) and user_id=? and muscle_type = ?", new String[]{userid,muscle_id,station_no,userid,muscle_id});
        res1.moveToFirst();
        while (res1.isAfterLast() == false) {
            if ((res1 != null) && (res1.getCount() > 0)) {
                maxWeightH_Total = res1.getString(res1.getColumnIndex("total_weight"));
                maxWeightH_TotalStationNo = res1.getString(res1.getColumnIndex("station_no"));
            }
            res1.moveToNext();
        }
        res1.close();


        Cursor res2;

        //select sum(tw) as tw, date, group_concat(DISTINCT station_no order by station_no SEPARATOR ' - ') as station_no from (select day_date, d.muscle_type, d.user_id, max(d.total_weight) as tw, d.date, d.station_no from tbl_year_days y JOIN dailyrecord d ON d.date = y.day_date WHERE d.user_id = ".$user_id." AND d.muscle_type= ".$muscle_id." group by date, machineno) A group by date order by tw desc LIMIT 1"

        res2 = db.rawQuery("select sum(tw) as tw, date, group_concat(DISTINCT station_no) as station_no from (select day_date, d.muscle_type, d.user_id, max(d.total_weight) as tw, d.date, d.station_no from tbl_year_days y JOIN dailyrecord d ON d.date = y.day_date WHERE d.user_id = ? AND d.muscle_type= ? group by date, machineno) A group by date order by tw desc LIMIT 1", new String[]{userid,muscle_id});
        res2.moveToFirst();
        while (res2.isAfterLast() == false) {
            if ((res2 != null) && (res2.getCount() > 0)) {
                maxEffort24 = res2.getString(res2.getColumnIndex("tw"));

                String tempStr = res2.getString(res2.getColumnIndex("station_no"));

                String[] arrOfStr = tempStr.split(",");
                Arrays.sort(arrOfStr);
               // maxEffort24StationNo = String.join(" - ", arrOfStr);

                maxEffort24StationNo = TextUtils.join(", ", arrOfStr);
                //maxEffort24StationNo = res2.getString(res2.getColumnIndex("station_no")).replace(",", " - ");
            }
            res2.moveToNext();
        }
        res2.close();


        Cursor res3;
        res3 = db.rawQuery("select sum(tw) as tw, DATE('%d%m',start_date) as start_date, DATE('%d%m %Y',end_date) as end_date, group_concat(DISTINCT station_no ) as station_no from (select start_date, end_date, d.muscle_type, d.user_id, max(d.total_weight) as tw, d.date, d.station_no from tbl_year_weeks y JOIN dailyrecord d ON d.date BETWEEN y.start_date AND y.end_date WHERE d.user_id = ?  AND d.muscle_type= ? group by date, station_no) A group by end_date order by tw desc limit 1", new String[]{userid,muscle_id});
        res3.moveToFirst();
        while (res3.isAfterLast() == false) {
            if ((res3 != null) && (res3.getCount() > 0)) {
                maxEffort7 = res3.getString(res3.getColumnIndex("tw"));
                String tempStr = res3.getString(res3.getColumnIndex("station_no"));

                String[] arrOfStr = tempStr.split(",");
                Arrays.sort(arrOfStr);
                //maxEffort7StationNo = String.join(" - ", arrOfStr);
                maxEffort7StationNo = TextUtils.join(", ", arrOfStr);
                //maxEffort7StationNo = res3.getString(res3.getColumnIndex("station_no")).replace(",", " - ");
            }
            res3.moveToNext();
        }
        res3.close();

        JSONObject values1 = new JSONObject();
        values1.put("muValueMaxWeight", maxWeight);
        values1.put("muValueMaxWeight_HTotal", maxWeightH_Total);
        values1.put("muValueMaxEffort_24", maxEffort24);
        values1.put("muValueMaxEffort_7", maxEffort7);

        JSONObject station_values = new JSONObject();
        station_values.put("muValueMaxWeight", maxWeightStationNo);
        station_values.put("muValueMaxWeight_HTotal", maxWeightH_TotalStationNo);
        station_values.put("muValueMaxEffort_24", maxEffort24StationNo);
        station_values.put("muValueMaxEffort_7", maxEffort7StationNo);

        JSONObject values2 = new JSONObject();
        values2.put("stations", station_values);
        values2.put("values", values1);


        json.put("status", "success");
        json.put("values", values2);

        return json;
    }

    public Boolean getSyncStatus(String userId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Boolean status = false;
        int stationLog = 0, dailyRecord = 0;

        Cursor res;
        res = db.rawQuery("select count(*) as count from  station_log where user_id = ? and offline_sync = 0", new String[]{userId});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                stationLog = res.getInt(res.getColumnIndex("count"));
            }
            res.moveToNext();
        }
        res.close();

        if(stationLog != 0){
            status = true;
        }else{
            Cursor res1;
            res1 = db.rawQuery("select count(*) as count from  dailyrecord where user_id = ? and offline_sync = 0", new String[]{userId});
            res1.moveToFirst();
            while (res1.isAfterLast() == false) {
                if ((res1 != null) && (res1.getCount() > 0)) {
                    dailyRecord = res1.getInt(res1.getColumnIndex("count"));
                }
                res1.moveToNext();
            }
            res1.close();

            if(dailyRecord != 0){
                status = true;
            }else{
                status = false;
            }

        }


        //Log.d("sync status : ", String.valueOf(status));
        return status;

    }


    public JSONObject getExportData(String userId) throws JSONException {
        JSONObject json = new JSONObject();

        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray stationLogArray = new JSONArray();
        JSONArray dailyRecordsArray = new JSONArray();

        Cursor res;
        res = db.rawQuery("SELECT *   FROM `station_log` WHERE `user_id` = ? AND offline_sync = 0 ORDER BY `id` ", new String[]{userId});


        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0)) {
                JSONObject item = new JSONObject();
                item.put("id", res.getString(res.getColumnIndex("id")));
                item.put("muscle_id", res.getString(res.getColumnIndex("muscle_id")));
                item.put("muscle_type_info", res.getString(res.getColumnIndex("muscle_type_info")));
                //item.put("muscle_type_info", res.getString(res.getColumnIndex("muscle_type_info")));
                item.put("user_id", res.getString(res.getColumnIndex("user_id")));
                item.put("disclosure_support", res.getString(res.getColumnIndex("disclosure_support")));
                item.put("favourite_title", res.getString(res.getColumnIndex("favourite_title")));
                item.put("workoutroutinename", res.getString(res.getColumnIndex("workoutroutinename")));
                item.put("scheduleworkoutroutine", res.getString(res.getColumnIndex("scheduleworkoutroutine")));
                item.put("sheduleDay", res.getString(res.getColumnIndex("sheduleDay")));
                item.put("sheduleweedDays", res.getString(res.getColumnIndex("sheduleweedDays")));
                item.put("fav", res.getString(res.getColumnIndex("fav")));
                item.put("status", res.getString(res.getColumnIndex("status")));
                item.put("IsImported", res.getString(res.getColumnIndex("IsImported")));
                item.put("station_number", res.getString(res.getColumnIndex("station_number")));
                item.put("created_at", res.getString(res.getColumnIndex("created_at")));

                stationLogArray.put(item);
            }
            res.moveToNext();
        }
        res.close();


        Cursor res1;
        res1 = db.rawQuery("SELECT *   FROM `dailyrecord` WHERE `user_id` = ? AND offline_sync = 0 ORDER BY `id` ", new String[]{userId});


        res1.moveToFirst();
        while (res1.isAfterLast() == false) {
            if ((res1 != null) && (res1.getCount() > 0)) {
                JSONObject item = new JSONObject();
                item.put("id", res1.getString(res1.getColumnIndex("id")));
                item.put("muscle_type", res1.getString(res1.getColumnIndex("muscle_type")));
                item.put("station_no", res1.getString(res1.getColumnIndex("station_no")));
                item.put("set_number", res1.getString(res1.getColumnIndex("set_number")));
                item.put("reps", res1.getString(res1.getColumnIndex("reps")));
                item.put("calorie", res1.getString(res1.getColumnIndex("calorie")));
                item.put("Weight", res1.getString(res1.getColumnIndex("Weight")));
                item.put("date", res1.getString(res1.getColumnIndex("date")));
                item.put("user_id", res1.getString(res1.getColumnIndex("user_id")));
                item.put("status", res1.getString(res1.getColumnIndex("status")));
                item.put("timezone", res1.getString(res1.getColumnIndex("timezone")));
                item.put("machineno", res1.getString(res1.getColumnIndex("machineno")));
                item.put("total_reps", res1.getString(res1.getColumnIndex("total_reps")));
                item.put("total_calorie", res1.getString(res1.getColumnIndex("total_calorie")));
                item.put("total_weight", res1.getString(res1.getColumnIndex("total_weight")));
                item.put("gymtotal", res1.getString(res1.getColumnIndex("gymtotal")));
                item.put("time", res1.getString(res1.getColumnIndex("time")));
                item.put("favourite", res1.getString(res1.getColumnIndex("favourite")));
                item.put("tt", res1.getString(res1.getColumnIndex("tt")));
                item.put("repstime", res1.getString(res1.getColumnIndex("repstime")));
                item.put("IsImported", res1.getString(res1.getColumnIndex("IsImported")));
                item.put("currenttime", res1.getString(res1.getColumnIndex("currenttime")));

                dailyRecordsArray.put(item);
            }
            res1.moveToNext();
        }
        res1.close();

        //Log.d("dailyRecordsArray : " , String.valueOf(dailyRecordsArray.length()));
        json.put("stationLog", stationLogArray);
        json.put("dailyRecords", dailyRecordsArray);
        return json;
    }


    public String updateSyncStatus(String userId, String records, String station) {

        String status = "success";
       // JSONArray station = jsonObject.getJSONArray("stationLog");
       // JSONArray records = jsonObject.getJSONArray("dailyRecords");

        //Log.d("records", records);

        SQLiteDatabase db = this.getReadableDatabase();

        int stationLog = 0, dailyRecord = 0;

        if(!records.equals("")){
            String query = "UPDATE dailyrecord SET offline_sync = 1 WHERE id IN ("+records+")";
            db.execSQL(query);
        }

        if(!station.equals("")) {
            String query1 = "UPDATE station_log SET offline_sync = 1 WHERE id IN (" + station + ")";
            db.execSQL(query1);
        }

        return status;
    }

    public String syncLocalDB(String userId, JSONArray records, JSONArray station, JSONArray yearDays, JSONArray yearWeeks, JSONArray wppmprousers, JSONArray wpterms) {

        String status = "success";
        SQLiteDatabase db = this.getReadableDatabase();
        long lastInsertId;

        db.beginTransaction();
        try {

            /*db.execSQL("delete from dailyrecord where user_id = " + userId);
            db.execSQL("delete from station_log where user_id = " + userId);*/
            db.execSQL("delete from dailyrecord");
            db.execSQL("delete from station_log");
            db.execSQL("delete from tbl_year_days");
            db.execSQL("delete from tbl_year_weeks");
            db.execSQL("delete from wp_pmpro_memberships_users");
            db.execSQL("delete from wp_terms");


            for (int i = 0; i < records.length(); i++) {

                JSONObject value = records.getJSONObject(i);

                ContentValues newValues = new ContentValues();
                newValues.put("id", value.getString("id"));
                newValues.put("muscle_type", value.getString("muscle_type"));
                newValues.put("station_no", value.getString("station_no"));
                newValues.put("set_number", value.getString("set_number"));
                newValues.put("reps", value.getString("reps"));
                newValues.put("calorie", value.getString("calorie"));
                newValues.put("Weight", value.getString("Weight"));
                newValues.put("date", value.getString("date"));
                newValues.put("user_id", value.getString("user_id"));
                newValues.put("status", value.getString("status"));
                newValues.put("timezone", value.getString("timezone"));
                newValues.put("machineno", value.getString("machineno"));
                newValues.put("total_reps", value.getString("total_reps"));
                newValues.put("total_calorie", value.getString("total_calorie"));
                newValues.put("total_weight", value.getString("total_weight"));
                newValues.put("gymtotal", value.getString("gymtotal"));
                newValues.put("time", value.getString("time"));
                newValues.put("tt", value.getString("tt"));
                newValues.put("repstime", value.getString("repstime"));
                newValues.put("IsImported", value.getString("IsImported"));
                newValues.put("currenttime", value.getString("currenttime"));
                newValues.put("offline_sync", "1");

                lastInsertId = db.insert("dailyrecord", null, newValues);
            }


            for (int i = 0; i < station.length(); i++) {

                JSONObject value = station.getJSONObject(i);

                ContentValues stationValues = new ContentValues();
                stationValues.put("id", value.getString("id"));
                stationValues.put("muscle_id", value.getString("muscle_id"));
                stationValues.put("user_id", value.getString("user_id"));
                stationValues.put("station_number", value.getString("station_number"));
                stationValues.put("muscle_type_info", value.getString("muscle_type_info"));
                stationValues.put("favourite_title", value.getString("favourite_title"));
                stationValues.put("disclosure_support", value.getString("disclosure_support"));
                stationValues.put("workoutroutinename", value.getString("workoutroutinename"));
                stationValues.put("scheduleworkoutroutine", value.getString("scheduleworkoutroutine"));
                stationValues.put("sheduleDay", value.getString("sheduleDay"));
                stationValues.put("sheduleweedDays", value.getString("sheduleweedDays"));
                stationValues.put("fav", value.getString("fav"));
                stationValues.put("status", value.getString("status"));
                stationValues.put("IsImported", value.getString("IsImported"));
                stationValues.put("created_at", value.getString("created_at"));
                stationValues.put("offline_sync", "1");

                lastInsertId = db.insert("station_log", null, stationValues);
            }


            for (int i = 0; i < yearDays.length(); i++) {

                JSONObject value = yearDays.getJSONObject(i);

                ContentValues dayValues = new ContentValues();
                dayValues.put("id", value.getString("id"));
                dayValues.put("day_date", value.getString("day_date"));

                lastInsertId = db.insert("tbl_year_days", null, dayValues);
            }

            for (int i = 0; i < yearWeeks.length(); i++) {

                JSONObject value = yearWeeks.getJSONObject(i);

                ContentValues weekValues = new ContentValues();
                weekValues.put("id", value.getString("id"));
                weekValues.put("start_date", value.getString("start_date"));
                weekValues.put("end_date", value.getString("end_date"));

                lastInsertId = db.insert("tbl_year_weeks", null, weekValues);
            }


            for (int i = 0; i < wppmprousers.length(); i++) {

                JSONObject value = wppmprousers.getJSONObject(i);

                ContentValues wppmprouserValues = new ContentValues();
                wppmprouserValues.put("id", value.getString("id"));
                wppmprouserValues.put("user_id", value.getString("user_id"));
                wppmprouserValues.put("membership_id", value.getString("membership_id"));
                wppmprouserValues.put("code_id", value.getString("code_id"));
                wppmprouserValues.put("initial_payment", value.getString("initial_payment"));
                wppmprouserValues.put("billing_amount", value.getString("billing_amount"));
                wppmprouserValues.put("cycle_number", value.getString("cycle_number"));
                wppmprouserValues.put("cycle_period", value.getString("cycle_period"));
                wppmprouserValues.put("billing_limit", value.getString("billing_limit"));
                wppmprouserValues.put("trial_amount", value.getString("trial_amount"));
                wppmprouserValues.put("trial_limit", value.getString("trial_limit"));
                wppmprouserValues.put("status", value.getString("status"));
                wppmprouserValues.put("startdate", value.getString("startdate"));
                wppmprouserValues.put("enddate", value.getString("enddate"));
                wppmprouserValues.put("modified", value.getString("modified"));


                lastInsertId = db.insert("wp_pmpro_memberships_users", null, wppmprouserValues);
            }

            for (int i = 0; i < wpterms.length(); i++) {

                JSONObject value = wpterms.getJSONObject(i);

                ContentValues wptermsValues = new ContentValues();
                wptermsValues.put("term_id", value.getString("term_id"));
                wptermsValues.put("name", value.getString("name"));
                wptermsValues.put("slug", value.getString("slug"));
                wptermsValues.put("term_group", value.getString("term_group"));


                lastInsertId = db.insert("wp_terms", null, wptermsValues);
            }

            db.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return status;

    }
}
