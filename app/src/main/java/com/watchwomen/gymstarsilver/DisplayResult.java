package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayResult extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private DatabaseTransactions DBTransact;
    private SimpleGestureFilter detector;
    ImageView backarr;
    String calcResults,repsval,weightval,stationval,muscleval,date_last,weight_last,reps_last,total_last,
            gymstar_total_last,last_set_today,current_date,previous_date,current_time;
    ProgressDialog mProgressDialog;
    TextView disprepstoday,disprepslast,dispweighttoday,dispweightlast,disptotaltoday,disptotallast,dispGStarTltoday,dispGStarTllast,
            lastresultdate,setdisplay,headertext,caloriedisplay, timedisplay, timeTotalDisplay,tvStartimer,lastresulttime,tvDisplayheadertext;

    ImageView favoritebutton;
    LinearLayout timeLayout;
    int timeValue = 0, totalTimeValue = 0;
    Timer timer;
    TimerTask timerTask;
    Handler handler = new Handler();
    private static final String TAG = "DisplayResult";
    LinearLayout timer_display;
    int storeTime=0;
    String convertedTotalTime;
    TinyDB tinydb;
    String update_gym_recordResults;
    String last_set_id;
    String wr_routine;
    String wr_schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        tinydb          = new TinyDB(DisplayResult.this);
        detector        = new SimpleGestureFilter(this,this);
        Bundle bundle   = getIntent().getExtras();
        muscleval       = bundle.getString("muscle_name");
        weightval       = bundle.getString("weight");
        stationval      = bundle.getString("station_no");
        repsval         = bundle.getString("reps");
//        timeValue       = bundle.getInt("time");
        current_date    = bundle.getString("currentDate");
        current_time    = bundle.getString("currentTime");
        previous_date   = bundle.getString("previousDate");


        wr_routine = bundle.getString("wr_routine_name");
        wr_schedule = bundle.getString("wr_schedule");

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display_result);

        timedisplay     =(TextView) findViewById(R.id.timeValue);
        headertext      =(TextView)findViewById(R.id.textView2);
        tvDisplayheadertext=(TextView)findViewById(R.id.displayheadertext);
        setdisplay      =(TextView)findViewById(R.id.setdisplay);
        disprepstoday   =(TextView)findViewById(R.id.disprepstoday);
        disprepslast    =(TextView)findViewById(R.id.disprepslast);
        dispweighttoday =(TextView)findViewById(R.id.dispweighttoday);
        dispweightlast  =(TextView)findViewById(R.id.dispweightlast);
        disptotaltoday  =(TextView)findViewById(R.id.disptotaltoday);
        disptotallast   =(TextView)findViewById(R.id.disptotallast);
        dispGStarTltoday=(TextView)findViewById(R.id.dispGStarTltoday);
        dispGStarTllast =(TextView)findViewById(R.id.dispGStarTllast);
        lastresultdate  =(TextView)findViewById(R.id.lastresultdate);
        timeLayout      =(LinearLayout) findViewById(R.id.timeLayout);
        timeTotalDisplay=(TextView) findViewById(R.id.timerunning_Value);
        tvStartimer     =(TextView) findViewById(R.id.tv_stimer);
        lastresulttime  =(TextView)findViewById(R.id.lastresulttime);
        timer_display   =(LinearLayout) findViewById(R.id.timer_display) ;

        tvDisplayheadertext.setText(muscleval);
        System.out.println("hfgggggggggggg"+tinydb.getBoolean("timer_check"));

        if(tinydb.getBoolean("timer_check")){

        }else{

            Log.d("tinydB-timercheck","false");
            timeLayout.setClickable( false );
            timeLayout.setEnabled(false);
            timeLayout.setBackgroundResource(0);
            tvStartimer.setVisibility( View.GONE );
            timedisplay.setVisibility( View.GONE );
            timeTotalDisplay.setVisibility( View.GONE );
            lastresulttime.setVisibility( View.GONE );

        }

        timeTotalDisplay.setText(getTimeString(AppPreferences.getInstance().getTotalRunningTime()));
        lastresulttime.setText( AppPreferences.getInstance().getLastRespTime()+"/"+AppPreferences.getInstance().getLastTotalTime());

//      timedisplay.setText(getTimeString(AppPreferences.getInstance().getRunningTime()));
//      timeValue=AppPreferences.getInstance().getRunningTime();
        Log.d(TAG, "onCreate: "+timeValue);
        timedisplay.setText("00.00.0");
        timer = null;

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer == null) {
                    startTimer();
                }
                else
                    stopTimer();
            }
        });

