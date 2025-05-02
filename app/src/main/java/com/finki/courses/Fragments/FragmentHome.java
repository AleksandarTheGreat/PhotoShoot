package com.finki.courses.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentHomeHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.Repositories.Implementations.UserRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.ViewModel.ViewModelCategories;
import com.finki.courses.ViewModel.ViewModelPosts;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FragmentHome extends Fragment implements IEssentials {

    private FragmentHomeBinding binding;
    private Toaster toaster;
    private FragmentHomeHelper fragmentHomeHelper;
    private MainActivityHelper mainActivityHelper;
    private CategoryRepository categoryRepository;
    private boolean isNightModeOn;
    private UserRepository userRepository;

    private ViewModelCategories viewModelCategories;
    private ViewModelPosts viewModelPosts;

    public FragmentHome() {
        // Required empty public constructor
    }

    public FragmentHome(MainActivityHelper mainActivityHelper) {
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.bind(inflater.inflate(R.layout.fragment_home, container, false));

        instantiateObjects();
        additionalThemeChanges();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        isNightModeOn = ThemeUtils.isNightModeOn(getContext());

        viewModelPosts = new ViewModelProvider(requireActivity()).get(ViewModelPosts.class);
        viewModelCategories = new ViewModelProvider(requireActivity()).get(ViewModelCategories.class);
        fragmentHomeHelper = new FragmentHomeHelper(getContext(), binding, viewModelCategories, viewModelPosts, mainActivityHelper);

        viewModelCategories.getMutableLiveDataCategories().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                binding.linearLayoutCategories.removeAllViews();
                if (!categories.isEmpty()) {

                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    fragmentHomeHelper.showScrollViewAndHideLinearLayout();

                    for (Category category : categories) {
                        fragmentHomeHelper.buildUILayoutForCategory(category);
                    }

                    progressDialog.cancel();

                    toaster.text("Built the UI, not from firebase");
                    Log.d("Tag", "All categories are : " + categories);

                } else {
                    fragmentHomeHelper.hideScrollViewAndShowLinearLayout();
                }
            }
        });

        toaster = new Toaster(getContext());
        userRepository = new UserRepository(getContext());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String profilePictureUrl = userRepository.loadProfilePictureFromCache(email);
        if (!profilePictureUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(profilePictureUrl)
                    .into(binding.imageViewProfileFragmentHome);
        }
    }

    @Override
    public void addEventListeners() {
        binding.floatingActionButtonAddCategory.setOnClickListener(view -> {
            fragmentHomeHelper.showCategoryInputDialog();
        });

        binding.buttonAddCategory.setOnClickListener(view -> {
            fragmentHomeHelper.showCategoryInputDialog();
        });

        binding.imageViewProfileFragmentHome.setOnClickListener(view -> {
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemUser);
            mainActivityHelper.changeFragments(new FragmentUser(mainActivityHelper), false);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}







