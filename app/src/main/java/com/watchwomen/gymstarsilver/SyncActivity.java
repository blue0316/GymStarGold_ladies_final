package com.watchwomen.gymstarsilver;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class SyncActivity extends AppCompatActivity {

    private DatabaseTransactions DBTransact;

    //Highest Max Station
    TextView txt_status;
    public Button login, btn_continue;

    TextView stationHeaderText;
    String muscleName,station_no;
    ProgressDialog mProgressDialog;
    private static final String TAG = "SyncActivity";
    Boolean syncStatus = false;
    int jumpTime = 0;
    String redirectLink = "";

            NetworkInfo wifiCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        supportRequestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_sync );
        login = (Button) findViewById(R.id.btn_login);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        DBTransact = new DatabaseTransactions(this);

        mProgressDialog=new ProgressDialog(SyncActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Sync process");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressNumberFormat(null);
        //mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.setProgress(0);

        String userId = AppPreferences.getInstance().getUserId();
        //syncStatus = DBTransact.getSyncStatus(userId);

        redirectLink = getIntent().getStringExtra("redirectLink");
        if(redirectLink == null){
            redirectLink = "";
        }

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        txt_status  = (TextView) findViewById(R.id.txt_status);

        if (wifiCheck.isConnected()) {
            if (AppPreferences.getInstance().getLoginStatus() == true) {
                txt_status.setText("Sync process started...");
                initView();
            }else{
                txt_status.setText("Login required");
                login.setVisibility(LinearLayout.VISIBLE);
            }
        } else {
            txt_status.setText("Wifi not cconnected");
        }
    }

    private void initView() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SyncActivity.this, Login.class);
                 startActivity(i);
                finish();
                }
        });

        if(redirectLink.equals("logout")){
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.getInstance().setOfflineStatus(true);
                    Intent i = new Intent(SyncActivity.this, CustomDialogClass.class);
                    startActivity(i);
                    finish();
                }
            });

        }else{
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.getInstance().setOfflineStatus(true);
                    Intent i = new Intent(SyncActivity.this, NavigationMenuActivity.class);
                   // i.putExtra( "mode", "offline" );
                    startActivity(i);
                    finish();
                }
            });

        }



        exportData();

        /*if(syncStatus){
            exportData();
        }else{
            Intent intent = new Intent(SyncActivity.this, SyncDialogActivity.class);
            if (syncStatus) {
                intent.putExtra("syncStatus","true");
            }else{
                intent.putExtra("syncStatus","false");
            }
            startActivity(intent);
            finish();

        }*/
    }


    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }


    public void exportData() {



        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

            String userId = AppPreferences.getInstance().getUserId();

            DBTransact = new DatabaseTransactions(this);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject =  DBTransact.getExportData(userId);

                Log.d("Sync Output:", jsonObject.toString(4));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        //String dbSyncUrl = AppPreferences.getInstance().url + "action=dbsync&userid=" + AppPreferences.getInstance().getUserId();
        String dbSyncUrl = "https://www.gymstarpro.com/sync.php?" + "action=dbsync1&userid=" + AppPreferences.getInstance().getUserId();

        ProgressDialogStart();
        ProgressThread();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, dbSyncUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray records;
                            JSONArray station;
                            JSONArray yearDays;
                            JSONArray yearWeeks;
                            JSONArray wppmprousers;
                            JSONArray wpterms;

                            records = response.getJSONArray("records");
                            station = response.getJSONArray("station");
                            yearDays = response.getJSONArray("yeardays");
                            yearWeeks = response.getJSONArray("yearweek");
                            wppmprousers = response.getJSONArray("wppmprousers");
                            wpterms = response.getJSONArray("wpterms");

                            String syncstatus = response.getString("status");

                            Log.d("serverstatus", syncstatus);

                            String userId = AppPreferences.getInstance().getUserId();
                            String status =  DBTransact.syncLocalDB(userId,records,station,yearDays,yearWeeks,wppmprousers,wpterms);

                            Log.d("localstatus", status);

                            if(status.equals("success")){
                                txt_status.setText("Sync process success");
                                btn_continue.setVisibility(LinearLayout.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SyncActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
                ProgressDialogDismiss();
            }
        });



        RequestQueue requestQueue = Volley.newRequestQueue(SyncActivity.this);
        requestQueue.add(jsonObjectRequest);

           /* StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, dbSyncUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if (status.equals("success")) {

                                    JSONObject bodyObject = jsonObject;

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
                            Toast.makeText(SyncActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
                            ProgressDialogDismiss();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //adding post param.
                    params.put("userid", AppPreferences.getInstance().getUserId());
                    Log.d(TAG, "getParams: " + params);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(SyncActivity.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

*/

    }




    public void ProgressDialogDismiss() {

        try {

            mProgressDialog.setProgress(90);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!SyncActivity.this.isDestroyed()) {
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
                if (!SyncActivity.this.isDestroyed()) {
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

    public void ProgressThread(){
        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {

                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(110);
                        jumpTime += 5;
                        mProgressDialog.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

}


