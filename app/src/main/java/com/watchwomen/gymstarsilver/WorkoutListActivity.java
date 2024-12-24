package com.watchwomen.gymstarsilver;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;

import java.util.ArrayList;



public class WorkoutListActivity extends AppCompatActivity {

    String mode;
    WheelView wheelView;
    ArrayList<Integer> listItems;
    TextView headertext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_workout_list);

        initView();

    }

    private void initView() {
        AppPreferences.getInstance().setTotalRunningTime(0);
        AppPreferences.getInstance().setRunningTime(0);
        mode = getIntent().getStringExtra("mode");
        wheelView = (WheelView) findViewById(R.id.wheelview);
        headertext = (TextView) findViewById(R.id.headertext);
        listItems = new ArrayList<>();
        if(mode.equals("arms")){
            listItems.add(R.drawable.sub_btn_arms_biceps);
            listItems.add(R.drawable.sub_btn_arms_triceps);
            headertext.setText("ARM");
        }
        else if(mode.equals("chest")){
            listItems.add(R.drawable.sub_btn_chest_lower);
            listItems.add(R.drawable.sub_btn_chest_upper);
            headertext.setText("CHEST");
        }
        else if(mode.equals("shoulder")){
            listItems.add(R.drawable.sub_btn_shoulder_deltroid_b);
            listItems.add(R.drawable.sub_btn_shoulder_deltroid_f);
            headertext.setText("SHOULDERS");
        }
        else if(mode.equals("ulegs")){
            listItems.add(R.drawable.sub_btn_legs_upper_f);
            listItems.add(R.drawable.sub_btn_legs_upper_b);
            listItems.add(R.drawable.sub_btn_legs_upper_butt);
            headertext.setText("LEGS/Upper");
        }
        else if (mode.equals("membership")) {
            listItems.add(R.drawable.btn_membership_bronze);
            listItems.add(R.drawable.nav_silver_small);

            headertext.setText("Membership");
        }
        wheelView.setWheelItemCount(listItems.size());
        wheelView.setAdapter(new WheelAdapter() {
            @Override
            public Drawable getDrawable(int position) {
                //return drawable here - the position can be seen in the gifs above
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), listItems.get(position));
                return drawable;
            }

            @Override
            public int getCount() {
                //return the count
                return listItems.size();
            }
        });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                if(mode.equals("arms")){
                    switch (position)
                    {
                        case 0:
                        {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Biceps");
                            startActivity(intent);
                            break;
                        }
                        case 1:
                        {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Triceps");
                            startActivity(intent);
                            break;
                        }
                        default:
                            break;
                    }
                }
                else if(mode.equals("chest")){
                    switch (position) {
                        case 0: {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Chest Lower");
                            startActivity(intent);
                            break;
                        }
                        case 1:
                        {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Chest Upper");
                            startActivity(intent);
                            break;
                        }
                        default:
                            break;
                    }
                }
                else if(mode.equals("shoulder")){
                    switch (position)
                    {
                        case 0: {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Deltoid Front");
                            startActivity(intent);
                            break;
                        }
                        case 1: {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Deltoid Back");
                            startActivity(intent);
                            break;
                        }
                        default:
                            break;
                    }
                }
                else if(mode.equals("ulegs")){
                    switch (position) {
                        case 0: {

                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Legs Front");
                            startActivity(intent);
                            break;
                        }
                        case 1:
                        {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Legs Back");
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            Intent intent = new Intent(WorkoutListActivity.this, CalculatorActivity.class);
                            intent.putExtra("item", "Gluteus Maxim(butt)");
                            startActivity(intent);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        });
    }
}
