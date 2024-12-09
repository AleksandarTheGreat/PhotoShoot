package com.finki.courses.Repositories.Implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SymbolTable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.finki.courses.Fragments.FragmentUser;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Repositories.IUserRepository;
import com.finki.courses.databinding.FragmentUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.Map;

public class UserRepository implements IUserRepository {

    private static final String COLLECTION_NAME = "Users";
    private static final String PROFILE_SHARED_PREF = "PROFILE_SHARED_PREF";
    private Context context;
    private Toaster toaster;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FragmentUserBinding fragmentUserBinding;

    // Used in FragmentHome for the profile picture
    public UserRepository(Context context){
        this.context = context;
    }

    // Used in the FragmentUser as it should be
    public UserRepository(Context context, FragmentUserBinding fragmentUserBinding) {
        this.context = context;
        this.toaster = new Toaster(context);
        this.fragmentUserBinding = fragmentUserBinding;

        this.firebaseStorage = FirebaseStorage.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loadProfilePictureFromFirebase() {
        String email = firebaseAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");

                        String imageUrl = String.valueOf(map.get("imageUrl"));
                        if (imageUrl.isEmpty()) return;

                        Picasso.get().load(imageUrl).into(fragmentUserBinding.imageViewUserPicture);
                        // IF the picture has not been added to cache when uploaded
                        // Some sort of a safety precaution
                        saveProfilePictureToCache(imageUrl, email);
                        Log.d("Tag", "Loaded profile picture from firebase");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void uploadUserPictureToStorage(InputStream inputStream) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading profile picture");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String email = firebaseAuth.getCurrentUser().getEmail();

        String fileName = System.currentTimeMillis() + ".jpg";

        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageFile = storageReference.child("/" + email + "/profilePictures/" + fileName);

        UploadTask uploadTask = (UploadTask) imageFile.putStream(inputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getTask().isSuccessful()) {
                            imageFile.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            addUserPictureToDocument(email, String.valueOf(uri));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toaster.text(e.getLocalizedMessage());
                                            progressDialog.dismiss();
                                            fragmentUserBinding.imageViewUserPicture.setImageResource(0);
                                            FragmentUser.clearUserImageUri();
                                        }
                                    });
                        } else {
                            toaster.text("Failed to upload image");
                            progressDialog.dismiss();
                            fragmentUserBinding.imageViewUserPicture.setImageResource(0);
                            FragmentUser.clearUserImageUri();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        progressDialog.dismiss();
                        fragmentUserBinding.imageViewUserPicture.setImageResource(0);
                        FragmentUser.clearUserImageUri();
                    }
                });

    }

    @Override
    public void addUserPictureToDocument(String email, String profilePictureUri) {
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> userMap = (Map<String, Object>) documentSnapshot.get("user");

                        userMap.put("imageUrl", profilePictureUri);
                        documentReference.update("user", userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        toaster.text("Uploaded profile picture successfully");
                                        progressDialog.dismiss();
                                        FragmentUser.clearUserImageUri();

                                        // Add to cache always on upload
                                        Log.d("Tag", "Saved profile picture to firebase");
                                        saveProfilePictureToCache(profilePictureUri, email);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        toaster.text(e.getLocalizedMessage());
                                        Log.d("Tag", e.getLocalizedMessage());
                                        progressDialog.dismiss();
                                        fragmentUserBinding.imageViewUserPicture.setImageResource(0);
                                        FragmentUser.clearUserImageUri();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        progressDialog.dismiss();
                        fragmentUserBinding.imageViewUserPicture.setImageResource(0);
                        FragmentUser.clearUserImageUri();
                    }
                });
    }

    @Override
    public String loadProfilePictureFromCache(String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageUrl", "");

        Log.d("Tag", "User image loaded from shared pref '" + sharedPrefName + "'");
        return imageUrl;
    }

    @Override
    public void saveProfilePictureToCache(String imageUrl, String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("imageUrl", imageUrl);
        editor.apply();

        Log.d("Tag", "User image saved to shared pref '" + sharedPrefName + "'");
    }

    @Override
    public boolean profileCacheIsEmpty(String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("imageUrl", "");

        return imageUrl.isEmpty();
    }

}












