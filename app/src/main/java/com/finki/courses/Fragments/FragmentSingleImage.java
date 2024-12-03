package com.finki.courses.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentSingleImageBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class FragmentSingleImage extends Fragment implements IEssentials {

    private FragmentSingleImageBinding binding;
    private Map<String, Object> postMap;

    public FragmentSingleImage(){

    }

    public FragmentSingleImage(Map<String, Object> postMap) {
        this.postMap = postMap;
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
        String imageUri = (String) postMap.get("imageUrl");
        Uri uri = Uri.parse(imageUri);
        Picasso.get()
                .load(uri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(binding.touchImageViewFragmentSingleImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(uri).into(binding.touchImageViewFragmentSingleImage);
            }
        });
        Log.d("Tag", imageUri);
    }

    @Override
    public void addEventListeners() {

    }

    @Override
    public void additionalThemeChanges() {

    }
}