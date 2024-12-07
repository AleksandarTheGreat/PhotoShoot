package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.AuthenticationRepository;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.databinding.FragmentUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FragmentUser extends Fragment implements IEssentials {

    private FragmentUserBinding binding;
    private Toaster toaster;
    private MainActivityHelper mainActivityHelper;
    private FirebaseAuth firebaseAuth;
    private AuthenticationRepository authenticationRepository;

    private FragmentUserHelper fragmentUserHelper;
    private PostRepository postRepository;

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

        fragmentUserHelper = new FragmentUserHelper(getContext(), binding);

        postRepository = new PostRepository(getContext());
        postRepository.listAllForUser(fragmentUserHelper);
    }

    @Override
    public void addEventListeners() {
        binding.buttonSignOut.setOnClickListener(view -> {
            authenticationRepository.signOutUser();
        });

        binding.buttonICanFixThat.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}








