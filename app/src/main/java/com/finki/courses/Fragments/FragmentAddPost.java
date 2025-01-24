package com.finki.courses.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;


public class FragmentAddPost extends Fragment implements IEssentials {

    private FragmentAddPostBinding binding;
    private Toaster toaster;
    private static final int REQ_CODE_GALLERY = 1;
    private static Uri pickedImageUri;
    private Category category;
    private PostRepository postRepository;
    private MainActivityHelper mainActivityHelper;

    public FragmentAddPost() {
        // Required empty public constructor
    }

    public FragmentAddPost(Category category, MainActivityHelper mainActivityHelper) {
        this.category = category;
        this.mainActivityHelper = mainActivityHelper;
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
        postRepository = new PostRepository(getContext(), binding, mainActivityHelper);
        toaster = new Toaster(getContext());
    }

    @Override
    public void addEventListeners() {
        binding.imageViewAdd.setOnClickListener(view -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, REQ_CODE_GALLERY);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            binding.imageViewAdd.setVisibility(View.INVISIBLE);

            pickedImageUri = data.getData();
            Picasso.get().load(data.getData()).into(binding.imageViewPickedImage);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Upload")
                    .setMessage("To upload a picture click yes")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadImageToFirebaseStorage();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearImageUri();
                            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearImageUri();
    }

    public static void clearImageUri() {
        pickedImageUri = null;
    }

    private void uploadImageToFirebaseStorage() {
        if (pickedImageUri == null) {
            toaster.text("Please pick an image first");
            return;
        }

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(pickedImageUri);
            if (inputStream != null) {
                postRepository.uploadImage(category, inputStream);
            } else {
                toaster.text("Somehow the inputStream is null");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}




