package com.finki.courses.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.databinding.FragmentAddPostBinding;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;


public class FragmentAddPost extends Fragment implements IEssentials {

    private FragmentAddPostBinding binding;
    private Toaster toaster;
    private static final int REQ_CODE_GALLERY = 1;
    private Uri pickedImageUri;
    private String categoryName;
    private PostRepository postRepository;

    public FragmentAddPost() {
        // Required empty public constructor
    }

    public FragmentAddPost(String categoryName){
        this.categoryName = categoryName;
        Log.d("Tag", "Category is '" + categoryName + "'");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddPostBinding.bind(inflater.inflate(R.layout.fragment_add_post, container, false));

        instantiateObjects();
        additionalThemeChanges();

        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        postRepository = new PostRepository(getContext());

        toaster = new Toaster(getContext());
    }

    @Override
    public void addEventListeners() {
        binding.imageViewAdd.setOnClickListener(view -> {
            toaster.text("Okay, I've done this");
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, REQ_CODE_GALLERY);
        });

        binding.buttonSave.setOnClickListener(view -> {
            binding.imageViewAdd.setVisibility(View.VISIBLE);
            binding.imageViewPickedImage.setImageResource(0);
            pickedImageUri = null;

            // First save the image to firebase storage
            // then after it is saved call postRepository.add(category, post);
            postRepository.add(categoryName, new Post());
        });
    }

    @Override
    public void additionalThemeChanges() {
        if (ThemeUtils.isNightModeOn(getContext())){
            binding.imageViewAdd.setImageResource(R.drawable.ic_add_night);
        } else {
            binding.imageViewAdd.setImageResource(R.drawable.ic_add_day);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null){
            binding.imageViewAdd.setVisibility(View.INVISIBLE);

            pickedImageUri = data.getData();
            Picasso.get().load(data.getData()).into(binding.imageViewPickedImage);
        }
    }
}