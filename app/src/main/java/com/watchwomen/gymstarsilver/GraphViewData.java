package com.watchwomen.gymstarsilver;

/**
 * Created by Administrator on 5/1/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;




//import android.util.Log;

public class GraphViewData extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    Calendar cal;
    SimpleDateFormat dateFormat, timeFormat;
    String current_date, previous_date, current_time, getGraphResults, muscleval, stationval;
    ProgressDialog mProgressDialog;
    GraphView graph;
    private SimpleGestureFilter detector;
    int i = 0, maxlimit;
    private GraphView graphView;

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.graph);
        graph = (GraphView) findViewById(R.id.graph1);
        graph.setVisibility(View.INVISIBLE);

        mProgressDialog = new ProgressDialog(GraphViewData.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        Bundle bundle = getIntent().getExtras();

        muscleval = bundle.getString("muscle_name");
        stationval = bundle.getString("station_no");
        current_date = bundle.getString("currentDate");
        previous_date = bundle.getString("previousDate");


        getGraphResults();
    }
   /* public View  onCreate(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        /*supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);
        Bundle bundle = getIntent().getExtras();
        BarGraphSeries<DataPoint> series;
        final View view = inflater.inflate(R.layout.graph, container, false);
        // setup UI controls
        initUi(view);
        getGraphResults();
        return view;
     /*   muscleval = bundle.getString("muscle_name");
        stationval = bundle.getString("station_no");
        current_date = bundle.getString("currentDate");
        previous_date = bundle.getString("previousDate");

        mProgressDialog=new ProgressDialog(GraphSeries.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("h:mm a");
        // Line Graph


    }*/

    public void getGraphResults() {
        getGraphResults = AppPreferences.getInstance().url + "action=get_graph_data";
        ProgressDialogStart();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, getGraphResults,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DataPoint todayDatapoint, lastdayDatapoint;

                       /* BarGraphSeries<DataPoint> todayseries = new BarGraphSeries<>(new DataPoint[] {
                                new DataPoint(0,0)
                        });
                        BarGraphSeries<DataPoint> lastdayseries = new BarGraphSeries<>(new DataPoint[] {
                                new DataPoint(0,0)
                        });*/


                        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                                new DataPoint(0, 0)
                                // new DataPoint(1, 5),
                                // new DataPoint(2, 3),
                                // new DataPoint(3, 2),
                                // new DataPoint(4, 6)
                        });



                       // graphView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        series1.setColor(Color.BLUE);
                        series1.setDrawDataPoints(true);
                        //series1.setDataPointsRadius(2);
                        //series1.setBackgroundColor(Color.BLUE);
//                        series1.appendData(new DataPoint(1,5),true,10);
//                        series1.appendData(new DataPoint(2,3),true,10);
//                        series1.appendData(new DataPoint(3,2),true,10);
//                        series1.appendData(new DataPoint(4,6),true,10);




                        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{
                                new DataPoint(0, 0)
//                                new DataPoint(1, 10),
//                                new DataPoint(2, 6),
//                                new DataPoint(3, 7),
//                                new DataPoint(4, 8)
                        });
                        series2.setColor(Color.YELLOW);
                        series2.setDrawDataPoints(true);
                        //series2.setDataPointsRadius(2);

/*                        series2.appendData(new DataPoint(1,10),true,10);
                        series2.appendData(new DataPoint(2,6),true,10);
                        series2.appendData(new DataPoint(3,7),true,10);
                        series2.appendData(new DataPoint(4,8),true,10);

*/
                        //graph.addSeries(series1);
                        //graph.addSeries(series2);

                        //styling
                        /*series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint dataPoint) {
                                return Color.rgb((int) dataPoint.getX() * 255 / 4, (int) Math.abs(dataPoint.getY() * 255 / 6), 100);
                            }
                        });*/

                        //series1.setSpacing(20);


                        /*series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint dataPoint) {
                                return Color.rgb((int) dataPoint.getX() * 255 / 4, (int) Math.abs(dataPoint.getY() * 255 / 6), 100);
                            }
                        });*/

                        //series2.setSpacing(30);


                        //draw values on top
//                        series1.setDrawValuesOnTop(true);
//                      series1.setValuesOnTopColor(Color.RED);


