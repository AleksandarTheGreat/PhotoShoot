package com.finki.courses.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Activities.LoginActivity;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.Repositories.AuthenticationRepository;
import com.finki.courses.databinding.FragmentUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FragmentUser extends Fragment implements IEssentials {

    private FragmentUserBinding binding;
    private Toaster toaster;
    private MainActivityHelper mainActivityHelper;
    private FirebaseAuth firebaseAuth;
    private AuthenticationRepository authenticationRepository;

    public FragmentUser() {}
    public FragmentUser(MainActivityHelper mainActivityHelper){
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.bind(inflater.inflate(R.layout.fragment_user, container, false));

        instantiateObjects();
        additionalThemeChanges();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        binding.textViewEmail.setText(user != null? user.getEmail(): "");

        authenticationRepository = new AuthenticationRepository(getContext());
        toaster = new Toaster(getContext());
    }

    @Override
    public void addEventListeners() {
        binding.buttonViewAll.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentGallery());
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemGallery);
        });

        binding.buttonSignOut.setOnClickListener(view -> {
            authenticationRepository.signOutUser();
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}