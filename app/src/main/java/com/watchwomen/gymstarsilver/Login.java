package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class Login extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    TextView headertext;
    String mode;
    Button login;
    ImageView backarr;
    EditText username,password;
    String LoginUrl,currentDate,current_date;
    ProgressDialog mProgressDialog;
    private SimpleGestureFilter detector;
    SimpleDateFormat dateFormat;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date expDate=null;
    Date presentDate=null;
    Calendar cal;
    private DatabaseTransactions DBTransact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        headertext=(TextView)findViewById(R.id.headertext);
        login=(Button)findViewById(R.id.loginbtn);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        Calendar c= Calendar.getInstance();
        Date d= c.getTime();
        currentDate = format.format(d);
        DBTransact = new DatabaseTransactions(this);
       /* backarr=(ImageView)findViewById(R.id.backarr);
        backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
       // headertext.setText("Login");
        mProgressDialog=new ProgressDialog(Login.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mode= getIntent().getStringExtra("mode");
        if(AppPreferences.getInstance().getLoginStatus()==true){
            if (mode != null) {
                if (mode.equals("cardio")) {
                    Intent i = new Intent(Login.this, CardioWorkout.class);
                    startActivity(i);
                    finish();
                } else if (mode.equals("workout")) {
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else if (mode.equals("routine")) {
                    Intent i = new Intent(Login.this, Routine.class);
                    startActivity(i);
                    finish();
                } else if (mode.equals("sync")) {
                    Intent i = new Intent(Login.this, SyncActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(username.getText().toString().equals("") && username.getText().length()==0){
                    Toast.makeText(Login.this,"Please Insert Correct Username",Toast.LENGTH_LONG).show();
                }
                else if(password.getText().toString().equals("") && password.getText().length()==0){
                    Toast.makeText(Login.this,"Please Insert Correct Password!",Toast.LENGTH_LONG).show();
                }
                else{
                    CallLogin();
                    /*if (mode != null) {
                        if (mode.equals("cardio")) {

                            Intent i = new Intent(Login.this, CardioWorkout.class);
                           // startActivity(i);
                           // finish();
                        } else if (mode.equals("workout")) {
                            Intent i = new Intent(Login.this, Workout.class);
                          //  startActivity(i);
                          //  finish();
                        }
                    }*/

                }

            }
        });
    }


    //voley call
    public void CallLogin() {

        LoginUrl= AppPreferences.getInstance().url+"action=login";

        //LoginUrl="http://offers-and-you.com/gymstarpro/restapi.php?action=login";
        ProgressDialogStart();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, LoginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jobj=new JSONObject(response);
                            Log.d("response", response);
                            String status= jobj.getString("status");
                            String msg=jobj.getString("message");

                            if(status.equals("0")){
                                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            else if(status.equals("1")){
                                String access_token=jobj.getString("access_token");
                                String userID=jobj.getString("user_id");
                                String exp=jobj.getString("expiry_date");
                                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();

                                AppPreferences.getInstance().setAccesToken(access_token);
                                AppPreferences.getInstance().setUserID(userID);
                                AppPreferences.getInstance().setLoginStatus(true);

                                if((exp != null && !exp.equals("0000-00-00"))   ) {
                                    try {
                                        expDate = new SimpleDateFormat("yyyy-MM-dd").parse(exp);
                                        presentDate = new SimpleDateFormat("yyyy-MM-dd").parse(current_date);
                                    } catch (Exception e) {
                                        System.out.println("Error while getting expiry date" + e.toString());
                                    }
                                    if (expDate.compareTo(presentDate) < 0) {
                                        Toast.makeText(Login.this, "Your Subscription has expired.", Toast.LENGTH_LONG).show();
                                        AppPreferences.getInstance().setUserID(null);
                                        AppPreferences.getInstance().setLoginStatus(false);
                                        mode = null;
                                    }
                                }

                                String userId = AppPreferences.getInstance().getUserId();

                                Boolean syncStatus = DBTransact.getSyncStatus(userId);
                                Log.d("sync : " , String.valueOf(syncStatus));
                                Intent intent = new Intent(Login.this, SyncDialogActivity.class);
                                if (syncStatus) {
                                    intent.putExtra("syncStatus","true");
                                }else{
                                    intent.putExtra("syncStatus","false");
                                }
                                startActivity(intent);
                                finish();

                                /*
                                if (mode != null) {
                                    if (mode.equals("cardio")) {

                                        Intent i = new Intent(Login.this, CardioWorkout.class);
                                        startActivity(i);
                                        finish();
                                    } else if (mode.equals("workout")) {

                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else if (mode.equals("routine")) {

                                        Intent i = new Intent(Login.this, Routine.class);
                                        startActivity(i);
                                        finish();
                                    }else if (mode.equals("sync")) {

                                        Intent i = new Intent(Login.this, SyncActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,"Server Error,Please try again.",Toast.LENGTH_LONG).show();

                        ProgressDialogDismiss();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                cal = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                current_date = dateFormat.format(cal.getTime());
                Map<String,String> params = new HashMap<String, String>();//adding post param.
                params.put("username", username.getText().toString());
                params.put("password",password.getText().toString());
                params.put("current_date",currentDate);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!Login.this.isDestroyed()) {
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
                if (!Login.this.isDestroyed()) {
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
                //Intent i = new Intent(Workout.this, Home.class);
               /* i.putExtra("set",last_set_today);
                i.putExtra("station_no",stationval);
                i.putExtra("reps",reps_last);
                i.putExtra("weight",weight_last);
                i.putExtra("Total",total_last);
                i.putExtra("Date",date_last);
                i.putExtra("Muscle",muscleval);*/
                //startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN:

                // i = new Intent(Workout.this, Login.class);
                // startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                break;

        }
    }
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }


    //end
}
