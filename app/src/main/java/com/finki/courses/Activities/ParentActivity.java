package com.finki.courses.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.finki.courses.Utils.ThemeUtils;
import com.google.android.material.color.DynamicColors;

public abstract class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ThemeUtils.isNightModeOn(ParentActivity.this)){
            Log.d("Tag", "Night mode is on");
        } else {
            Log.d("Tag", "Day mode is on");
        }
    }

    public abstract void instantiateObjects();
    public abstract void addEventListeners();
    public abstract void additionalThemeChanges();
}
