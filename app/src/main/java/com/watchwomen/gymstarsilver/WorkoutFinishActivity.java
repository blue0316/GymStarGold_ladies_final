package com.watchwomen.gymstarsilver;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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


public class WorkoutFinishActivity extends AppCompatActivity implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button btncontinue;
    public TextView txt_content;

    private DatabaseTransactions DBTransact;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_finish);
        btncontinue = (Button) findViewById(R.id.btn_continue);
        txt_content = (TextView) findViewById(R.id.txt_content);

        btncontinue.setOnClickListener(this);

        mProgressDialog=new ProgressDialog(WorkoutFinishActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);

        getGymTotal();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                Intent i = new Intent(WorkoutFinishActivity.this, NavigationMenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if(AppPreferences.getInstance().getOfflineStatus()){
                    i.putExtra( "mode", "offline" );
                }else{
                    i.putExtra( "mode", "online" );
                }
                startActivity(i);
                finish();
                break;
            default:
                break;
        }

    }


    public void getGymTotal(){

      String  getRoutineGymTotal = AppPreferences.getInstance().url+"action=get_routine_gymtotal";
        ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();
        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {

                String userId = AppPreferences.getInstance().getUserId();
                JSONObject jobj1 = DBTransact.getRoutineGymTotal(userId);

                Log.d("JSON Output:", jobj1.toString(4));


                String gymTotal = jobj1.getString("gymTotal");

                // Toast.makeText(WorkoutFinishActivity.this, "last_set: " + last_set, Toast.LENGTH_LONG).show();

                txt_content.setText("Congratulation You Are Done. Your GYMStar Total is " + gymTotal);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ProgressDialogDismiss();

        }else {


            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, getRoutineGymTotal,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response", response);
                                JSONObject jobj1 = new JSONObject(response);

                                Log.d("JSON Output:", jobj1.toString(4));


                                String gymTotal = jobj1.getString("gymTotal");

                                // Toast.makeText(WorkoutFinishActivity.this, "last_set: " + last_set, Toast.LENGTH_LONG).show();

                                txt_content.setText("Congratulation You Are Done. Your GYMStar Total is " + gymTotal);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressDialogDismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(WorkoutFinishActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();

                            //ProgressDialogDismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();//adding post param.
                    params.put("user_id", AppPreferences.getInstance().getUserId());
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(WorkoutFinishActivity.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }


    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!WorkoutFinishActivity.this.isDestroyed()) {
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
                if (!WorkoutFinishActivity.this.isDestroyed()) {
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