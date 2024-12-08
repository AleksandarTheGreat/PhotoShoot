package com.finki.courses.Activities.Application;

import android.app.Application;
import android.util.Log;

import com.google.android.material.color.DynamicColors;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        Log.d("Tag", "Dynamic colors applied");
    }
}
