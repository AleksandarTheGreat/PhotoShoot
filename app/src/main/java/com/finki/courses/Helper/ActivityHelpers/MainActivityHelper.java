package com.finki.courses.Helper.ActivityHelpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Fragments.FragmentUser;
import com.finki.courses.R;
import com.finki.courses.databinding.ActivityMainBinding;

public class MainActivityHelper {

    private Context context;
    private ActivityMainBinding binding;
    private AppCompatActivity appCompatActivity;

    public MainActivityHelper(Context context, AppCompatActivity appCompatActivity, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
        this.appCompatActivity = appCompatActivity;
    }

    public void hideLogoAndTitle(){
        binding.imageViewLogo.setVisibility(View.GONE);
        binding.textViewTitle.setVisibility(View.GONE);
    }

    public void showLogoAndTitle(){
        binding.imageViewLogo.setVisibility(View.VISIBLE);
        binding.textViewTitle.setVisibility(View.VISIBLE);
    }

    public void changeFragments(Fragment fragment){
//        if (fragment instanceof FragmentUser)
//            binding.bottomNavigationView.setSelectedItemId(R.id.itemUser);
//        else if (fragment instanceof FragmentHome)
//            binding.bottomNavigationView.setSelectedItemId(R.id.itemHome);

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(binding.fragmentContainerViewMainActivity.getId(), fragment)
                .commit();

    }

}







