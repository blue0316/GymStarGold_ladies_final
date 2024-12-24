package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemSelectedListener;

import static android.text.TextUtils.isEmpty;


public class Routine extends AppCompatActivity  implements OnItemSelectedListener{

    private DatabaseTransactions DBTransact;

    Spinner SpinnerRoutine;
    Spinner SpinnerDay;
    Spinner SpinnerMuscle;

    ProgressDialog mProgressDialog;

    ArrayList<Routine_Name> routine_names = new ArrayList<>();

    private static final String TAG = "Routine";
    private static int len = 1;

    private JSONArray routines_name;
    private JSONArray routines_record;

    private String selectedRoutine = "";
    private String selectedSchedule = "";
    private String selectedStation = "";
    private String selectedDay = "";
    private String selectedMuscle = "";
    private String selectedMuscleName = "";


    List<String> routineList = new ArrayList<String>();
    List<String> scheduleList = new ArrayList<String>();
    List<String> muscleIdList = new ArrayList<String>();
    List<String> dayIdList = new ArrayList<String>();

    String[] dayArray = new String[]{
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
    };


    String[] dayIdArray = new String[]{
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        String UserID = AppPreferences.getInstance().getUserId();


        //Toast.makeText(Routine.this,"Userid" + UserID,Toast.LENGTH_LONG).show();

        mProgressDialog = new ProgressDialog(Routine.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();
        Log.d("routine offline" , String.valueOf(offlineStatus));
        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);

            try {
                JSONObject jsonObject =  DBTransact.getAthleteRoutines(UserID);

                String status = jsonObject.getString("status");
                // Toast.makeText(Routine.this,"Routine Status: " + status,Toast.LENGTH_LONG).show();
                if (status.equals("success")) {

                    JSONObject bodyObject1 = jsonObject;
                    JSONArray routineArray = new JSONArray();
                    routines_record = bodyObject1.getJSONArray("routine_records");

                    routines_name = bodyObject1.getJSONArray("routine_names");
                    len = routines_name.length();
                    routine_names.add(new Routine_Name("SELECT ROUTINE NAME", "0"));

                    routineList.clear();
                    routineList.add("0");
                    scheduleList.clear();
                    scheduleList.add("0");

                    for (int i = 0; i < len; i++) {
                        JSONObject value = routines_name.getJSONObject(i);
                        String routine_display_name = (String) value.getString("routine_display_name");
                        String routine_name = (String) value.getString("routine_name");
                        String schedule = (String) value.getString("schedule");
                        //String station = (String) value.getString("station");

                        routine_names.add(new Routine_Name(routine_display_name));
                        routineList.add(routine_name);
                        scheduleList.add(schedule);
                    }
                }
                ProgressDialogDismiss();
                initializeUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }


           // Toast.makeText(Routine.this, "Offline: " + offlineStatus, Toast.LENGTH_LONG).show();
        } else {

        String athleteRoutine = AppPreferences.getInstance().url + "action=getAthleteRoutines&userid=" + UserID;

        //Toast.makeText(Routine.this,"Athlete Routine : " + athleteRoutine,Toast.LENGTH_LONG).show();

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
                                JSONArray routineArray = new JSONArray();
                                routines_record = bodyObject1.getJSONArray("routine_records");

                                routines_name = bodyObject1.getJSONArray("routine_names");
                                len = routines_name.length();
                                routine_names.add(new Routine_Name("SELECT ROUTINE NAME", "0"));

                                routineList.clear();
                                routineList.add("0");
                                scheduleList.clear();
                                scheduleList.add("0");

                                for (int i = 0; i < len; i++) {
                                    JSONObject value = routines_name.getJSONObject(i);
                                    String routine_display_name = (String) value.getString("routine_display_name");
                                    String routine_name = (String) value.getString("routine_name");
                                    String schedule = (String) value.getString("schedule");
                                    //String station = (String) value.getString("station");

                                    routine_names.add(new Routine_Name(routine_display_name));
                                    routineList.add(routine_name);
                                    scheduleList.add(schedule);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();
                        initializeUI();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Routine.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Routine.this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }
    }

