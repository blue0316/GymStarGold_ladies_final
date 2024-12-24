package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
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

public class MaxValueActivity extends AppCompatActivity {

    private DatabaseTransactions DBTransact;
    // Muscle Type Max Values Station
    TextView muStationMaxWeight;
    TextView muStationMaxWeightHTotal;
    TextView muStationMaxEffort_7;
    TextView muStationMaxEffort_24;
    //TextView muStationMaxEffort_mg_7;

    // Muscle Type Max Values Station
    TextView muValueMaxWeight;
    TextView muValueMaxWeightHTotal;
    TextView muValueMaxEffort_7;
    TextView muValueMaxEffort_24;
    //TextView muValueMaxEffort_mg_7;

    // Highest Max Station
    TextView maxStationMaxWeight;
    TextView maxStationMaxWeight_24;
    TextView maxStationMaxEffort_7;
    TextView maxStationMaxEffort_mg_24;
    TextView maxStationMaxEffort_mg_7;

    // Max Result Value
    TextView maxValueMaxWeight;
    TextView maxValueMaxWeight_24;
    TextView maxValueMaxEffort_7;
    TextView maxValueMaxEffort_mg_24;
    TextView maxValueMaxEffort_mg_7;

    // Max Muscle Type
    TextView maxMuscleMaxWeight;
    TextView maxMuscleMaxWeight_24;
    TextView maxMuscleMaxEffort_7;
    TextView maxMuscleMaxEffort_mg_24;
    TextView maxMuscleMaxEffort_mg_7;

    TextView stationHeaderText;

