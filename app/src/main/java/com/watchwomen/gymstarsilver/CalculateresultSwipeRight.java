package com.watchwomen.gymstarsilver;
import com.android.volley.AuthFailureError;
import com.watchwomen.gymstarsilver.SimpleGestureFilter.SimpleGestureListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;



public class CalculateresultSwipeRight extends AppCompatActivity implements SimpleGestureListener {
    TextView header,set;
    private SimpleGestureFilter detector;

    EditText stationno,reps,weight;
    Intent i;
    ImageView calresult;
    ImageView leftarrow,rightarrow,delete;

    static int bc = 1, forc = 1, abnc = 1, llegsc = 1;
    static int bic = 1, tric = 1, chlc = 1, chuc = 1,delfc=1,delbc=1,legfc=1,legbc=1,gmaxic=1;
    String headername,getLastResults,repsval,weightval,stationval,setnolastval,lastweightval,laststationval,lastrepsval,
            total_today,date_today,musclelast,current_date,previous_date,current_time;
    String timeValue;
    ImageView backarr;
    ProgressDialog mProgressDialog;
    TextView repres,wtres,totres,lastdt,stationnotxt,weighttxt,repstxt;;
    private GestureDetectorCompat gestureDetectorCompat;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    Calendar cal,prevcal;
    private static final String TAG = "CalculateresultSwipeRig";


    String wr_muscle_name = "";
    String wr_muscle_id = "";
    String wr_week_day = "";
    String wr_routine_name = "";
    String wr_station_no = "";
    String wr_schedule = "";
    JSONArray routineArray     = new JSONArray();
    int stationListIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        Bundle bundle = getIntent().getExtras();

        //lastweightval = bundle.getString("weight");

        //setnolastval = bundle.getString("set");
        //total_today=bundle.getString("Total");
        //lastrepsval = bundle.getString("reps");
        //date_today=bundle.getString("Date");
        musclelast = bundle.getString("Muscle");
        setContentView(R.layout.activity_calculateresultswiperight);
        header=(TextView)findViewById(R.id.headertext);
        set=(TextView)findViewById(R.id.set);
        stationno=(EditText)findViewById(R.id.stno);
        reps=(EditText)findViewById(R.id.repsedit);
        weight=(EditText)findViewById(R.id.weightedit);
        calresult=(ImageView)findViewById(R.id.calresult);
        // leftarrow=(ImageView)findViewById(R.id.back);
        // rightarrow=(ImageView)findViewById(R.id.forward);
        delete=(ImageView)findViewById(R.id.delete);
        //backarr=(ImageView)findViewById(R.id.backarr);
        lastdt=(TextView)findViewById(R.id.lastdate);
        repres=(TextView)findViewById(R.id.text3);
        wtres=(TextView)findViewById(R.id.text4);
        totres=(TextView)findViewById(R.id.text5);

        laststationval = bundle.getString("station_no");
        lastweightval = bundle.getString("weight");
        lastrepsval = bundle.getString("reps");
        timeValue = bundle.getString("time");
        stationno.setText(laststationval);
        reps.setText(lastrepsval);
        weight.setText(lastweightval);
        weight.requestFocus();

        mProgressDialog=new ProgressDialog(CalculateresultSwipeRight.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);


        getLastResultsAfterSwipe();

        /*set.setText("SET-"+setnolastval);
        repres.setText(lastrepsval);
        totres.setText(total_today);
        wtres.setText(lastweightval);
        lastdt.setText("Last Result "+date_today);*/
        stationnotxt = (TextView)findViewById(R.id.text2);
        weighttxt = (TextView)findViewById(R.id.reps);
        repstxt = (TextView)findViewById(R.id.weight);

        cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        prevcal=cal;

        // gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
       /* backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        /*mProgressDialog=new ProgressDialog(CalculateresultSwipeRight.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);*/