//      caloriedisplay      = (TextView)findViewById(R.id.dispcalorie);
        favoritebutton      = (ImageView) findViewById(R.id.favoriteButton);
        mProgressDialog     =  new ProgressDialog(DisplayResult.this);

        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);

        favoritebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Intent intent = new Intent(DisplayResult.this, FavoriteActivity.class);
//              startActivity(intent);
                Toast.makeText(getApplicationContext(), "This is marked as favorite!!", Toast.LENGTH_LONG).show();
                MakeFavorite();
                favoritebutton.setBackgroundResource(R.drawable.green_round_fav);

            }
        });

        calculateResults();

        //backarr=(ImageView)findViewById(R.id.backarr);
        /*backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

/*

       ImageView testButton = (ImageView) findViewById(R.id.btnTest);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               // swipe right

                Intent i = new Intent(DisplayResult.this, CalculatorActivity.class);

                i.putExtra("set",last_set_today);
                i.putExtra("station_no",stationval);
                i.putExtra("reps",repsval);
                i.putExtra("weight",weightval);
                i.putExtra("Total",total_last);
                i.putExtra("Date",date_last);
                i.putExtra("Muscle",muscleval);

                calculateTotalTime();

                convertedTotalTime  =   getTimeString(totalTimeValue);
                Log.d(TAG, "onSwipe: "+convertedTotalTime+"sddddddsd"+getTimeString(timeValue));
                update_gym_record();
                i.putExtra("time",convertedTotalTime);


                if(wr_routine.isEmpty() && wr_schedule.isEmpty()) {
                    i.putExtra("item",muscleval);
                    i.putExtra("is_swipe","true");
                }else{
                    i.putExtra("item","routine_wr");
                    i.putExtra("wr_routine_name",wr_routine);
                    i.putExtra("wr_schedule",wr_schedule);
                    i.putExtra("is_swipe","true");
                }
//              stopTimer();
                //i.putExtra("CurrentDate",current_date);
                //i.putExtra("PreviousDate",previous_date);
                startActivity(i);



                //swipe left
                calculateTotalTime();
                convertedTotalTime=getTimeString(totalTimeValue);
                Log.d(TAG, "onSwipe: "+convertedTotalTime+"sddddddsd"+storeTime);
                update_gym_record();
                Intent i = new Intent(DisplayResult.this, MaxValueActivity.class);
                i.putExtra("item",muscleval);
                i.putExtra("station_no",stationval);
                startActivity(i);


     }

        });

*/

    }

    private String getTimeString(int tenseconds) {
        int mins = tenseconds / 600;
        int seconds = (tenseconds - mins * 600) / 10;
        int tenSec = tenseconds % 10;
        String result = (String.format("%02d:%02d.%d", mins, seconds, tenSec));
        return result;
    }

    private void MakeFavorite() {
        String chestType = muscleval.toLowerCase();
        chestType.replace(" ", "-");
        chestType.replace("(", "-");
        chestType.replace(")", "");
//      stationval, muscleval
        String url = "https://www.gymstarpro.com/webapi/stationlog-fav.php?userid=" + AppPreferences.getInstance().getUserId() + "&muscle_type=" +
                chestType + "&station_no=" + stationval + "&favorite=1";

        ProgressDialogStart();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            Log.d("favoriteresponse", response);
                            String status = jobj.getString("status");
                            String code = jobj.getString("code");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DisplayResult.this,"Server Error,Please try again.",Toast.LENGTH_LONG).show();

                        ProgressDialogDismiss();

                    }
                });
