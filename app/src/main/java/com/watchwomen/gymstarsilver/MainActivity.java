package com.watchwomen.gymstarsilver;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import com.lukedeighton.wheelview.Circle;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.lukedeighton.wheelview.transformer.WheelItemTransformer;
import com.lukedeighton.wheelview.transformer.WheelSelectionTransformer;



public class MainActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener{


    private static final int ITEM_COUNT = 6;
    private static final int arrBtn[] = {R.drawable.top_results, R.drawable.btn_chest_small,
            R.drawable.btn_back_small, R.drawable.btn_legs_small,
            R.drawable.sub_btn_calves, R.drawable.btn_shoulders_small,
            R.drawable.btn_abdominal_small, R.drawable.btn_xtram_small,
            R.drawable.btn_forearms_small, R.drawable.btn_crosstrain_small,R.drawable.btn_arms_small};//, R.drawable.btn_logout}; // ,R.drawable.btn_xtrain_small};
    private TextView chagre;
    private DigitalClock clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);



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

        wheelView.setWheelItemCount(6);
        Log.d("whell", wheelView.getSelectionAngle() + "");

//        wheelView.setWheelSelectionTransformer(new WheelSelectionTransformer() {
//            @Override
//            public void transform(Drawable drawable, WheelView.ItemState itemState) {
//                if (itemState == )
//            }
//        });

        wheelView.setAdapter(new WheelAdapter() {
            @Override
            public Drawable getDrawable(int position) {
                //return drawable here - the position can be seen in the gifs above
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), arrBtn[position]);
                return drawable;
            }

            @Override
            public int getCount() {
                //return the count
                return arrBtn.length;
            }
        });
        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
             //  Toast.makeText(getApplicationContext(), "position " + position, Toast.LENGTH_LONG).show();

                switch (position)
                {
                    case 0: {
                        Intent i = new Intent(MainActivity.this, MainHighestNoActivity.class );
                        startActivity( i );
                        break;
                    }
                    case 1: {
                        Intent i=new Intent(MainActivity.this, WorkoutListActivity.class);
                        i.putExtra("mode","chest");
                        startActivity(i);
                        break;
                    }
                    case 2: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","back");
                        startActivity(i);
                        break;
                    }
                    case 4: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","lower legs calves");
                        startActivity(i);
                        break;
                    }
                    case 5: {
                        Intent i=new Intent(MainActivity.this, WorkoutListActivity.class);
                        i.putExtra("mode","shoulder");
                        startActivity(i);
                        break;
                    }

                    case 6: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","abdominal");
                        startActivity(i);
                        break;
                    }

                    case 7: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","XTra Muscle");
                        startActivity(i);
                        break;
                    }

                    case 8: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","forearms");
                        startActivity(i);
                        break;
                    }
                    case 9: {
                        Intent i=new Intent(MainActivity.this, CalculatorActivity.class);
                        i.putExtra("item","CrossTrain");
//                      i.putExtra("item","CrossTrain");
                        startActivity(i);
                        break;
                    }
//                    case 10:
//                    {
//                        Intent i=new Intent(MainActivity.this, CustomDialogClass.class);
//                        i.putExtra("item","crosstrain");
//                        startActivity(i);
//                        break;
//                    }

                    case 3: {
                        Intent i=new Intent(MainActivity.this, WorkoutListActivity.class);
                        i.putExtra("mode","ulegs");
                        startActivity(i);
                        break;
                    }

                    case 10: {
                        Intent i=new Intent(MainActivity.this, WorkoutListActivity.class);
                        i.putExtra("mode","arms");
                        startActivity(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        Intent i;

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
//              Toast.makeText(this, "Swipe Right", Toast.LENGTH_LONG).show();
                i= new Intent(MainActivity.this, NavigationMenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;

            case SimpleGestureFilter.SWIPE_LEFT:
                break;

            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                i = new Intent(MainActivity.this, CustomDialogClass.class);
                startActivity(i);
                break;

            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";


                break;
        }
    }

    @Override
    public void onDoubleTap() {
    }
}
