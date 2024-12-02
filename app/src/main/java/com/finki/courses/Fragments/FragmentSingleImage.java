package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentSingleImageBinding;


public class FragmentSingleImage extends Fragment implements IEssentials {

    private FragmentSingleImageBinding binding;
    private Post post;

    public FragmentSingleImage(){

    }

    public FragmentSingleImage(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleImageBinding.bind(inflater.inflate(R.layout.fragment_single_image, container, false));

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