    String muscleName,station_no;
    private ProgressDialog mProgressDialog = null;
    private static final String TAG = "MaxValueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_max_value);
        initView();
    }

    private void initView() {

        stationHeaderText = (TextView) findViewById(R.id.stationheadertext);

        if (getIntent().getExtras() != null)

            muscleName = getIntent().getExtras().getString("item");
            station_no = getIntent().getExtras().getString("station_no");

        Log.d(TAG, "initView: "+muscleName+" "+station_no);

        stationHeaderText.setText(muscleName + " Max Results");

        muStationMaxWeight          = (TextView) findViewById(R.id.muscle_max_station);
        muStationMaxWeightHTotal       = (TextView) findViewById(R.id.muscle_max_station_HTotal);
        muStationMaxEffort_7        = (TextView) findViewById(R.id.muscle_max_station_effort_7);
        muStationMaxEffort_24    = (TextView) findViewById(R.id.muscle_max_station_effort_24);
        //muStationMaxEffort_mg_7     = (TextView) findViewById(R.id.muscle_max_station_effort_mg_7);

        muValueMaxWeight            = (TextView) findViewById(R.id.muscle_max_weight_value);
        muValueMaxWeightHTotal         = (TextView) findViewById(R.id.muscle_max_weight_value_HTotal);
        muValueMaxEffort_7          = (TextView) findViewById(R.id.muscle_max_effort_value_7);
        muValueMaxEffort_24      = (TextView) findViewById(R.id.muscle_max_effort_value_24);
        //muValueMaxEffort_mg_7       = (TextView) findViewById(R.id.muscle_max_effort_value_MG_7);


        /*maxMuscleMaxWeight          = (TextView) findViewById(R.id.max_weight_muscle_type);
        maxMuscleMaxWeight_24       = (TextView) findViewById(R.id.max_weight_muscle_type_24);
        maxMuscleMaxEffort_7        = (TextView) findViewById(R.id.max_effort_muscle_type_7);
        maxMuscleMaxEffort_mg_24    = (TextView) findViewById(R.id.max_effort_muscle_type_mg_24);
        maxMuscleMaxEffort_mg_7     = (TextView) findViewById(R.id.max_effort_muscle_type_mg_7);*/


       /* maxStationMaxWeight         = (TextView) findViewById(R.id.max_weight_high_station);
        maxStationMaxWeight_24      = (TextView) findViewById(R.id.max_weight_high_station_24);
        maxStationMaxEffort_7       = (TextView) findViewById(R.id.max_weight_high_station_effort_7);
        maxStationMaxEffort_mg_24   = (TextView) findViewById(R.id.max_weight_high_station_effort_mg_24);
        //maxStationMaxEffort_mg_7    = (TextView) findViewById(R.id.max_weight_high_station_mg_7);

        maxValueMaxWeight           = (TextView) findViewById(R.id.max_weight_value);
        maxValueMaxWeight_24        = (TextView) findViewById(R.id.max_weight_value_24);
        maxValueMaxEffort_7         = (TextView) findViewById(R.id.max_weight_value_effort_7);
        maxValueMaxEffort_mg_24     = (TextView) findViewById(R.id.max_weight_value_effort_mg_24);
        //maxValueMaxEffort_mg_7      = (TextView) findViewById(R.id.max_weight_value_effort_mg_7);*/

        mProgressDialog=new ProgressDialog(MaxValueActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        getMaxResultsByMuscle();

    }

    public void DisplayResultValues(String category, JSONObject bodyObject) {

        JSONArray tempArray = new JSONArray();
        try {
            JSONObject jsonValueObject, jsonStationObject;
            jsonStationObject = bodyObject.getJSONObject("stations");
            jsonValueObject = bodyObject.getJSONObject("values");{

                /*

                tempArray = jsonStationObject.getJSONArray(category);
                int len = tempArray.length();
                String resultStation = "";
                for (int i = 0; i < len; i++) {
                    String val = tempArray.getString(i);
                    resultStation += val;
                    resultStation += ", ";
                }

                resultStation = resultStation.substring(0, resultStation.length() - 2);
*/

                String svalue  = jsonStationObject.getString(category);
                String resultStation = "" + svalue + "";

                int value  = jsonValueObject.getInt(category);
                String resultValue = "" + value + "";

                switch (category) {
                    case "muValueMaxWeight": {
                        muStationMaxWeight.setText(resultStation);
                        muValueMaxWeight.setText(resultValue);
                        break;
                    }
                    case "muValueMaxWeight_HTotal": {
                        muStationMaxWeightHTotal.setText(resultStation);
                        muValueMaxWeightHTotal.setText(resultValue);
                        break;
                    }
                    case "muValueMaxEffort_7": {
                        muStationMaxEffort_7.setText(resultStation);
                        muValueMaxEffort_7.setText(resultValue);
                        break;
                    }
                    case "muValueMaxEffort_24": {
                        muStationMaxEffort_24.setText(resultStation);
                        muValueMaxEffort_24.setText(resultValue);
                        break;
                    }
                   /* case "muValueMaxEffort_mg_7": {
                        muStationMaxEffort_mg_7.setText(resultStation);
                        muValueMaxEffort_mg_7.setText(resultValue);
                        break;
                    }
                    */
                    default: {
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*public void DisplayHighestStations(JSONObject bodyObject, String category) {
        try {
            JSONObject valueObject = bodyObject.getJSONObject("stations");
            JSONArray tempArray = new JSONArray();
            tempArray = valueObject.getJSONArray(category);
            int len = tempArray.length();
            String resultStation = "";
            for (int i = 0; i < len; i++) {
                String val = tempArray.getString(i);
                resultStation += val;
                resultStation += ", ";
            }
            resultStation = resultStation.substring(0, resultStation.length() - 2);
            switch (category)
            {
                case "maxStationMaxWeight":
                {
                    maxStationMaxWeight.setText(resultStation);
                    break;
                }
                case "maxStationMaxWeight_24":
                {
                    maxStationMaxWeight_24.setText(resultStation);
                    break;
                }
                case "maxStationMaxEffort_7":
                {
                    maxStationMaxEffort_7.setText(resultStation);
                    break;
                }
                case "maxStationMaxEffort_mg_24":
                {
                    maxStationMaxEffort_mg_24.setText(resultStation);
                    break;
                }
                case "maxStationMaxEffort_mg_7":
                {
                    maxStationMaxEffort_mg_7.setText(resultStation);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

//    public void DisplayHighestMuscles(JSONObject bodyObject, String category) {
//        try {
//            JSONObject valueObject = bodyObject.getJSONObject("muscle_types");
//            String muscle = valueObject.getString(category);
//            switch(category)
//            {
//                case "maxMuscleMaxWeight":
//                {
//                    maxMuscleMaxWeight.setText(muscle);
//                    break;
//                }
//                case "maxMuscleMaxWeight_24":
//                {
//                    maxMuscleMaxWeight_24.setText(muscle);
//                    break;
//                }
//                case "maxMuscleMaxEffort_7" :
//                {
//                    maxMuscleMaxEffort_7.setText(muscle);
//                    break;
//                }
//                case "maxMuscleMaxEffort_mg_24":
//                {
//                    maxMuscleMaxEffort_mg_24.setText(muscle);
//                    break;
//                }
//                case "maxMuscleMaxEffort_mg_7":
//                {
//                    maxMuscleMaxEffort_mg_7.setText(muscle);
//                    break;
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    public void DisplayHighestValues(JSONObject bodyObject, String category) {
//        JSONObject valueObject = null;
//        try {
//            valueObject = bodyObject.getJSONObject("values");
//            int value  = valueObject.getInt(category);
//            String result = value + "";
//            switch(category) {
//                case "maxValueMaxWeight":
//                {
//                    maxValueMaxWeight.setText(result);
//                    break;
//                }
//                case "maxValueMaxWeight_24":
//                {
//                    maxValueMaxWeight_24.setText(result);
//                    break;
//                }
//                case "maxValueMaxEffort_mg_24":
//                {
//                    maxValueMaxEffort_mg_24.setText(result);
//                    break;
//                }
//                case "maxValueMaxEffort_7":
//                {
//                    maxValueMaxEffort_7.setText(result);
//                    break;
//                }
//                case "maxValueMaxEffort_mg_7":
//                {
//                    maxValueMaxEffort_mg_7.setText(result);
//                    break;
//                }
//                default:
//                {
//                    break;
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void getMaxResultsByMuscle(){
        String urlMaxResult = AppPreferences.getInstance().url+"action=max_weight_api_by_muscle";
        ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {

                String userid = AppPreferences.getInstance().getUserId();
                JSONObject jsonObject = DBTransact.getMaxWeightByMuscle(userid,muscleName.toLowerCase(),station_no);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {

                        JSONObject bodyObject = jsonObject.getJSONObject("values");

                        DisplayResultValues("muValueMaxWeight", bodyObject);
                        DisplayResultValues("muValueMaxWeight_HTotal", bodyObject);
                        DisplayResultValues("muValueMaxEffort_7", bodyObject);
                        DisplayResultValues("muValueMaxEffort_24", bodyObject);

//                                getMaxResults();
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressDialogDismiss();

        }else {

            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlMaxResult,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if (status.equals("success")) {

                                    JSONObject bodyObject = jsonObject.getJSONObject("values");

                                    DisplayResultValues("muValueMaxWeight", bodyObject);
                                    DisplayResultValues("muValueMaxWeight_HTotal", bodyObject);
                                    DisplayResultValues("muValueMaxEffort_7", bodyObject);
                                    DisplayResultValues("muValueMaxEffort_24", bodyObject);

//                                getMaxResults();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ProgressDialogDismiss();
                            }
                            ProgressDialogDismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MaxValueActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();

                            ProgressDialogDismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();//adding post param.

                    params.put("userid", AppPreferences.getInstance().getUserId());
                    params.put("muscle_type", muscleName.toLowerCase());
                    params.put("station_no", station_no);

                    Log.d(TAG, "getParams: " + params.toString());
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(MaxValueActivity.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

//    public void getMaxResults(){
//        String urlMaxResult = AppPreferences.getInstance().url+"action=max_highest_api";
////        ProgressDialogStart();
//        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlMaxResult,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String status = jsonObject.getString("status");
//                            if (status.equals("success")) {
//
//                                JSONObject bodyObject = jsonObject;
//                                DisplayHighestStations(bodyObject, "maxStationMaxWeight");
//                                DisplayHighestStations(bodyObject, "maxStationMaxWeight_24");
//                                DisplayHighestStations(bodyObject, "maxStationMaxEffort_7");
//                                DisplayHighestStations(bodyObject, "maxStationMaxEffort_mg_24");
//                                DisplayHighestStations(bodyObject, "maxStationMaxEffort_mg_7");
//
//                                DisplayHighestMuscles(bodyObject, "maxMuscleMaxWeight");
//                                DisplayHighestMuscles(bodyObject, "maxMuscleMaxWeight_24");
//                                DisplayHighestMuscles(bodyObject, "maxMuscleMaxEffort_7");
//                                DisplayHighestMuscles(bodyObject, "maxMuscleMaxEffort_mg_24");
//                                DisplayHighestMuscles(bodyObject, "maxMuscleMaxEffort_mg_7");
//
//                                DisplayHighestValues(bodyObject, "maxValueMaxWeight");
//                                DisplayHighestValues(bodyObject, "maxValueMaxWeight_24");
//                                DisplayHighestValues(bodyObject, "maxValueMaxEffort_mg_24");
//                                DisplayHighestValues(bodyObject, "maxValueMaxEffort_mg_7");
//                                DisplayHighestValues(bodyObject, "maxValueMaxEffort_7");
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        ProgressDialogDismiss();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MaxValueActivity.this,"Server Error,Please try again.",Toast.LENGTH_LONG).show();
//                        ProgressDialogDismiss();
//
//                    }
//
//                }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();//adding post param.
//                params.put("userid", AppPreferences.getInstance().getUserId());
//                params.put("muscle_name", muscleName);
//                params.put("station_no", station_no);
//                Log.d(TAG, "getParams: "+params);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(MaxValueActivity.this);
//        requestQueue.add(stringRequest);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!MaxValueActivity.this.isDestroyed()) {
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
                if (!MaxValueActivity.this.isDestroyed()) {
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
