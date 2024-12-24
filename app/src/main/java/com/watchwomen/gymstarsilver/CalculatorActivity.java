package com.watchwomen.gymstarsilver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;


public class CalculatorActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private DatabaseTransactions DBTransact;
    TextView header,set;
    EditText stationno,reps,weight;
    String date;
    ImageView calresult;
    Intent i;
    ImageView leftarrow,rightarrow,delete;
    static int bc = 1, forc = 1, abnc = 1, llegsc = 1;
    static int bic = 1, tric = 1, chlc = 1, chuc = 1,delfc=1,delbc=1,legfc=1,legbc=1,gmaxic=1;
    String headername, getLastResults,repsval,weightval,stationval,current_date,previous_date,current_time;

    int timeValue;
    ImageView backarr;
    ProgressDialog mProgressDialog;
    TextView repres,wtres,totres,lastdt,stationnotxt,weighttxt,repstxt;
    private GestureDetectorCompat gestureDetectorCompat;
    private SimpleGestureFilter detector;
    SimpleDateFormat dateFormat,timeFormat;
    Calendar cal,prevcal;
    Date expDate=null;
    Date presentDate=null;
    private static final String TAG = "CalculatorActivity";


    String wr_muscle_name = "";
    String wr_muscle_id = "";
    String wr_week_day = "";
    String wr_routine_name = "";
    String wr_station_no = "";
    String wr_schedule = "";
    JSONArray routineArray     = new JSONArray();
    int stationListIndex = 0;
    boolean isSwipe = false;
    String swipePrevWeight = "";
    String swipePrevReps = "";

    ListView listView;
    TextView txtStationHeader;
    boolean timer=false;
    TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new SimpleGestureFilter(this,this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calculator);

        mProgressDialog=new ProgressDialog(CalculatorActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCanceledOnTouchOutside(false);

        tinydb  = new TinyDB(CalculatorActivity.this);

        if(getIntent().getStringExtra("item").equals("routine_wr")) {

            wr_muscle_name = "";
            wr_muscle_id = "";
            wr_week_day = "";
            wr_station_no = "";
            wr_routine_name = getIntent().getStringExtra("wr_routine_name");
            wr_schedule =  getIntent().getStringExtra("wr_schedule");
            String UserID = AppPreferences.getInstance().getUserId();

            if(getIntent().hasExtra("is_swipe")){

                if(getIntent().getStringExtra("is_swipe").isEmpty()){
                    isSwipe = false;
                    wr_muscle_name = "";
                    wr_station_no = "";
                    swipePrevWeight = "";
                    swipePrevReps = "";
                }else{
                    isSwipe = true;
                    wr_muscle_name = getIntent().getStringExtra("Muscle");
                    wr_station_no = getIntent().getStringExtra("station_no");
                    swipePrevWeight = getIntent().getStringExtra("weight");
                    swipePrevReps = getIntent().getStringExtra("reps");
                }

            }else{

                isSwipe = false;
                wr_muscle_name = "";
                wr_station_no = "";

            }

            Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();

            if (offlineStatus) {

                DBTransact = new DatabaseTransactions(this);
                try {
                    JSONObject jsonObject = DBTransact.getSystemLogRoutines(UserID,wr_routine_name,wr_schedule);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {

                        JSONObject bodyObject1 = jsonObject;

                        routineArray = bodyObject1.getJSONArray("routine_list");
                        //Toast.makeText(CalculatorActivity.this,"Routine length: " + routineArray.length(),Toast.LENGTH_LONG).show();

                        ProgressDialogDismiss();
                        initView();
                       // tinydb = new TinyDB(CalculatorActivity.this);
                    } else {
                        Toast.makeText(CalculatorActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {


                String athleteRoutine = AppPreferences.getInstance().url + "action=getSystemLogRoutines&userid=" + UserID + "&selectedRoutine=" + wr_routine_name + "&selectedSchedule=" + wr_schedule;

                //Toast.makeText(CalculatorActivity.this,"Athlete Routine : " + athleteRoutine,Toast.LENGTH_LONG).show();

                ProgressDialogStart();
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, athleteRoutine,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");

                                    if (status.equals("success")) {

                                        JSONObject bodyObject1 = jsonObject;

                                        routineArray = bodyObject1.getJSONArray("routine_list");
                                        //Toast.makeText(CalculatorActivity.this,"Routine length: " + routineArray.length(),Toast.LENGTH_LONG).show();

                                        ProgressDialogDismiss();
                                        initView();
                                       // tinydb = new TinyDB(CalculatorActivity.this);
                                    } else {
                                        Toast.makeText(CalculatorActivity.this, "Data not found", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(CalculatorActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();
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

                RequestQueue requestQueue = Volley.newRequestQueue(CalculatorActivity.this);
                requestQueue.add(stringRequest);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }

        }else{

            if(getIntent().hasExtra("is_swipe")){

                if(getIntent().getStringExtra("is_swipe").isEmpty()){
                    isSwipe = false;
                    swipePrevWeight = "";
                    swipePrevReps = "";
                }else{
                    isSwipe = true;
                    swipePrevWeight = getIntent().getStringExtra("weight");
                    swipePrevReps = getIntent().getStringExtra("reps");
                }
            }else{
                isSwipe = false;
                swipePrevWeight = "";
                swipePrevReps = "";
            }

            initView();
           // tinydb=new TinyDB(CalculatorActivity.this);
        }
    }

    private void initView() {
        header=(TextView)findViewById(R.id.headertext);
        set=(TextView)findViewById(R.id.set);
        stationno=(EditText)findViewById(R.id.stno);
        reps=(EditText)findViewById(R.id.repsedit);
        weight=(EditText)findViewById(R.id.weightedit);
        calresult=(ImageView)findViewById(R.id.calresult);
        stationno.requestFocus();
        // leftarrow=(ImageView)findViewById(R.id.back);
        //  rightarrow=(ImageView)findViewById(R.id.forward);
        delete=(ImageView)findViewById(R.id.delete);
        //backarr=(ImageView)findViewById(R.id.backarr);
        lastdt=(TextView)findViewById(R.id.lastdate);
        stationnotxt = (TextView)findViewById(R.id.text2);
        weighttxt = (TextView)findViewById(R.id.reps);
        repstxt = (TextView)findViewById(R.id.weight);
        repres=(TextView)findViewById(R.id.text3);
        wtres=(TextView)findViewById(R.id.text4);
        totres=(TextView)findViewById(R.id.text5);
//        listView = (ListView)findViewById(R.id.station_list);

        LinearLayout wrBtnLayout=(LinearLayout)this.findViewById(R.id.wrBtnLayout);
        LinearLayout setContainer=(LinearLayout)this.findViewById(R.id.setContainer);
        ImageView btnWR = (ImageView) findViewById(R.id.btnWR);
        ImageView btnArrow = (ImageView) findViewById(R.id.btnArrow);

        //ImageView testButton = (ImageView) findViewById(R.id.btnTest);


        cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("h:mm a");
        prevcal=cal;
        //prevcal.add(Calendar.DATE, -1);

        lastdt.setText("Last Result "+dateFormat.format(cal.getTime()));


        // gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
       /* backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/




        if(getIntent().getStringExtra("item").equals("routine_wr")) {

            try {
                if(routineArray.length() > 0){
                    JSONObject value = null;

                    if(isSwipe){

                        for(int i=0; i< routineArray.length(); i++){

                            value = routineArray.getJSONObject(i);

                            String station_number = (String) value.getString("station_number");
                            String muscle_name = (String) value.getString("muscle_name");
                            stationListIndex++;
                            if(muscle_name.equals(wr_muscle_name) && station_number.equals(wr_station_no)){

                                wr_muscle_name = muscle_name;
                                wr_station_no = station_number;

                                headername = wr_routine_name + " " + wr_schedule + " / " + wr_muscle_name;
                                //headername = wr_muscle_name;
                                header.setText(headername);
                                stationno.setText(wr_station_no);


                                break;
                            }

                        }
                        weight.setText(swipePrevWeight);
                        reps.setText(swipePrevReps);

                    }else{
                        value = routineArray.getJSONObject(stationListIndex);

                        String station_number = (String) value.getString("station_number");
                        String muscle_name = (String) value.getString("muscle_name");

                        wr_muscle_name = muscle_name;
                        wr_station_no = station_number;

                        headername = wr_routine_name + " " + wr_schedule + " / " + wr_muscle_name;
                        //headername = wr_muscle_name;
                        header.setText(headername);
                        stationno.setText(wr_station_no);
                        stationListIndex++;

                    }

                    getLastResults();
                    weight.requestFocus();
                }else{
                    Toast.makeText(CalculatorActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            wrBtnLayout.setVisibility(LinearLayout.VISIBLE);
			
			TextView headertext = (TextView) findViewById(R.id.headertext);
            headertext.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
            
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)setContainer.getLayoutParams();
            params.setMargins(0, -20, 0, 0);
            setContainer.setLayoutParams(params);

            headername = wr_routine_name + " " + wr_schedule + " / " + wr_muscle_name;

            stationno.setText(wr_station_no);

            btnWR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent act3 = new Intent(view.getContext(), Routine.class);
                    startActivity(act3);
                }
            });

            btnArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                       // Toast.makeText(CalculatorActivity.this,"stationListIndex : " + stationListIndex,Toast.LENGTH_LONG).show();
                        if(routineArray.length() > stationListIndex){
                            JSONObject value = null;
                          //  Toast.makeText(CalculatorActivity.this,"stationListIndex : " + stationListIndex,Toast.LENGTH_LONG).show();

                            value = routineArray.getJSONObject(stationListIndex);

                            String station_number = (String) value.getString("station_number");
                            String muscle_name = (String) value.getString("muscle_name");

                            wr_muscle_name = muscle_name;
                            wr_station_no = station_number;

                            headername = wr_routine_name + " " + wr_schedule + " / " + wr_muscle_name;
                            //headername = wr_muscle_name;
                            header.setText(headername);
                            stationno.setText(wr_station_no);

                            set.setText("SET-1");
                            repres.setText("0/0");
                            totres.setText("0");
                            wtres.setText("0/0");
                            lastdt.setText("Last Result ");
                            weight.setText("");
                            reps.setText("");
                            AppPreferences.getInstance().setLastTotalTime("");
                            AppPreferences.getInstance().setLastRespTime("");

                            stationListIndex++;
                            getLastResults();
                            weight.requestFocus();

                        }else{
                            //Toast.makeText(CalculatorActivity.this,"No Data Found",Toast.LENGTH_LONG).show();

                            Intent i = new Intent(CalculatorActivity.this, WorkoutFinishActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }else{
            wrBtnLayout.setVisibility(LinearLayout.GONE);

           /* LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)setContainer.getLayoutParams();
            params.setMargins(0, -55, 0, 0);
            setContainer.setLayoutParams(params);*/

            headername = getIntent().getStringExtra("item");

            if(isSwipe){
                stationno.setText(getIntent().getStringExtra("station_no"));
                weight.setText(swipePrevWeight);
                reps.setText(swipePrevReps);
                getLastResults();
            }

        }

        //getLastResults();


        header.setText(headername);
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

       /* testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIntent().getStringExtra("item").equals("routine_wr")) {
                    confirmDialogDemo();
                }else{
                    i= new Intent(CalculatorActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(AppPreferences.getInstance().getOfflineStatus()){
                        i.putExtra( "mode", "offline" );
                    }else{
                        i.putExtra( "mode", "online" );
                    }
                    AppPreferences.getInstance().setRunningTime(0);
                    AppPreferences.getInstance().setTotalRunningTime(0);
                    startActivity(i);
                    finish();
                }
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
                       weight.setText("");
                       reps.setText("");
                       getLastResults();
                   }
               }
           }
        );


        calresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculatorActivity.this, DisplayResult.class);

                if ((stationno.getText().toString().equals("")) || (stationno.getText().toString().length()) == 0) {
                    Toast.makeText(CalculatorActivity.this,"Please Enter Station",Toast.LENGTH_LONG).show();
                } else if ((reps.getText().toString().equals("")) || (reps.getText().toString().length()) == 0){
                    Toast.makeText(CalculatorActivity.this,"Please Enter Reps",Toast.LENGTH_LONG).show();
                }else if ((weight.getText().toString().equals("")) || (weight.getText().toString().length()) == 0){
                    Toast.makeText(CalculatorActivity.this,"Please Enter Weight",Toast.LENGTH_LONG).show();
                }
                else{
                    if(wr_muscle_name.isEmpty()) {
                        i.putExtra("muscle_name",headername);
                        i.putExtra("wr_routine_name","");
                        i.putExtra("wr_schedule","");
                    }else{
                        i.putExtra("muscle_name",wr_muscle_name);
                        i.putExtra("wr_routine_name",wr_routine_name);
                        i.putExtra("wr_schedule",wr_schedule);
                    }

                    i.putExtra("station_no",stationno.getText().toString());
                    i.putExtra("reps",reps.getText().toString());
                    i.putExtra("weight",weight.getText().toString());
                    i.putExtra("currentDate",current_date);
                    i.putExtra("currentTime",current_time);
                    i.putExtra("time",timeValue);

                    //prevcal.add(Calendar.DAY_OF_YEAR, -1);
                    i.putExtra("previousDate",previous_date);

                    startActivity(i);
                }

            }
        });

        findViewById(R.id.showlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculatorActivity.this, StationListActivity.class);

                if(wr_muscle_name.isEmpty()) {
                    i.putExtra("muscle_name",headername);
                }else{
                    i.putExtra("muscle_name",wr_muscle_name);
                }


                startActivity(i);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (headername != null && !headername.equals("")) {

            if(wr_muscle_name.isEmpty()) {
                header.setText(headername);
            }else{
                header.setText( wr_routine_name + " " + wr_schedule + " / " + wr_muscle_name);
            }
        }
    }

    public void getLastResults(){
        //getLastResults="http://offers-and-you.com/gymstarpro/restapi.php?action=get_last_results";
        //getLastResults="https://www.gymstarpro.com/restapi.php?action=get_last_results";
        getLastResults = AppPreferences.getInstance().url+"action=get_last_results";
        ProgressDialogStart();

        Boolean offlineStatus = AppPreferences.getInstance().getOfflineStatus();
        if (offlineStatus) {

            DBTransact = new DatabaseTransactions(this);
            try {


                String station_no = stationno.getText().toString();

                String user_id = AppPreferences.getInstance().getUserId();
                String muscle_name = "";
                if (wr_muscle_name.isEmpty()) {
                    muscle_name = headername;
                } else {
                    muscle_name = wr_muscle_name;
                }

                cal = Calendar.getInstance();
                 current_date = dateFormat.format(cal.getTime());
                 current_time = timeFormat.format(cal.getTime());
                cal.add(Calendar.DAY_OF_YEAR, -1);
                previous_date = dateFormat.format(cal.getTime());

                String userId = AppPreferences.getInstance().getUserId();
                JSONObject jobj1 = DBTransact.getLastResults(user_id,station_no,muscle_name,current_date,current_time,previous_date);

                Log.d("JSON Output:", jobj1.toString(4));

                    timer = jobj1.getBoolean("timer");
                    System.out.println("hgffffffffff" + timer);
                    tinydb.putBoolean("timer_check", timer);

                    String last_set = jobj1.getString("new_set");
                    JSONObject jobj = jobj1.getJSONObject("last_results");
                    String exp = jobj1.getString("expiry_date");
                            /*if((exp=="") || (exp=="0000-00-00")){
                                exp = "9999-12-31";
                            }*/
                    //String last_set= jobj.getString("last_set");
                    date = jobj.getString("date");
                    //String last_set= jobj.getString("last_set");
                    String date = jobj.getString("date");
                    String weight = jobj.getString("weight");
                    String reps = jobj.getString("reps");
                    String total = jobj.getString("total");
                    String gymstar_total = jobj.getString("gymstar_total");

                            /*if (exp.equalsIgnoreCase("0000-00-00")){
                                exp = "null";
                            }*/

                    if ((exp != null && !exp.equals("0000-00-00"))) {
                        try {
                            expDate = new SimpleDateFormat("yyyy-MM-dd").parse(exp);
                            presentDate = new SimpleDateFormat("yyyy-MM-dd").parse(current_date);
                        } catch (Exception e) {
                            System.out.println("Error while getting expiry date" + e.toString());
                        }
                        if (expDate.compareTo(presentDate) < 0) {
                            Toast.makeText(CalculatorActivity.this, "Your Subscription has expired.", Toast.LENGTH_LONG).show();
                            AppPreferences.getInstance().setUserID(null);
                            AppPreferences.getInstance().setLoginStatus(false);
                            i = new Intent(CalculatorActivity.this, NavigationMenuActivity.class);
                            startActivity(i);
                        }
                    }

                    // Toast.makeText(CalculatorActivity.this, "last_set: " + last_set, Toast.LENGTH_LONG).show();

                    set.setText("SET-" + last_set);
                    repres.setText(reps);
                    totres.setText(total);
                    wtres.setText(weight);
                    lastdt.setText("Last Result " + date);
                    String total_time = jobj1.getString("total_time");
                    String resp_time = jobj1.getString("repstime");
                    AppPreferences.getInstance().setLastTotalTime(total_time);
                    AppPreferences.getInstance().setLastRespTime(resp_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ProgressDialogDismiss();

        }else {


            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, getLastResults,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("response", response);
                                JSONObject jobj1 = new JSONObject(response);

                                Log.d("JSON Output:", jobj1.toString(4));

                                timer = jobj1.getBoolean("timer");
                                System.out.println("hgffffffffff" + timer);
                                tinydb.putBoolean("timer_check", timer);

                                String last_set = jobj1.getString("new_set");
                                JSONObject jobj = jobj1.getJSONObject("last_results");
                                String exp = jobj1.getString("expiry_date");
                            /*if((exp=="") || (exp=="0000-00-00")){
                                exp = "9999-12-31";
                            }*/
                                //String last_set= jobj.getString("last_set");
                                date = jobj.getString("date");
                                //String last_set= jobj.getString("last_set");
                                String date = jobj.getString("date");
                                String weight = jobj.getString("weight");
                                String reps = jobj.getString("reps");
                                String total = jobj.getString("total");
                                String gymstar_total = jobj.getString("gymstar_total");

                            /*if (exp.equalsIgnoreCase("0000-00-00")){
                                exp = "null";
                            }*/

                                if ((exp != null && !exp.equals("0000-00-00"))) {
                                    try {
                                        expDate = new SimpleDateFormat("yyyy-MM-dd").parse(exp);
                                        presentDate = new SimpleDateFormat("yyyy-MM-dd").parse(current_date);
                                    } catch (Exception e) {
                                        System.out.println("Error while getting expiry date" + e.toString());
                                    }
                                    if (expDate.compareTo(presentDate) < 0) {
                                        Toast.makeText(CalculatorActivity.this, "Your Subscription has expired.", Toast.LENGTH_LONG).show();
                                        AppPreferences.getInstance().setUserID(null);
                                        AppPreferences.getInstance().setLoginStatus(false);
                                        i = new Intent(CalculatorActivity.this, NavigationMenuActivity.class);
                                        startActivity(i);
                                    }
                                }

                                // Toast.makeText(CalculatorActivity.this, "last_set: " + last_set, Toast.LENGTH_LONG).show();

                                set.setText("SET-" + last_set);
                                repres.setText(reps);
                                totres.setText(total);
                                wtres.setText(weight);
                                lastdt.setText("Last Result " + date);
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
                            Toast.makeText(CalculatorActivity.this, "Server Error,Please try again.", Toast.LENGTH_LONG).show();

                            //ProgressDialogDismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();//adding post param.


                    params.put("station_no", stationno.getText().toString());

                    params.put("user_id", AppPreferences.getInstance().getUserId());

                    if (wr_muscle_name.isEmpty()) {
                        params.put("muscle_name", headername);
                    } else {
                        params.put("muscle_name", wr_muscle_name);
                    }
                    System.out.println("kjffsggggggfggf" + "yha ayya" + params.toString());
                    cal = Calendar.getInstance();
                    current_date = dateFormat.format(cal.getTime());
                    current_time = timeFormat.format(cal.getTime());
         /*       try {
                    expDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    presentDate = new SimpleDateFormat("dd/MM/yyyy").parse(current_date);
                }catch(Exception e){

                }
                if (expDate.compareTo(presentDate)<0){
                    i= new Intent(Calculateresult.this, Home.class);
                    startActivity(i);
                }*/
                    params.put("current_date", current_date);
                    params.put("current_time", current_time);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    previous_date = dateFormat.format(cal.getTime());
                    params.put("previous_date", previous_date);

                    Log.d(TAG, "getParams: " + params.toString());
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(CalculatorActivity.this);
            requestQueue.add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    public void ProgressDialogDismiss() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!CalculatorActivity.this.isDestroyed()) {
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
                if (!CalculatorActivity.this.isDestroyed()) {
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

    private void confirmDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm!");
        builder.setMessage("Are You Sure You Want To Quite Your Routine?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(getIntent().getStringExtra("item").equals("routine_wr")) {

                i= new Intent(CalculatorActivity.this, NavigationMenuActivity.class);
            }else{
                i= new Intent(CalculatorActivity.this, MainActivity.class);
            }

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            if(AppPreferences.getInstance().getOfflineStatus()){
                i.putExtra( "mode", "offline" );
            }else{
                i.putExtra( "mode", "online" );
            }

            AppPreferences.getInstance().setRunningTime(0);
            AppPreferences.getInstance().setTotalRunningTime(0);
            startActivity(i);
            finish();

        }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //
        }
        });

        builder.show();

    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:

              /*  if(getIntent().getStringExtra("item").equals("routine_wr")) {

                    i= new Intent(CalculatorActivity.this, NavigationMenuActivity.class);
                }else{
                    i= new Intent(CalculatorActivity.this, MainActivity.class);
                }

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


                AppPreferences.getInstance().setRunningTime(0);
                AppPreferences.getInstance().setTotalRunningTime(0);
                startActivity(i);
                finish();
                break;*/

            if(getIntent().getStringExtra("item").equals("routine_wr")) {
                confirmDialogDemo();
            }else{
                i= new Intent(CalculatorActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if(AppPreferences.getInstance().getOfflineStatus()){
                    i.putExtra( "mode", "offline" );
                }else{
                    i.putExtra( "mode", "online" );
                }
                AppPreferences.getInstance().setRunningTime(0);
                AppPreferences.getInstance().setTotalRunningTime(0);
                startActivity(i);
                finish();
            }

            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                /*i = new Intent(Calculateresult.this, GraphViewData.class);
                i.putExtra("muscle_name",headername);
                i.putExtra("station_no",stationno.getText().toString());
                i.putExtra("currentDate",current_date);

                //prevcal.add(Calendar.DAY_OF_YEAR, -1);
                i.putExtra("previousDate",previous_date);*/

                // startActivity(i);
                if(stationno.getText().toString().length()>0) {
                    i = new Intent(CalculatorActivity.this, GraphViewData.class);

                    if(wr_muscle_name.isEmpty()) {
                        i.putExtra("muscle_name",headername);
                    }else{
                        i.putExtra("muscle_name",wr_muscle_name);
                    }

                    i.putExtra("station_no",stationno.getText().toString());
                    i.putExtra("currentDate",current_date);

                    //prevcal.add(Calendar.DAY_OF_YEAR, -1);
                    i.putExtra("previousDate",previous_date);

//                    startActivity(i);
                }else{
                    Toast.makeText(CalculatorActivity.this,"Please enter Station Number and try again.",Toast.LENGTH_LONG).show();
                }
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
//                i = new Intent(CalculatorActivity.this, CustomDialogClass.class);
//                startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
//                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                break;

        }
    }
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    }

