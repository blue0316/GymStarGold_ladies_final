package com.watchwomen.gymstarsilver;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

public class DatabaseCopyActivity extends AppCompatActivity {

    private DatabaseCopy mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_database_copy);
    }
}