    private void initializeUI() {

       // Toast.makeText(Routine.this,"UI",Toast.LENGTH_LONG).show();

        SpinnerRoutine = (Spinner)findViewById(R.id.spinner1);

      //  SpinnerDay = (Spinner)findViewById(R.id.spinner2);
      //  SpinnerMuscle = (Spinner)findViewById(R.id.spinner3);


        // Spinner click listener
        SpinnerRoutine.setOnItemSelectedListener(this);

    //    SpinnerDay.setOnItemSelectedListener(this);
     //   SpinnerMuscle.setOnItemSelectedListener(this);


        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnReview = (Button) findViewById(R.id.btnReview);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SpinnerRoutine.getSelectedItem().toString().trim().equals("SELECT ROUTINE NAME")) {
                    Toast.makeText(Routine.this, "Please select routine name", Toast.LENGTH_SHORT).show();
                }else{
                    //  Toast.makeText(Routine.this, "Routine: " + selectedRoutine, Toast.LENGTH_LONG).show();

                    Intent act2 = new Intent(Routine.this, CalculatorActivity.class);
                    Bundle b = new Bundle();
                    act2.putExtra("item","routine_wr");
                    act2.putExtra("wr_station_no",selectedStation);
                    act2.putExtra("wr_muscle_name",selectedMuscleName);
                    act2.putExtra("wr_muscle_id",selectedMuscle);
                    act2.putExtra("wr_schedule",selectedSchedule);
                   // act2.putExtra("wr_week_day",selectedDay);
                    act2.putExtra("wr_routine_name",selectedRoutine);

//                    Toast.makeText(Routine.this, "wr_station_no: " + selectedStation, Toast.LENGTH_LONG).show();
//                    Toast.makeText(Routine.this, "wr_muscle_name: " + selectedMuscleName, Toast.LENGTH_LONG).show();
//                    Toast.makeText(Routine.this, "wr_muscle_id: " + selectedMuscle, Toast.LENGTH_LONG).show();
//                    Toast.makeText(Routine.this, "wr_schedule: " + selectedSchedule, Toast.LENGTH_LONG).show();
//                    Toast.makeText(Routine.this, "wr_routine_name: " + selectedRoutine, Toast.LENGTH_LONG).show();

                    startActivity(act2);
                }

