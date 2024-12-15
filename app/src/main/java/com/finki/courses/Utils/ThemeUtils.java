package com.finki.courses.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class ThemeUtils {

    public static boolean isNightModeOn(Context context){
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void setUpStatusBarColors(Context context, AppCompatActivity appCompatActivity){
        if (!isNightModeOn(context)){
            Window window = appCompatActivity.getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
