package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentUserBinding;


public class FragmentUser extends Fragment implements IEssentials {

    private FragmentUserBinding binding;

    public FragmentUser() {}

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

    }

    @Override
    public void addEventListeners() {

    }

    @Override
    public void additionalThemeChanges() {

    }
}