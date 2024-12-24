package com.watchwomen.gymstarsilver;

import android.app.Application;

/**
 * Created by mac on 4/3/18.
 */

public class GymStarSilver extends Application {
    private static GymStarSilver instance = null;
    private GymStarSilver mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static GymStarSilver getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new GymStarSilver();
        }
    }

}
