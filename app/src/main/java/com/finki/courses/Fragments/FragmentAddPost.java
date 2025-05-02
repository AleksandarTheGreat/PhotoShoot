package com.finki.courses.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.Post.OnPostAddedCallback;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.Utils.ThemeUtils;
import com.finki.courses.ViewModel.ViewModelCategories;
import com.finki.courses.ViewModel.ViewModelPosts;
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
import java.util.List;


public class FragmentAddPost extends Fragment implements IEssentials {

    private FragmentAddPostBinding binding;
    private Toaster toaster;
    private static final int REQ_CODE_GALLERY = 1;
    private Category category;
    private PostRepository postRepository;
    private MainActivityHelper mainActivityHelper;

    private ViewModelPosts viewModelPosts;
    private ViewModelCategories viewModelCategories;


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

        viewModelCategories = new ViewModelProvider(requireActivity()).get(ViewModelCategories.class);
        viewModelPosts = new ViewModelProvider(requireActivity()).get(ViewModelPosts.class);
    }

    @Override
    public void addEventListeners() {
        binding.imageViewAdd.setOnClickListener(view -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
            // Multiple files selected
            if (data.getClipData() != null){
                binding.imageViewAdd.setVisibility(View.INVISIBLE);

                Picasso.get().load(data.getClipData().getItemAt(0).getUri()).into(binding.imageViewPickedImage);
                int size = data.getClipData().getItemCount();

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Upload")
                        .setMessage("To upload all " + size + " pictures click yes")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i=0;i<data.getClipData().getItemCount();i++){
                                    Uri uri = data.getClipData().getItemAt(i).getUri();

                                    uploadImageToFirebaseStorage(uri);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
            // Single file selected
            else if (data.getData() != null){
                binding.imageViewAdd.setVisibility(View.INVISIBLE);

                Uri pickedImageUri = data.getData();
                Picasso.get().load(data.getData()).into(binding.imageViewPickedImage);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Upload")
                        .setMessage("To upload a picture click yes")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImageToFirebaseStorage(pickedImageUri);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void uploadImageToFirebaseStorage(Uri pickedImageUri) {
        if (pickedImageUri == null) {
            toaster.text("Please pick an image first");
            return;
        }

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(pickedImageUri);
            if (inputStream != null) {

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Adding post...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                viewModelPosts.uploadImage(category, inputStream, new OnPostAddedCallback() {
                    @Override
                    public void onPostAdded(long categoryId, Post post, boolean addedSuccessfully) {
                        if (addedSuccessfully){
                            // somehow wait for all the images to be uploaded before going to fragment home,
                            // but that is not the major task right now.

                            toaster.text("Added new post");

                            binding.imageViewAdd.setVisibility(View.VISIBLE);
                            binding.imageViewPickedImage.setImageResource(0);

                            // this is not an optimal solution, I need to change this.
                            viewModelCategories.listAll(categories -> viewModelCategories.getMutableLiveDataCategories().setValue(categories));
                            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);

                            // Don't forget to call categories - list all method

                        } else {
                            binding.imageViewAdd.setVisibility(View.VISIBLE);
                            binding.imageViewPickedImage.setImageResource(0);
                        }

                        progressDialog.dismiss();
                    }
                });
            } else {
                toaster.text("Somehow the inputStream is null");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}




