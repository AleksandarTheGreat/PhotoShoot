package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentGalleryBinding;


public class FragmentGallery extends Fragment implements IEssentials {

    private FragmentGalleryBinding binding;
    private Toaster toaster;

    public FragmentGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.bind(inflater.inflate(R.layout.fragment_gallery, container, false));

        instantiateObjects();
        additionalThemeChanges();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        toaster = new Toaster(getContext());
        toaster.text("New gallery");
    }

    @Override
    public void addEventListeners() {
    }

    @Override
    public void additionalThemeChanges() {

    }
}