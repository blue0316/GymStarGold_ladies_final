package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
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

public class FavoriteActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{

    String item = "", stationno = "";
    private TextView txtStationHeader;
    private SimpleGestureFilter detector;
    TextView txtStation, txtMaxWeight, txtMaxEffort, txtMaxWeeklyTtl, txtFvrMaxWeight, txtFvrMaxEffort;
    String maxWeight = "75", maxEffort = "75", maxWeeklyTtl = "75", maxFvrtweight = "75", maxFvrEffort = "75";
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_favorite);
        if (getIntent().getExtras() != null)
        {
            item = getIntent().getExtras().getString("item");
            stationno = getIntent().getExtras().getString("station_no");
        }
        initView();
    }

    private void initView() {

        txtStationHeader = (TextView)findViewById(R.id.stationheadertext);
//        maxheadertext
        txtStation = (TextView) findViewById(R.id.stno);
        txtMaxWeight = (TextView) findViewById(R.id.maxweight);
        txtMaxEffort = (TextView) findViewById(R.id.maxeffort);
        txtMaxWeeklyTtl = (TextView) findViewById(R.id.maxweekly_ttl);
        txtFvrMaxWeight = (TextView) findViewById(R.id.fvrt_maxweight);
        txtFvrMaxEffort = (TextView) findViewById(R.id.fvrt_maxeffort);

        txtStationHeader.setText(item.toUpperCase());
        txtStation.setText("Station " + stationno);
        txtMaxWeight.setText(maxWeight);
        txtMaxEffort.setText(maxEffort);
        txtFvrMaxEffort.setText(maxFvrEffort);
        txtMaxWeeklyTtl.setText(maxWeeklyTtl);
        txtFvrMaxWeight.setText(maxFvrtweight);
        GetMaxValues();

    }

    private void GetMaxValues() {
        String chestType = item.toLowerCase();
        chestType.replace(" ", "-");
        chestType.replace("(", "-");
        chestType.replace(")", "");
//        stationval, muscleval
        String url = "https://www.gymstarpro.com/webapi/stationlog-fav.php?userid=" + AppPreferences.getInstance().getUserId() + "&muscle_type=" +
                chestType + "&station_no=" + stationno + "&favorite=1";

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
                        Toast.makeText(FavoriteActivity.this,"Server Error,Please try again.",Toast.LENGTH_LONG).show();

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
//
//        };

        RequestQueue requestQueue = Volley.newRequestQueue(FavoriteActivity.this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                finish();
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                break;
            case SimpleGestureFilter.SWIPE_UP :
                break;
            default:
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }

    public void ProgressDialogStart() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!FavoriteActivity.this.isDestroyed()) {
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
                if (!FavoriteActivity.this.isDestroyed()) {
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
