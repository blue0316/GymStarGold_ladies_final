package com.watchwomen.gymstarsilver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;



public class CardioWorkout extends AppCompatActivity {
TextView cardiotext,headertext;
    ImageView backarr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cardio_workout);
        headertext=(TextView)findViewById(R.id.headertext);
        headertext.setText("Cardio Workout");
        cardiotext=(TextView)findViewById(R.id.cardiotext);
        cardiotext.setText("Cardio Workout is a Silver Membership Under Development We Will Let You Know When Ready");
        cardiotext.setGravity(Gravity.CENTER);
        //backarr=(ImageView)findViewById(R.id.backarr);
        /*backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }
}
