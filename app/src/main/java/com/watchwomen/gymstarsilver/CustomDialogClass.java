package com.watchwomen.gymstarsilver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class CustomDialogClass extends AppCompatActivity implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no, sync;
    private DatabaseTransactions DBTransact;
    TextView status_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customlayout);
        DBTransact = new DatabaseTransactions(this);
        status_msg  = (TextView) findViewById(R.id.txt_dia);
        sync = (Button) findViewById(R.id.btn_sync);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        sync.setOnClickListener(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        String userId = AppPreferences.getInstance().getUserId();

        Boolean syncStatus = DBTransact.getSyncStatus(userId);
        if (syncStatus) {
            status_msg.setText("Unsaved local data exist. Sync before logout to prevent the data loss");
            sync.setVisibility(View.VISIBLE);
            yes.setText("Logout");
            no.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                AppPreferences.getInstance().setUserID(null);
                AppPreferences.getInstance().setLoginStatus(false);
                //c.finish();
                Intent i = new Intent(CustomDialogClass.this, NavigationMenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
            case R.id.btn_sync:
                Intent in = new Intent(CustomDialogClass.this, SyncActivity.class);
                in.putExtra("redirectLink","logout");
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                finish();
                break;
            case R.id.btn_no:
                finish();
                break;
            default:
                break;
        }

    }

}