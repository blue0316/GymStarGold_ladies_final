package com.watchwomen.gymstarsilver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import com.lukedeighton.wheelview.Circle;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.lukedeighton.wheelview.transformer.WheelItemTransformer;
import com.lukedeighton.wheelview.transformer.WheelSelectionTransformer;

import org.json.JSONObject;

import java.io.IOException;




public class NavigationMenuActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private DatabaseCopy mDBHelper;
    private SQLiteDatabase mDb;
    private DatabaseTransactions DBTransact;

    private SimpleGestureFilter detector;
    private static final String TAG = "NavigationMenuActivity";
    private static final int ITEM_COUNT = 6;
    private static final int arrBtn[] = {R.drawable.nav_btn_routine_black, R.drawable.nav_btn_workout_black, R.drawable.nav_btn_instruction_black, R.drawable.nav_btn_whatsnew_black,
            R.drawable.nav_btn_membership_black,  R.drawable.nav_btn_logout,  R.drawable.nav_btn_sync};
    String mode = "";
    private TextView chagre;
    private DigitalClock clock;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        supportRequestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_navigation_menu );
        mDBHelper = new DatabaseCopy(this);
        DBTransact = new DatabaseTransactions(this);

        if (getIntent().hasExtra("mode")) {
            mode = getIntent().getStringExtra("mode");
        } else {
            mode = "";
        }

        Log.d("offline status", String.valueOf(AppPreferences.getInstance().getOfflineStatus()));

  /*
       if (!mDBHelper.checkDataBase()) {
         mDBHelper = new DatabaseCopy(this);

            try {
                mDBHelper.updateDataBase();
                Toast.makeText(this, "Database copied!", Toast.LENGTH_LONG).show();
            } catch (IOException mIOException) {
                Toast.makeText(this, "Database copy error", Toast.LENGTH_LONG).show();
                throw new Error("UnableToUpdateDatabase");
            }

            try {
                mDb = mDBHelper.getWritableDatabase();
            } catch (SQLException mSQLException) {
                throw mSQLException;
            }

        }else{
            Toast.makeText(this, "Database exist!", Toast.LENGTH_LONG).show();
       } */


        chagre = (TextView) findViewById(R.id.clocktext);// TextView to display Time
        clock = (DigitalClock) findViewById(R.id.clock);// DigitalClock With visibility=gone

        clock.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}
            @Override
            public void afterTextChanged(Editable s) {

                chagre.setText(s.toString().toLowerCase());//will be called when system clock updataes

            }
        });



        AppPreferences.getInstance().setTotalRunningTime(0);
        AppPreferences.getInstance().setRunningTime(0);

        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        wheelView.setWheelSelectionTransformer(new WheelSelectionTransformer() {
            @Override
            public void transform(Drawable drawable, WheelView.ItemState itemState) {
                Circle circle = itemState.getBounds();
                Log.d("mycircle", circle.getCenterX() + " , " +  circle.getCenterY() + ", " + circle.getRadius());
            }
        });

        wheelView.setWheelItemTransformer(new WheelItemTransformer() {
            @Override
            public void transform(WheelView.ItemState itemState, Rect itemBounds) {
                float scale = itemState.getAngleFromSelection() * 0.014f;
                scale = Math.min(1.12f, 1.2f - Math.min(0.5f, Math.abs(scale)));
                Circle bounds = itemState.getBounds();
                float radius = bounds.getRadius() * scale;
                float x = bounds.getCenterX();
                float y = bounds.getCenterY();
                itemBounds.set(Math.round(x - radius), Math.round(y - radius), Math.round(x + radius), Math.round(y + radius));
            }
        });

        wheelView.setWheelItemCount(ITEM_COUNT);
        Log.d("whell", wheelView.getSelectionAngle() + "");

