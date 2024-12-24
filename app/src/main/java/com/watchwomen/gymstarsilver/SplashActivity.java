package com.watchwomen.gymstarsilver;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
//      ImageView imageView = (ImageView)findViewById(R.id.imageView);
//      imageView.setImageBitmap(Utils.GetImageFromAssets(this, "splash.png"));
        new CountDownTimer(Constants.SPLASH_TIME, 100){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, NavigationMenuActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
