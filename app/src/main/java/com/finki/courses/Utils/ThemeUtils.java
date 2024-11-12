package com.finki.courses.Utils;

import android.content.Context;
import android.content.res.Configuration;

public class ThemeUtils {

    public static boolean isNightModeOn(Context context){
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
