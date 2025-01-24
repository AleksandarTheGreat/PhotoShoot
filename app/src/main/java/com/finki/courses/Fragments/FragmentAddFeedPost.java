package com.finki.courses.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Activities.MainActivity;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.FeedPost;
import com.finki.courses.R;
import com.finki.courses.Repositories.IFeedPostRepository;
import com.finki.courses.Repositories.Implementations.FeedPostRepository;
import com.finki.courses.databinding.FragmentAddFeedPostBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class FragmentAddFeedPost extends Fragment implements IEssentials {

    private FragmentAddFeedPostBinding binding;
    private final int REQ_CODE = 4;
    private Toaster toaster;
    private MainActivityHelper mainActivityHelper;
    private FeedPostRepository feedPostRepository;
    private FirebaseAuth firebaseAuth;

    public FragmentAddFeedPost() {
        // Required empty public constructor
    }

    public FragmentAddFeedPost(MainActivityHelper mainActivityHelper){
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddFeedPostBinding.bind(inflater.inflate(R.layout.fragment_add_feed_post, container, false));

        instantiateObjects();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        toaster = new Toaster(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        feedPostRepository = new FeedPostRepository(getContext(), mainActivityHelper);
    }

    @Override
    public void addEventListeners() {
        binding.imageViewAdd.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQ_CODE);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK && data != null){
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(binding.imageViewFeedPost);
            hideAddIcon();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Upload image")
                    .setMessage("This image will be uploaded to the feed")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = data.getData();

                            // Right here the post url is local before uploading
                            // then I will update the url to the uploaded location

                            String email = firebaseAuth.getCurrentUser().getEmail();
                            FeedPost feedPost = new FeedPost(email, uri.toString());

                            feedPostRepository.uploadPostToStorage(feedPost);
                            Log.d("Tag", "Picked image uri is:\n" + uri.toString());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toaster.text("That's what I thought");
                            mainActivityHelper.changeFragments(new FragmentFeed(mainActivityHelper), false);
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();

        }
    }

    public void showAddIcon(){
        binding.imageViewAdd.setVisibility(View.VISIBLE);
    }

    public void hideAddIcon(){
        binding.imageViewAdd.setVisibility(View.INVISIBLE);
    }
}