package com.finki.courses.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.finki.courses.Fragments.FragmentFeed;
import com.finki.courses.Fragments.FragmentGallery;
import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Fragments.FragmentUser;
import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.R;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoriesLoadedCallBack;
import com.finki.courses.ViewModel.ViewModelCategories;
import com.finki.courses.ViewModel.ViewModelPosts;
import com.finki.courses.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends ParentActivity {

    private ActivityMainBinding binding;
    private MainActivityHelper mainActivityHelper;
    private Toaster toaster;
    private FragmentHome fragmentHome;
    private FragmentUser fragmentUser;
    private FragmentGallery fragmentGallery;
    private FragmentFeed fragmentFeed;
    private FirebaseAuth firebaseAuth;

    // ViewModels Here
    private ViewModelCategories viewModelCategories;
    private ViewModelPosts viewModelPosts;

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
        // validate current user before anything else
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            Log.d("Tag", "Current user is null");
            Intent openLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(openLoginActivity);
            finish();
        } else {
            Log.d("Tag", "'" + user.getEmail() + "' is signed in");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModels here
        viewModelCategories = new ViewModelProvider(this).get(ViewModelCategories.class);
        viewModelCategories.init(MainActivity.this);
        viewModelCategories.listAll(new OnCategoriesLoadedCallBack() {
            @Override
            public void onLoaded(List<Category> categories) {
                viewModelCategories.getMutableLiveDataCategories().setValue(categories);
                toaster.text("Loaded all from firebase");
            }
        });

        mainActivityHelper = new MainActivityHelper(MainActivity.this, this, binding);

        viewModelPosts = new ViewModelProvider(this).get(ViewModelPosts.class);
        viewModelPosts.init(MainActivity.this, mainActivityHelper);

        fragmentHome = new FragmentHome(mainActivityHelper);
        fragmentUser = new FragmentUser(mainActivityHelper);
        fragmentGallery = new FragmentGallery(mainActivityHelper);
        fragmentFeed = new FragmentFeed(mainActivityHelper);

        toaster = new Toaster(MainActivity.this);

        mainActivityHelper.changeFragments(fragmentHome, false);
        binding.bottomNavigationView.setSelectedItemId(R.id.itemHome);
    }

    @Override
    public void addEventListeners() {
        binding.main.setOnClickListener(view -> {
            toaster.text("Hello there");
        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.itemHome){
                    mainActivityHelper.changeFragments(fragmentHome, false);
                } else if (item.getItemId() == R.id.itemUser){
                    mainActivityHelper.changeFragments(fragmentUser, false);
                } else if (item.getItemId() == R.id.itemGallery){
                    mainActivityHelper.changeFragments(fragmentGallery, false);
                } else if (item.getItemId() == R.id.itemFeed){
                    mainActivityHelper.changeFragments(fragmentFeed, false);
                }
                return true;
            }
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}