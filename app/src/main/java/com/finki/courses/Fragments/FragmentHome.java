package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentHomeBinding;


public class FragmentHome extends Fragment implements IEssentials {

    private FragmentHomeBinding binding;
    private Toaster toaster;

    public FragmentHome() {
        // Required empty public constructor
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
        toaster = new Toaster(getContext());
    }

    @Override
    public void addEventListeners() {
        binding.floatingActionButtonAddCategory.setOnClickListener(view -> {
            toaster.text("Not ready yet");
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}







