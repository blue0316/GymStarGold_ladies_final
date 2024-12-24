package com.watchwomen.gymstarsilver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RoutineCalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_calculator);

        ImageView btnWR = (ImageView) findViewById(R.id.btnWR);
        ImageView btnArrow = (ImageView) findViewById(R.id.btnArrow);
        Button calresult = (Button) findViewById(R.id.calresult);
        btnWR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act2 = new Intent(view.getContext(), RoutineList.class);
                startActivity(act2);
            }
        });

        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act3 = new Intent(view.getContext(), Routine.class);
                startActivity(act3);
            }
        });

        calresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act4 = new Intent(view.getContext(), RoutineDisplay.class);
                startActivity(act4);
            }
        });
    }
}