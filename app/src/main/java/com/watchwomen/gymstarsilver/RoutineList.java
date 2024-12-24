package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RoutineList extends AppCompatActivity {

    private DatabaseTransactions DBTransact;
    ProgressDialog mProgressDialog;
    private static final String TAG = "RoutineList";
    JSONArray routineArray     = new JSONArray();

    String selectedRoutine = "";
    String selectedSchedule = "";
    String selectedDay = "";
    String selectedMuscle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        String UserID = AppPreferences.getInstance().getUserId();

        mProgressDialog = new ProgressDialog(RoutineList.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            selectedRoutine = b.getString("selectedRoutine");
            selectedSchedule = b.getString("selectedSchedule");

            //selectedDay = b.getString("selectedDay");
            //selectedMuscle = b.getString("selectedMuscle");
        }

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {
                Log.d("selectedRoutine ", selectedRoutine);
                Log.d("selectedSchedule ", selectedSchedule);
                Log.d("UserID ", UserID);
            JSONObject jsonObject =  DBTransact.getSystemLogRoutines(UserID,selectedRoutine,selectedSchedule);

            String status = jsonObject.getString("status");
            if (status.equals("success")) {
                JSONObject bodyObject1 = jsonObject;
                routineArray = bodyObject1.getJSONArray("routine_list");
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressDialogDismiss();
            init(routineArray);

        }else {

            //String athleteRoutine = AppPreferences.getInstance().url+"action=getSystemLogRoutines&userid="+UserID+"&selectedRoutine="+selectedRoutine+"&selectedDay="+selectedDay+"&selectedMuscle="+selectedMuscle;

            String athleteRoutine = AppPreferences.getInstance().url + "action=getSystemLogRoutines&userid=" + UserID + "&selectedRoutine=" + selectedRoutine + "&selectedSchedule=" + selectedSchedule;


            //  Toast.makeText(RoutineList.this,"Athlete Routine : " + athleteRoutine,Toast.LENGTH_LONG).show();

            ProgressDialogStart();
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, athleteRoutine,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                // Toast.makeText(Routine.this,"Routine Status: " + status,Toast.LENGTH_LONG).show();
                                if (status.equals("success")) {

                                    JSONObject bodyObject1 = jsonObject;

                                    routineArray = bodyObject1.getJSONArray("routine_list");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressDialogDismiss();
                            init(routineArray);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override

                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RoutineList.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
                            ProgressDialogDismiss();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //adding post param.
                    params.put("userid", AppPreferences.getInstance().getUserId());
//              params.put("muscle_name", muscleName);
//              params.put("station_no", station_no);
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(RoutineList.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    public void init(JSONArray routineArray) {


        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TextView headerTxt = (TextView) findViewById(R.id.RW_header);

        headerTxt.setText(selectedRoutine + " " + selectedSchedule);

        String[] dayArray = new String[]{
                "M",
                "T",
                "W",
                "TH",
                "F",
                "SA",
                "SU"
        };

        if(routineArray.length() > 0){

            //Toast.makeText(RoutineList.this,"length : " + routineArray.length(),Toast.LENGTH_LONG).show();

            try {
            for (int i = 0; i < routineArray.length(); i++) {

                JSONObject value = null;

                value = routineArray.getJSONObject(i);
                String routine_name = (String) value.getString("routine_name");
                String schedule_day = (String) value.getString("schedule_day");
                String station_number = (String) value.getString("station_number");
                String muscle_name = (String) value.getString("muscle_name");
                String routine_nos = (String) value.getString("routine_nos");
                String week_days = (String) value.getString("week_days");

                TableRow tbrow = new TableRow(this);

                TextView t1v = new TextView(this);
                t1v.setText(schedule_day);
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                t1v.setTypeface(t1v.getTypeface(), Typeface.BOLD);
                t1v.setPadding(0,0,0,0);
                t1v.setTextSize(26);
                t1v.setBackgroundResource(R.drawable.routine_no_bg);
                tbrow.addView(t1v);

                TextView t2v = new TextView(this);
                t2v.setText(station_number);
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                t2v.setTypeface(t1v.getTypeface(), Typeface.BOLD);
                t2v.setPadding(0,0,0,0);
                t2v.setTextSize(18);
                tbrow.addView(t2v);

                TextView t3v = new TextView(this);

                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(13);
                t3v.setFilters(fArray);

                t3v.setText(muscle_name);
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                t3v.setTypeface(t1v.getTypeface(), Typeface.BOLD);
                t3v.setPadding(0,0,0,0);
                t3v.setTextSize(18);
                tbrow.addView(t3v);


                String[] wd = week_days.split(",");
                String wdays = "";
                if( wd.length > 0 ) {

                    for(int j=0; j < dayArray.length; j++ ) {

                        String val = Integer.toString(j);

                        if(Arrays.asList(wd).contains(val)) {
                            wdays = wdays + dayArray[j] + " ";
                        }
                    }
                }

                TextView t4v = new TextView(this);
                t4v.setText(wdays);
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                t4v.setTypeface(t1v.getTypeface(), Typeface.BOLD);
                t4v.setPadding(20,0,0,0);
                t4v.setTextSize(18);
                t4v.setAllCaps(true);
                tbrow.addView(t4v);

                stk.addView(tbrow);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(RoutineList.this,"No Data Found",Toast.LENGTH_LONG).show();
        }

    }


    public void ProgressDialogStart() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!RoutineList.this.isDestroyed()) {
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
                if (!RoutineList.this.isDestroyed()) {
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

}
