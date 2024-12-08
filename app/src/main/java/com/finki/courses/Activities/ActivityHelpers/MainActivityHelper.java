package com.finki.courses.Activities.ActivityHelpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.finki.courses.Fragments.FragmentAddPost;
import com.finki.courses.Fragments.FragmentGallery;
import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Fragments.FragmentUser;
import com.finki.courses.R;
import com.finki.courses.databinding.ActivityMainBinding;

public class MainActivityHelper {

    private final Context context;
    private final ActivityMainBinding binding;
    private final AppCompatActivity appCompatActivity;

    public MainActivityHelper(Context context, AppCompatActivity appCompatActivity, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.appCompatActivity = appCompatActivity;
    }

    public void changeFragments(Fragment fragment, boolean shouldPutOnStack) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        if (shouldPutOnStack) {
            fragmentManager.beginTransaction()
                    .addToBackStack("stack1")
                    .replace(binding.fragmentContainerViewMainActivity.getId(), fragment)
                    .commit();
        } else {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction()
                    .replace(binding.fragmentContainerViewMainActivity.getId(), fragment)
                    .commit();
        }
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

}



