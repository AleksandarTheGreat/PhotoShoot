package com.finki.courses.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

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
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class FragmentHome extends Fragment implements IEssentials {

    private FragmentHomeBinding binding;
    private Toaster toaster;
    private FragmentHomeHelper fragmentHomeHelper;
    private MainActivityHelper mainActivityHelper;
    private CategoryRepository categoryRepository;

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
        fragmentHomeHelper = new FragmentHomeHelper(getContext(), binding, mainActivityHelper, toaster);

        categoryRepository = new CategoryRepository(getContext(), binding, fragmentHomeHelper);
        categoryRepository.listAll();

        toaster = new Toaster(getContext());
        toaster.text("Called again");
    }

    @Override
    public void addEventListeners() {
        binding.floatingActionButtonAddCategory.setOnClickListener(view -> {
            fragmentHomeHelper.showCategoryInputDialog();
        });

        binding.searchBarFragmentHome.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    fragmentHomeHelper.hideLogoAndTitle();
                } else {
                    fragmentHomeHelper.showLogoAndTitle();
                }
            }
        });

        binding.buttonAddCategory.setOnClickListener(view -> {
            fragmentHomeHelper.showCategoryInputDialog();
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}