// {
//            @Override
//            protected Map<String,String> getParams(){
//
//                Map<String,String> params = new HashMap<String, String>();//adding post param.
//                params.put("username", username.getText().toString());
//                params.put("password",password.getText().toString());
//                params.put("current_date",currentDate);
//                return params;
//            }
//        };

        RequestQueue requestQueue = Volley.newRequestQueue(DisplayResult.this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void startTimer() {
        timer = new Timer();
//        timeValue = 0;
        timeLayout.setBackgroundResource(R.drawable.red_rectedittext);
        initializeTimerTask();
        timer.schedule(timerTask, 100, 100);

    }

    private void initializeTimerTask() {
        Log.d("initializeTimerTask","true");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeValue ++;
//                        int val = timeValue;
                        if (timer != null) {
                            timedisplay.setText(getTimeString(timeValue));
                            timeTotalDisplay.setText(getTimeString(AppPreferences.getInstance().getTotalRunningTime()+timeValue));
                        }
                    }
                });
            }
        };
    }

    public void stopTimer() {
        if (timer != null) {
            AppPreferences.getInstance().setRunningTime(timeValue);
            Log.d(TAG, "stopTimer: "+AppPreferences.getInstance().getRunningTime());
            timer.cancel();
            timer = null;
//          timeValue = 0;
        }
        timeLayout.setBackgroundResource(R.drawable.green_rectedittext);
    }

    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";

                Intent i = new Intent(DisplayResult.this, CalculatorActivity.class);
                i.putExtra("set",last_set_today);
                i.putExtra("station_no",stationval);
                i.putExtra("reps",repsval);
                i.putExtra("weight",weightval);
                i.putExtra("Total",total_last);
                i.putExtra("Date",date_last);
                i.putExtra("Muscle",muscleval);

                calculateTotalTime();

                convertedTotalTime  =   getTimeString(totalTimeValue);
                Log.d(TAG, "onSwipe: "+convertedTotalTime+"sddddddsd"+getTimeString(timeValue));
                update_gym_record();
                i.putExtra("time",convertedTotalTime);


                if(wr_routine.isEmpty() && wr_schedule.isEmpty()) {
                    i.putExtra("item",muscleval);
                    i.putExtra("is_swipe","true");
                }else{
                    i.putExtra("item","routine_wr");
                    i.putExtra("wr_routine_name",wr_routine);
                    i.putExtra("wr_schedule",wr_schedule);
                    i.putExtra("is_swipe","true");
                }
