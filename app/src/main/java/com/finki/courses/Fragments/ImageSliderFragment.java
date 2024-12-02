package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentImageSliderBinding;

import java.util.List;

public class ImageSliderFragment extends Fragment implements IEssentials {

    private FragmentImageSliderBinding binding;
    private List<Post> postList;

    public ImageSliderFragment(){

    }

    public ImageSliderFragment(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageSliderBinding.bind(inflater.inflate(R.layout.fragment_image_slider, container, false));

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