package com.finki.courses.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.AuthenticationRepository;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.Repositories.Implementations.UserRepository;
import com.finki.courses.databinding.FragmentUserBinding;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import kotlin.Result;


public class FragmentUser extends Fragment implements IEssentials {

    private FragmentUserBinding binding;
    private Toaster toaster;
    private MainActivityHelper mainActivityHelper;
    private FirebaseAuth firebaseAuth;
    private AuthenticationRepository authenticationRepository;

    private FragmentUserHelper fragmentUserHelper;
    private PostRepository postRepository;

    private static int PROFILE_PIC_REQ_CODE = 2;
    private static int COVER_PIC_REQ_CODE = 3;
    private static Uri profilePhotoUrl;
    private static Uri coverPhotoUrl;
    private UserRepository userRepository;


    public FragmentUser() {
    }

    public FragmentUser(MainActivityHelper mainActivityHelper) {
        this.mainActivityHelper = mainActivityHelper;
    }

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
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        binding.textViewEmail.setText(user != null ? user.getEmail() : "");

        authenticationRepository = new AuthenticationRepository(getContext());
        toaster = new Toaster(getContext());

        fragmentUserHelper = new FragmentUserHelper(getContext(), binding);

        postRepository = new PostRepository(getContext(), mainActivityHelper);
        postRepository.listAllForUser(fragmentUserHelper);

        String email = firebaseAuth.getCurrentUser().getEmail();
        userRepository = new UserRepository(getContext(), binding);
        userRepository.loadCoverPhotoFromFirebase();

        if (userRepository.profileCacheIsEmpty(email)) {
            userRepository.loadProfilePictureFromFirebase();
        } else {
            String profileUrl = userRepository.loadProfilePictureFromCache(email);
            Picasso.get().load(profileUrl).into(binding.imageViewUserPicture);
        }
    }

    @Override
    public void addEventListeners() {
        binding.imageViewSignOut.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            authenticationRepository.signOutUser();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toaster.text("That's what I thought");
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();

        });

        binding.buttonICanFixThat.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemHome);
        });


        binding.imageViewUserPicture.setOnClickListener(view -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK);
            openGallery.setType("image/*");
            startActivityForResult(openGallery, PROFILE_PIC_REQ_CODE);
        });

        binding.imageViewCoverPhoto.setOnClickListener(view -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK);
            openGallery.setType("image/*");
            startActivityForResult(openGallery, COVER_PIC_REQ_CODE);
        });

    }

    @Override
    public void additionalThemeChanges() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_PIC_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            profilePhotoUrl = data.getData();
            Picasso.get().load(profilePhotoUrl).into(binding.imageViewUserPicture);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Change profile picture")
                    .setMessage("Are you sure you want to change your profile picture?")
                    .setPositiveButton("Yes, I am", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveProfilePicture();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userRepository.loadProfilePictureFromFirebase();
                            clearProfilePictureUrl();
                            toaster.text("That's what I thought");
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        if (requestCode == COVER_PIC_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            coverPhotoUrl = data.getData();
            Picasso.get().load(coverPhotoUrl).into(binding.imageViewCoverPhoto);
            toaster.text("Successfully pulled cover uri: '" + coverPhotoUrl.toString() + "'");

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Change cover photo")
                    .setMessage("Are you sure you want to change your cover picture?")
                    .setPositiveButton("Yes, I am", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveCoverPicture();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userRepository.loadCoverPhotoFromFirebase();
                            clearCoverPictureUrl();
                            toaster.text("That's what I thought");
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearProfilePictureUrl();
        clearCoverPictureUrl();
    }

    public static void clearProfilePictureUrl() {
        profilePhotoUrl = null;
    }

    public static void clearCoverPictureUrl() {
        coverPhotoUrl = null;
    }

    private void saveProfilePicture(){
        if (profilePhotoUrl == null) {
            toaster.text("Pick a profile image first");
            return;
        }

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(profilePhotoUrl);
            if (inputStream != null) {
                userRepository.uploadUserPictureToStorage(inputStream);
            } else {
                toaster.text("Failed to open inputStream");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCoverPicture(){
        if (coverPhotoUrl == null) {
            toaster.text("Pick a cover photo first");
            return;
        }

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(coverPhotoUrl);
            if (inputStream != null) {
                String email = firebaseAuth.getCurrentUser().getEmail();
                userRepository.uploadAndAddCoverPhotoToDocument(email, inputStream);
            } else {
                toaster.text("Failed to open inputStream");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}