                //Intent act2 = new Intent(view.getContext(), RoutineCalculatorActivity.class);
               // startActivity(act2);
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (SpinnerRoutine.getSelectedItem().toString().trim().equals("SELECT ROUTINE NAME")) {
                    Toast.makeText(Routine.this, "Please select routine name", Toast.LENGTH_SHORT).show();
             /*   }else if(SpinnerDay.getSelectedItem().toString().trim().equals("SELECT DAY OF WEEK")){
                    Toast.makeText(Routine.this, "Please select day of week", Toast.LENGTH_SHORT).show();
                }else if(SpinnerMuscle.getSelectedItem().toString().trim().equals("SELECT MUSCLE GROUP")){
                    Toast.makeText(Routine.this, "Please select muscle group", Toast.LENGTH_SHORT).show();*/
                }else{
                  //  Toast.makeText(Routine.this, "Routine: " + selectedRoutine, Toast.LENGTH_LONG).show();
                   // Toast.makeText(Routine.this, "Day: " + selectedSchedule, Toast.LENGTH_LONG).show();
                    //Toast.makeText(Routine.this, "Muscle: " + selectedMuscle, Toast.LENGTH_LONG).show();

                    Intent act3 = new Intent(view.getContext(), RoutineList.class);
                    Bundle b = new Bundle();
                    b.putString("selectedRoutine", selectedRoutine);
                    b.putString("selectedSchedule", selectedSchedule);
                 //   b.putString("selectedDay", selectedDay);
                 //   b.putString("selectedMuscle", selectedMuscle);
                    act3.putExtras(b);
                    startActivity(act3);
                }

            }
        });

        /*
        List<String> list = new ArrayList<String>();
        list.add("SELECT MUSCLE GROUP");

        List<String> daylist = new ArrayList<String>();
        daylist.add("SELECT DAY OF WEEK");

        muscleIdList.add("0");
        dayIdList.add("-1");*/


        ArrayAdapter<Routine_Name> spinnerArrayAdapter1 = new ArrayAdapter<Routine_Name>(getApplicationContext(), R.layout.activity_spinner_textview_align, routine_names);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.activity_spinner_textview_align);
        SpinnerRoutine.setAdapter(spinnerArrayAdapter1);

        /*
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_spinner_textview_align, daylist);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.activity_spinner_textview_align);
        SpinnerDay.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_spinner_textview_align, list);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.activity_spinner_textview_align);
        SpinnerMuscle.setAdapter(spinnerArrayAdapter2);
        */
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.spinner1)
        {
            String item = parent.getItemAtPosition(position).toString();
            if(!item.equals("SELECT ROUTINE NAME")){
                selectedRoutine = routineList.get(position);
                selectedSchedule = scheduleList.get(position);
               /*
                try {


                    len = routines_record.length();
                    for (int i = 0; i < len; i++) {

                        JSONObject value = routines_record.getJSONObject(i);
                        String routinename = (String) value.getString("routine_name");
                        String station_number = (String) value.getString("station_number");
                        String schedule_day = (String) value.getString("schedule_day");
                        String schedule_workout_routine = (String) value.getString("schedule_workout_routine");
                        String muscle_name = (String) value.getString("muscle_name");
                        String muscle_id = (String) value.getString("muscle_id");

                        if(selectedRoutine.equals(routinename) && schedule_workout_routine.equals(selectedSchedule)) {
                            selectedStation = station_number;
                            selectedMuscleName = muscle_name;
                            selectedMuscle = muscle_id;

                            break;
                        }
                    }

                   // Toast.makeText(parent.getContext(), "selectedStation " + selectedStation , Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(parent.getContext(), "Error", Toast.LENGTH_LONG).show();
                }*/


            }
        }
      /*  else if(parent.getId() == R.id.spinner2)
        {
            String item = parent.getItemAtPosition(position).toString();
            if(!item.equals("SELECT DAY OF WEEK")){
                //selectedDay = dayIdArray[position];
                selectedDay = dayIdList.get(position);

                try {
                    len = routines_record.length();
                    List<String> list = new ArrayList<String>();
                    list.add("SELECT MUSCLE GROUP");

                    muscleIdList.clear();
                    muscleIdList.add("0");

                    for (int i = 0; i < len; i++) {

                        JSONObject value = routines_record.getJSONObject(i);
                        String routinename = (String) value.getString("routine_name");
                        String musclename = (String) value.getString("muscle_name");
                        String muscleid = (String) value.getString("muscle_id");
                        String schedule_week_days = (String) value.getString("schedule_week_days");

                        String[] wd = schedule_week_days.split(",");

                        if(selectedRoutine.equals(routinename) && Arrays.asList(wd).contains(selectedDay)) {
                            list.add(musclename);
                            muscleIdList.add(muscleid);
                        }
                    }



                    ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_spinner_textview_align, list);
                    spinnerArrayAdapter2.setDropDownViewResource(R.layout.activity_spinner_textview_align);
                    spinnerArrayAdapter2.notifyDataSetChanged();
                    SpinnerMuscle.setAdapter(spinnerArrayAdapter2);


                    //Toast.makeText(parent.getContext(), "muscle length1: " + muscleIdList.size() , Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(parent.getContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }



            //Toast.makeText(parent.getContext(), "Day: ", Toast.LENGTH_LONG).show();
        }
        else if(parent.getId() == R.id.spinner3)
        {
            String item = parent.getItemAtPosition(position).toString();
            if(!item.equals("SELECT MUSCLE GROUP")){
                //Toast.makeText(parent.getContext(), "muscle length: " + muscleIdList.size() + " - Position: " + position, Toast.LENGTH_LONG).show();
                selectedMuscle =  muscleIdList.get(position);
            }
            //Toast.makeText(parent.getContext(), "muscle: ", Toast.LENGTH_LONG).show();
        }*/

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public void ProgressDialogStart() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!Routine.this.isDestroyed()) {
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
                if (!Routine.this.isDestroyed()) {
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



    private class Routine_Name {
        private String routine_name;
        private String routine_id;

        public Routine_Name() {
        }

        public Routine_Name(String routine_name, String routine_id) {
            this.routine_name = routine_name;
            this.routine_id = routine_id;
        }

        public Routine_Name(String routine_name) {
            this.routine_name = routine_name;
        }

        public String getRoutine_name() {
            return routine_name;
        }

        public void setRoutine_name(String routine_name) {
            this.routine_name = routine_name;
        }

        public String getRoutine_id() {
            return routine_id;
        }

        public void setRoutine_id(String routine_id) {
            this.routine_id = routine_id;
        }

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return routine_name
         */
        @Override
        public String toString() {
            return routine_name;
        }
    }



}