//        wheelView.setWheelSelectionTransformer(new WheelSelectionTransformer() {
//            @Override
//            public void transform(Drawable drawable, WheelView.ItemState itemState) {
//                if (itemState == )
//            }
//        });
        initView();
    }

    private void initView() {
        final WheelView wheelView = (WheelView) findViewById( R.id.wheelview );
        detector = new SimpleGestureFilter( this, this );
       wheelView.setWheelItemCount(ITEM_COUNT);

        if(mode.equals("")) {
            if (AppPreferences.getInstance().getLoginStatus() == true) {

                String userId = AppPreferences.getInstance().getUserId();

                Boolean syncStatus = DBTransact.getSyncStatus(userId);
                Log.d("sync : " , String.valueOf(syncStatus));
                Intent intent = new Intent(NavigationMenuActivity.this, SyncDialogActivity.class);
                if (syncStatus) {
                    intent.putExtra("syncStatus","true");
                }else{
                    intent.putExtra("syncStatus","false");
                }

                startActivity(intent);
            }
        }


        wheelView.setWheelSelectionTransformer( new WheelSelectionTransformer() {
            @Override
            public void transform(Drawable drawable, WheelView.ItemState itemState) {

            }
        });

        wheelView.setAdapter( new WheelAdapter() {
            @Override
            public Drawable getDrawable(int position) {
                //return drawable here - the position can be seen in the gifs above
                Drawable drawable = ContextCompat.getDrawable( getApplicationContext(), arrBtn[position] );
                return drawable;
            }

            @Override
            public int getCount() {
                //return the count
                return arrBtn.length;
            }
        });

      //  {R.drawable.nav_btn_routine_black, R.drawable.nav_btn_workout_black, R.drawable.nav_btn_instruction_black, R.drawable.nav_btn_whatsnew_black,
        //        R.drawable.nav_btn_membership_black,  R.drawable.nav_btn_logout,  R.drawable.nav_btn_sync};

        wheelView.setOnWheelItemClickListener( new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                //  Toast.makeText(getApplicationContext(), "position " + position, Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0: {
                        Intent i = new Intent( NavigationMenuActivity.this, Login.class );
                        i.putExtra( "mode", "routine" );
                        startActivity( i );
                        break;
                    }
                    case 1: {
                        Intent i = new Intent( NavigationMenuActivity.this, Login.class );
                        i.putExtra( "mode", "workout" );
                        startActivity( i );
                        break;
                    }
                    case 4: {
                        Intent intent = new Intent( NavigationMenuActivity.this, MembershipActivity.class );
                        intent.putExtra( "mode", "membership" );
                        startActivity( intent );
                        break;
                    }
                    case 5: {
                        Intent i = new Intent( NavigationMenuActivity.this, CustomDialogClass.class );
//                      i.putExtra("mode", "workout");
                        startActivity( i );
                        break;
                    }
                    case 6: {
                        Intent i = new Intent( NavigationMenuActivity.this, Login.class );
                        i.putExtra( "mode", "sync" );
                        startActivity( i );
                        break;
                    }
                   case 2: {
                        Intent intent = new Intent( NavigationMenuActivity.this, InstructionsActivity.class );
                        intent.putExtra( "mode", "membership" );
                        startActivity( intent );
                        break;
                    }
                }
            }
        } );
    }

    private void runNotification() {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        Notification notification = new NotificationCompat.Builder( this, "999" )
                .setSmallIcon( R.mipmap.ic_bull )
                .setContentTitle( "title" )
                .setContentText( "text" )
                .setLargeIcon( BitmapFactory.decodeResource( getResources(),
                        R.drawable.bull_calcresult ) )
                .setStyle( new NotificationCompat.BigTextStyle()
                        .bigText( "Apply NotificationCompat.MessagingStyle to display sequential messages between any number of people. This is ideal for messaging apps because it provides a consistent layout for each message by handling the sender name and message text separately, and each message can be multiple lines long.\n" +
                                "\n" +
                                "To add a new message, call addMessage(), passing the message text, the time received, and the sender name. You can also pass this information as a NotificationCompat.MessagingStyle.Message object." ) )

                .build();
        notificationManager.notify( 999, notification );
    }


    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent( me );
        return super.dispatchTouchEvent( me );
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";

                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";

                break;

            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
//                Intent i = new Intent( this, MainHighestNoActivity.class );
//                startActivity( i );

                break;

            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                break;

        }
    }

    @Override
    public void onDoubleTap() {
    }

    @Override
    public void onDestroy() {
        //Log.d("destroy: ", String.valueOf(AppPreferences.getInstance().getOfflineStatus()));
        //AppPreferences.getInstance().setOfflineStatus(false);
        super.onDestroy();
    }

}
