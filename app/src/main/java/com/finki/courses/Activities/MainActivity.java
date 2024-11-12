package com.finki.courses.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Fragments.FragmentUser;
import com.finki.courses.Helper.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends ParentActivity {

    private ActivityMainBinding binding;
    private MainActivityHelper mainActivityHelper;
    private Toaster toaster;
    private FragmentHome fragmentHome;
    private FragmentUser fragmentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instantiateObjects();
        additionalThemeChanges();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        addEventListeners();
    }

    @Override
    public void instantiateObjects() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentHome = new FragmentHome();
        fragmentUser = new FragmentUser();

        toaster = new Toaster(MainActivity.this);

        mainActivityHelper = new MainActivityHelper(MainActivity.this, this, binding);
        mainActivityHelper.changeFragments(fragmentHome);
        binding.bottomNavigationView.setSelectedItemId(R.id.itemHome);
    }

    @Override
    public void addEventListeners() {
        binding.main.setOnClickListener(view -> {
            toaster.text("Hello there");
        });

        binding.searchBarMainActivity.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    mainActivityHelper.hideLogoAndTitle();
                } else {
                    mainActivityHelper.showLogoAndTitle();
                }
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.itemHome){
                    mainActivityHelper.changeFragments(fragmentHome);
                } else if (item.getItemId() == R.id.itemUser){
                    mainActivityHelper.changeFragments(fragmentUser);
                }
                return true;
            }
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}