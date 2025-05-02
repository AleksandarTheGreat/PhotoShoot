package com.finki.courses.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.bumptech.glide.Glide;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentImageSliderBinding;
import com.finki.courses.databinding.FragmentSingleImageBinding;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class FragmentSingleImage extends Fragment implements IEssentials {

    private FragmentSingleImageBinding binding;
    private FragmentImageSliderBinding fragmentImageSliderBinding;
    private Map<String, Object> postMap;
    private Toaster toaster;

    public FragmentSingleImage() {

    }

    public FragmentSingleImage(Map<String, Object> postMap, FragmentImageSliderBinding fragmentImageSliderBinding) {
        this.postMap = postMap;
        this.fragmentImageSliderBinding = fragmentImageSliderBinding;
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
        toaster = new Toaster(getContext());

        String imageUri = (String) postMap.get("imageUrl");
        Glide.with(getContext())
                .load(imageUri)
                .into(binding.photoViewFragmentSingleImage);

        Log.d("Tag", imageUri);
    }

    @SuppressLint("NewApi")
    @Override
    public void addEventListeners() {

    }

    @Override
    public void additionalThemeChanges() {

    }
}