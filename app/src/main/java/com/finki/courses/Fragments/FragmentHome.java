package com.finki.courses.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentHomeHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.Repositories.Implementations.UserRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FragmentHome extends Fragment implements IEssentials {

    private FragmentHomeBinding binding;
    private Toaster toaster;
    private FragmentHomeHelper fragmentHomeHelper;
    private MainActivityHelper mainActivityHelper;
    private CategoryRepository categoryRepository;
    private boolean isNightModeOn;
    private UserRepository userRepository;

    public FragmentHome() {
        // Required empty public constructor
    }

    public FragmentHome(MainActivityHelper mainActivityHelper){
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
        fragmentHomeHelper = new FragmentHomeHelper(getContext(), binding, mainActivityHelper);

        categoryRepository = new CategoryRepository(getContext(), binding, fragmentHomeHelper);
        categoryRepository.listAll();

        toaster = new Toaster(getContext());
        userRepository = new UserRepository(getContext());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String profilePictureUrl = userRepository.loadProfilePictureFromCache(email);
        if (!profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(binding.imageViewProfileFragmentHome);
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
        if (isNightModeOn){
            binding.imageViewLogo.setImageResource(R.drawable.ic_logo_finki_white);
            binding.textViewTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            binding.textViewEmptyList.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            binding.imageViewLogo.setImageResource(R.drawable.ic_logo_finki_black);
            binding.textViewTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            binding.textViewEmptyList.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }
}