//                        series2.setDrawValuesOnTop(true);
//                        series2.setValuesOnTopColor(Color.BLUE);


                        try {
                            // JSONObject jobj = new JSONObject(response);
                            int todayMaxval = 0, lastdayMaxval = 0;
                            //JSONArray rows= jobj.getJSONArray("set_number");
                            JSONArray rows = new JSONArray(response);
                            if (rows.length() > 0) {
                                //graph.setVisibility(View.VISIBLE);
                            } else {
                                graph.setVisibility(View.INVISIBLE);
                            }
                            for (i = 0; i < rows.length(); i++) {

                                JSONObject obj = rows.getJSONObject(i);
                                String todayResult = obj.getString("today_gymtotal");
                                String lastResult = obj.getString("last_gymtotal");

                                if (todayMaxval < Integer.parseInt(todayResult)) {
                                    todayMaxval = Integer.parseInt(todayResult);
                                }

                                if (lastdayMaxval < Integer.parseInt(lastResult)) {
                                    lastdayMaxval = Integer.parseInt(lastResult);
                                }
                            }

                            if (lastdayMaxval > todayMaxval) {
                                maxlimit = lastdayMaxval;
                            } else {
                                maxlimit = todayMaxval;
                            }


                            for (i = 0; i < rows.length(); i++) {

                                JSONObject jobj = rows.getJSONObject(i);
                                String todayResult = jobj.getString("today_gymtotal");
                                String lastResult = jobj.getString("last_gymtotal");
                                String set_number = jobj.getString("set_number");


                                //todayDatapoint = new DataPoint(Integer.parseInt(set_number),Integer.parseInt(todayResult));
                                //lastdayDatapoint = new DataPoint(Integer.parseInt(set_number),Integer.parseInt(lastResult));

                                series1.appendData(new DataPoint(Integer.parseInt(set_number), Integer.parseInt(todayResult)), true, maxlimit);
                                series2.appendData(new DataPoint(Integer.parseInt(set_number), Integer.parseInt(lastResult)), true, maxlimit);

                                //        todayseries.appendData(todayDatapoint,true,10);

                                //      lastdayseries.appendData(lastdayDatapoint,true,10);

                            }
                            /*todayseries.setSpacing(50);
                            todayseries.setValuesOnTopColor(Color.BLACK);
                            todayseries.setDrawValuesOnTop(true);
                            lastdayseries.setSpacing(50);
                            lastdayseries.setDrawValuesOnTop(true);
                            lastdayseries.setValuesOnTopColor(Color.YELLOW);*/

                            graph.addSeries(series1);
                            graph.addSeries(series2);
                            graph.getViewport().setMinX(0);
                            graph.getViewport().setMinY(0);
                            graph.setBackgroundColor(Color.rgb(128, 0, 32));
                            graph.setPadding(1,1,1,1);


                           // graph.getLegendRenderer().setVisible(true);

                            graph.getLegendRenderer().setTextColor(Color.WHITE);
                            graph.getLegendRenderer().setMargin(1);
                            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
                            graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);// remove horizontal x labels and line
                            graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
                            //graph.getGridLabelRenderer().setHorizontalAxisTitle("Set #");
                            graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                            graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                            graph.getViewport().setMaxX(rows.length());
                            graph.getViewport().setMaxY(maxlimit);
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setXAxisBoundsManual(true);

                            graph.getGridLabelRenderer().setLabelsSpace(1);
                            //graph.getGridLabelRenderer().setHorizontalAxisTitle("STATION #");
                            //graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
                            graph.getGridLabelRenderer().setHumanRounding(true);

                            //graph.getViewport().setScalable(true);
                            graph.getViewport().setScrollable(true);


                            graph.setVisibility(View.VISIBLE);
                            //String last_set= jobj.getString("last_set");

                        } catch (JSONException e) {
                            System.out.println(e.toString());
                            e.printStackTrace();
                        }
                        ProgressDialogDismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GraphViewData.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
                        System.out.println(error.toString());
                        //ProgressDialogDismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();//adding post param.

                params.put("user_id", AppPreferences.getInstance().getUserId());
                params.put("current_date", current_date);
                params.put("muscle_name", muscleval);
                params.put("previous_date", previous_date);

                params.put("station_no", stationval);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(GraphViewData.this);
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

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!GraphViewData.this.isDestroyed()) {
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
                if (!GraphViewData.this.isDestroyed()) {
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

    public void onSwipe(int direction) {
        String str = "";
        Intent j;
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                finish();
//                j = new Intent(GraphViewData.this, CalculatorActivity.class);
//                startActivity(j);
                break;
        }

    }
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}