        headername = musclelast;
        //getLastResults();


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stationno.setText("");
                weight.setText("");
                reps.setText("");

            }
        });
       /* leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/



        stationnotxt.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                stationno.requestFocus();
                stationno.setFocusableInTouchMode(true);
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(stationno,InputMethodManager.SHOW_FORCED);

            }
        });
        repstxt.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                reps.requestFocus();
                reps.setFocusableInTouchMode(true);
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(reps,InputMethodManager.SHOW_FORCED);

            }
        });
        weighttxt.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                weight.requestFocus();
                weight.setFocusableInTouchMode(true);
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(weight,InputMethodManager.SHOW_FORCED);

            }
        });


        stationno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                               @Override
                                               public void onFocusChange(View view, boolean b) {
                                                   if (!b){
                                                       InputMethodManager im= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                                       im.hideSoftInputFromWindow(view.getWindowToken(),0);
                                                       getLastResultsAfterSwipe();
                                                   }
                                               }
                                           }
        );


        calresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculateresultSwipeRight.this, DisplayResult.class);

                if ((stationno.getText().toString().equals("")) || (stationno.getText().toString().length()) == 0) {
                    Toast.makeText(CalculateresultSwipeRight.this,"Please Enter Station",Toast.LENGTH_LONG).show();
                } else if ((reps.getText().toString().equals("")) || (reps.getText().toString().length()) == 0){
                    Toast.makeText(CalculateresultSwipeRight.this,"Please Enter Reps",Toast.LENGTH_LONG).show();
                }else if ((weight.getText().toString().equals("")) || (weight.getText().toString().length()) == 0){
                    Toast.makeText(CalculateresultSwipeRight.this,"Please Enter Weight",Toast.LENGTH_LONG).show();
                }
                else{
                    i.putExtra("muscle_name",headername);
                    i.putExtra("station_no",stationno.getText().toString());
                    i.putExtra("reps",reps.getText().toString());
                    i.putExtra("weight",weight.getText().toString());
                    i.putExtra("currentDate",current_date);
                    i.putExtra("currentTime",current_time);

                    i.putExtra("time", timeValue);
                    //prevcal.add(Calendar.DAY_OF_YEAR, -1);
                    i.putExtra("previousDate",previous_date);
                    startActivity(i);
                }


            }
        });

        findViewById(R.id.showlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculateresultSwipeRight.this, StationListActivity.class);
                i.putExtra("muscle_name",headername);
                startActivity(i);
            }
        });
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

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                i = new Intent(CalculateresultSwipeRight.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               /* i.putExtra("set",last_set_today);
                i.putExtra("station_no",stationval);
                i.putExtra("reps",reps_last);
                i.putExtra("weight",weight_last);
                i.putExtra("Total",total_last);
                i.putExtra("Date",date_last);
                i.putExtra("Muscle",muscleval);*/

                AppPreferences.getInstance().setRunningTime(0);
                AppPreferences.getInstance().setTotalRunningTime(0);
                startActivity(i);
                finish();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                i= new Intent(CalculateresultSwipeRight.this, GraphViewData.class);
                i.putExtra("muscle_name",headername);
                i.putExtra("station_no",stationno.getText().toString());
                i.putExtra("currentDate",current_date);
                i.putExtra("previousDate",previous_date);

//                startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
//                Intent i = new Intent(CalculateresultSwipeRight.this, CustomDialogClass.class);
//                startActivity(i);
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                break;

        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        if (headername != null && !headername.equals("")) {
            header.setText(headername.toUpperCase());

        }
    }
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    public void getLastResultsAfterSwipe(){
        //getLastResults="http://offers-and-you.com/gymstarpro/restapi.php?action=get_last_results";
        //getLastResults="https://www.gymstarpro.com/restapi.php?action=get_last_results";
        getLastResults = AppPreferences.getInstance().url+"action=get_last_results";
        ProgressDialogStart();
        System.out.println("hgffffffffff"+"yha aaya");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, getLastResults,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jobj1=new JSONObject(response);
                            Log.d("response", response);

                            JSONObject jobj=jobj1.getJSONObject("last_results");
                            String last_set= jobj1.getString("new_set");
                            String date= jobj.getString("date");
                            String weight= jobj.getString("weight");
                            String reps= jobj.getString("reps");
                            String total= jobj.getString("total");
                            String gymstar_total= jobj.getString("gymstar_total");

                            set.setText("SET-"+last_set);
                            repres.setText(reps);
                            totres.setText(total);
                            wtres.setText(weight);
                            lastdt.setText("Last Result "+date);
                            String total_time = jobj1.getString("total_time");
                            String resp_time = jobj1.getString("repstime");
                            AppPreferences.getInstance().setLastTotalTime(total_time);
                            AppPreferences.getInstance().setLastRespTime(resp_time);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CalculateresultSwipeRight.this,error.getMessage(),Toast.LENGTH_LONG).show();

                        ProgressDialogDismiss();

                    }
                }){

//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                JSONObject
//                Map<String,String> params = new HashMap<String, String>();//adding post param.
//                params.put("station_no", stationno.getText().toString());
//                params.put("user_id",AppPreferences.getInstance().getUserId());
//                params.put("muscle_name",headername);
//                cal = Calendar.getInstance();
//                current_date = dateFormat.format(cal.getTime());
//                params.put("current_date",current_date);
//                current_time = timeFormat.format(cal.getTime());
//                params.put("current_time",current_time);
//                params.put("workout_time",timeValue + "");
//
//                cal.add(Calendar.DAY_OF_YEAR, -1);
//                previous_date = dateFormat.format(cal.getTime());
//                params.put("previous_date",previous_date);
//                Log.d("response_input", params.toString());
//                String str = body;
//
//                return str.getBytes();
//
//            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();//adding post param.
                params.put("station_no", stationno.getText().toString());
                params.put("user_id",AppPreferences.getInstance().getUserId());
                params.put("muscle_name",headername);
                cal = Calendar.getInstance();
                current_date = dateFormat.format(cal.getTime());
                params.put("current_date",current_date);
                current_time = timeFormat.format(cal.getTime());
                params.put("current_time",current_time);
                params.put("time_workout",timeValue + "");

                cal.add(Calendar.DAY_OF_YEAR, -1);
                previous_date = dateFormat.format(cal.getTime());
                params.put("previous_date",previous_date);

                Log.d(TAG, "getParams: "+params.toString());
                Log.d("response_input", params.toString()+" time"+timeValue);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(CalculateresultSwipeRight.this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!CalculateresultSwipeRight.this.isDestroyed()) {
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

    public void ProgressDialogStart() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!CalculateresultSwipeRight.this.isDestroyed()) {
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
}
