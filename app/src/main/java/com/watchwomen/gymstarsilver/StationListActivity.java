package com.watchwomen.gymstarsilver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;




public class StationListActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private DatabaseTransactions DBTransact;
    List<StationItem> dataStationList;
    ProgressDialog mProgressDialog;
    private ListView listView;

    String headername;
    private TextView txtStationHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        txtStationHeader = (TextView)findViewById(R.id.stationheadertext);
        dataStationList = new ArrayList<>();
        mProgressDialog=new ProgressDialog(StationListActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        listView = (ListView)findViewById(R.id.station_list);
        headername = getIntent().getStringExtra("muscle_name");

        //getLastResults();
        String header = headername.toLowerCase();
        if (header.contains("gluteus"))
            header = "Gluteus";
        else
            header = headername;

        Log.d("had", header);
        header = header.substring(0, 1).toUpperCase() + header.substring(1);
        String[] arr = header.split(" ");
        String res = "";
        for (int i = 0; i < arr.length; i++)
        {
            String temp = arr[i];
            res += temp.substring(0, 1).toUpperCase() + temp.substring(1);
            res += " ";
        }
        Log.d("header", res);
        txtStationHeader.setText(res );
        GetStationData();
    }

    @Override
    public void onSwipe(int direction) {

        switch (direction)
        {
            case SimpleGestureFilter.SWIPE_RIGHT:
            {
                finish();
                break;
            }
        }
    }

    @Override
    public void onDoubleTap() {

    }

    public class StationItem
    {
        String itemId = "";
        String itemTitle1 = "";
        String itemTitle2 = "";

        public StationItem(String itemId, String itemTitle1, String itemTitle2) {
            this.itemId = itemId;
            this.itemTitle1 = itemTitle1;
            this.itemTitle2 = itemTitle2;
        }
        public StationItem(JSONObject jsonObject) {
            try {
                this.itemId = jsonObject.getString("id");
                this.itemTitle1 = jsonObject.getString("disclosure_support");
                this.itemTitle2 = jsonObject.getString("title2");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemTitle1() {
            return itemTitle1;
        }

        public void setItemTitle1(String itemTitle1) {
            this.itemTitle1 = itemTitle1;
        }

        public String getItemTitle2() {
            return itemTitle2;
        }

        public void setItemTitle2(String itemTitle2) {
            this.itemTitle2 = itemTitle2;
        }
    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!StationListActivity.this.isDestroyed()) {
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
                if (!StationListActivity.this.isDestroyed()) {
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

    private void GetStationData() {


        dataStationList = new ArrayList<>();
        String header = headername.toLowerCase();
        header = header.replace(" ", "-");
        header = header.replace("(", "-");
        header = header.replace(")", "");
        Log.d("header", header);
//        https://www.gymstarpro.com/webapi/stationlog.php?userid=18&muscle_type=extra-m
        String url = "https://www.gymstarpro.com/webapi/stationlog.php?userid=" + AppPreferences.getInstance().getUserId() + "&muscle_type=" + header;
       // Toast.makeText(StationListActivity.this,"url: " + url,Toast.LENGTH_LONG).show();
        ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {
                String userId = AppPreferences.getInstance().getUserId();
                String muscleType = header;
                JSONObject jsonObject = DBTransact.getSystemLogRoutines(userId,muscleType);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    dataStationList.add(new StationItem(item));
                    listView.setAdapter(new StationListAdapter());
                }

            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(StationListActivity.this, "No data found.", Toast.LENGTH_LONG).show();
            }

            ProgressDialogDismiss();

        }else {


            Log.d("url", url);
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("stationList", response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    dataStationList.add(new StationItem(item));
                                    listView.setAdapter(new StationListAdapter());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                                Toast.makeText(StationListActivity.this, "No data found.", Toast.LENGTH_LONG).show();
                            }

                            ProgressDialogDismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(StationListActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();

                            ProgressDialogDismiss();

                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(StationListActivity.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//
//        dataStationList.add(new StationItem("1", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("2", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("3", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("4", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("5", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("6", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("7", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("8", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("9", "Dumbls", "StdCtl"));
//        dataStationList.add(new StationItem("10", "Dumbls", "StdCtl"));
        }

    }

    public class StationListAdapter extends BaseAdapter
    {
        public StationListAdapter() {

        }

        @Override
        public int getCount() {
            return dataStationList.size();

        }

        @Override
        public StationItem getItem(int i) {
            return dataStationList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
            {
                view = LayoutInflater.from(StationListActivity.this).inflate(R.layout.cell_station_list, null);
            }
            TextView textView1, textView2, textView3;
            textView1 = (TextView) view.findViewById(R.id.textView1);
            textView2 = (TextView) view.findViewById(R.id.textView2);
            textView3 = (TextView) view.findViewById(R.id.textView3);
            StationItem item = getItem(i);
            textView1.setText(item.getItemId());
            textView2.setText(item.getItemTitle1());
            textView3.setText(item.getItemTitle2());
            return view;
        }
    }
}