//              stopTimer();
                //i.putExtra("CurrentDate",current_date);
                //i.putExtra("PreviousDate",previous_date);
                startActivity(i);

                break;

            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                calculateTotalTime();
                convertedTotalTime=getTimeString(totalTimeValue);
                Log.d(TAG, "onSwipe: "+convertedTotalTime+"sddddddsd"+storeTime);
                update_gym_record();
                i = new Intent(DisplayResult.this, MaxValueActivity.class);
                i.putExtra("item",muscleval);
                i.putExtra("station_no",stationval);
                startActivity(i);

                break;

            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                str = "Swipe Down";
                i = new Intent(DisplayResult.this, CustomDialogClass.class);
                startActivity(i);
                break;

            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;

        }
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    public void calculateResults(){

            //calcResults="http://offers-and-you.com/gymstarpro/restapi.php?action=add_gym_record";
            //calcResults="https://www.gymstarpro.com/restapi.php?action=add_gym_record";



            calcResults = AppPreferences.getInstance().url+"action=add_gym_record";

            ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {

                Log.d("offline insert", "inside");
                String userId = AppPreferences.getInstance().getUserId();
                JSONObject jobj = DBTransact.addGymRecord(userId,stationval,muscleval,repsval,weightval,TimeZone.getDefault().getID(),current_date,current_time,previous_date);
                JSONObject jobj1 = jobj.getJSONObject("today_results");
                JSONObject jobj2 = jobj.getJSONObject("last_results");
                last_set_today = jobj.getString("new_set");
                last_set_id = jobj.getString("last_set_id");
                String date_today = jobj1.getString("date");
                String weight_today = jobj1.getString("weight");
                String reps_today = jobj1.getString("reps");
                String total_today = jobj1.getString("total");

                String gymstar_total_today = jobj1.getString("gymstar_total");
                String calorie = jobj1.getString("calorie");
                //String last_set_last= jobj2.getString("new_set");
                date_last = jobj2.getString("date");
                weight_last = jobj2.getString("weight");
                reps_last = jobj2.getString("reps");
                total_last = jobj2.getString("total");
                gymstar_total_last = jobj2.getString("gymstar_total");
                headertext.setText("  Cal " + calorie);

                //caloriedisplay.setText("(Cal) "+calorie);
                setdisplay.setText("SET-" + last_set_today);
                disprepstoday.setText(reps_today);
                disprepslast.setText(reps_last);
                dispweighttoday.setText(weight_today);
                dispweightlast.setText(weight_last);
                disptotaltoday.setText(total_today);
                disptotallast.setText(total_last);
                dispGStarTltoday.setText(gymstar_total_today);
                dispGStarTllast.setText(gymstar_total_last);
                lastresultdate.setText(date_last);
                //lastdt.setText("Last Result"+"\n"+date);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressDialogDismiss();

        }else {


            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, calcResults,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                            //    Log.d("response", response);
                                JSONObject jobj = new JSONObject(response);
                                JSONObject jobj1 = jobj.getJSONObject("today_results");
                                JSONObject jobj2 = jobj.getJSONObject("last_results");
                                last_set_today = jobj.getString("new_set");
                                last_set_id = jobj.getString("last_set_id");
                                String date_today = jobj1.getString("date");
                                String weight_today = jobj1.getString("weight");
                                String reps_today = jobj1.getString("reps");
                                String total_today = jobj1.getString("total");

                                String gymstar_total_today = jobj1.getString("gymstar_total");
                                String calorie = jobj1.getString("calorie");
                                //String last_set_last= jobj2.getString("new_set");
                                date_last = jobj2.getString("date");
                                weight_last = jobj2.getString("weight");
                                reps_last = jobj2.getString("reps");
                                total_last = jobj2.getString("total");
                                gymstar_total_last = jobj2.getString("gymstar_total");
                                headertext.setText("  Cal " + calorie);

                                //caloriedisplay.setText("(Cal) "+calorie);
                                setdisplay.setText("SET-" + last_set_today);
                                disprepstoday.setText(reps_today);
                                disprepslast.setText(reps_last);
                                dispweighttoday.setText(weight_today);
                                dispweightlast.setText(weight_last);
                                disptotaltoday.setText(total_today);
                                disptotallast.setText(total_last);
                                dispGStarTltoday.setText(gymstar_total_today);
                                dispGStarTllast.setText(gymstar_total_last);
                                lastresultdate.setText(date_last);
                                //lastdt.setText("Last Result"+"\n"+date);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressDialogDismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(DisplayResult.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();

                            //ProgressDialogDismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();//adding post param.
                    params.put("station_no", stationval);
                    params.put("user_id", AppPreferences.getInstance().getUserId());
                    params.put("muscle_name", muscleval);
                    params.put("reps", repsval);
                    params.put("weight", weightval);
                    params.put("timezone", TimeZone.getDefault().getID());

                    System.out.println("Current Date is " + current_date);
                    System.out.println("Previous Date is " + previous_date);

                    params.put("current_date", current_date);
                    params.put("current_time", current_time);
                    params.put("previous_date", previous_date);
                  //  Log.d("add_result", params.toString());

                    System.out.println("djhjklllllllk" + "  station_no:" + stationval + " user_id:" + AppPreferences.getInstance().getUserId() + "muscle_name:" + muscleval + "rep:" + repsval
                            + "weight:" + weightval + "timezone:" + TimeZone.getDefault().getID() + "current_date:" + current_date + "current_time:" + current_time
                            + "previous_date" + previous_date);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(DisplayResult.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }
    }

    public void ProgressDialogStart() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!DisplayResult.this.isDestroyed()) {
                    if (mProgressDialog != null) {
                        if (!mProgressDialog.isShowing()) {
                            mProgressDialog.show();
                        }
                    }
                }
            } else {
                if (mProgressDialog != null) {
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!DisplayResult.this.isDestroyed()) {
                    if (mProgressDialog != null) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                }
            } else {
                if (mProgressDialog != null) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        calculateTotalTime();

    }

    void calculateTotalTime(){
        int storeTime=0;
        storeTime=AppPreferences.getInstance().getTotalRunningTime();
        totalTimeValue=storeTime+AppPreferences.getInstance().getRunningTime();
        AppPreferences.getInstance().setRunningTime(0);
      //  Log.d(TAG, "stopTimer: "+storeTime+" "+totalTimeValue);
        AppPreferences.getInstance().setTotalRunningTime(totalTimeValue);

    }

    public void update_gym_record() {
        //getLastResults="http://offers-and-you.com/gymstarpro/restapi.php?action=get_last_results";
        //getLastResults="https://www.gymstarpro.com/restapi.php?action=get_last_results";
        update_gym_recordResults = AppPreferences.getInstance().url + "action=update_gym_record";
        ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {

                String userId = AppPreferences.getInstance().getUserId();
                String set_id = last_set_id;
                String tt = convertedTotalTime;
                String repstime = getTimeString(timeValue);

                JSONObject jobj = DBTransact.updateGymRecord(set_id, tt, repstime);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressDialogDismiss();

        } else {


            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, update_gym_recordResults,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("dfjkssssssssssssss" + response);
                            ProgressDialogDismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(DisplayResult.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
                            //ProgressDialogDismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();//adding post param.
                    params.put("set_id", last_set_id);
                    params.put("tt", convertedTotalTime);
                    params.put("repstime", getTimeString(timeValue));
                 //   Log.d(TAG, "getParams: " + params.toString());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(DisplayResult.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

}