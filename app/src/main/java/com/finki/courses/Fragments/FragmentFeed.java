package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentFeedBinding;

public class FragmentFeed extends Fragment implements IEssentials {

    private FragmentFeedBinding binding;
    private MainActivityHelper mainActivityHelper;

    public FragmentFeed() {
        // Required empty public constructor
    }

    public FragmentFeed(MainActivityHelper mainActivityHelper){
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.bind(inflater.inflate(R.layout.fragment_feed, container, false));

        instantiateObjects();
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