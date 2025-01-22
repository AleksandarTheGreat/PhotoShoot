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
    private Context context;
    private Toaster toaster;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialogProfilePicture;
    private ProgressDialog progressDialogCoverPicture;
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

    /**
     * Profile picture CRUD
     * or parts of it are done here
     */

    @Override
    public void loadProfilePictureFromFirebase() {
        String email = firebaseAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");

                        String imageUrl = String.valueOf(map.get("profilePhotoUrl"));
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
        progressDialogProfilePicture = new ProgressDialog(context);
        progressDialogProfilePicture.setTitle("Uploading profile picture");
        progressDialogProfilePicture.setCancelable(false);
        progressDialogProfilePicture.show();

        String email = firebaseAuth.getCurrentUser().getEmail();

        String imageName = System.currentTimeMillis() + ".jpg";
        String fileName = "/" + email + "/profilePictures/" + imageName;

        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageFile = storageReference.child(fileName);


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
                                            clearForProfilePicture();
                                        }
                                    });
                        } else {
                            toaster.text("Failed to upload image");
                            clearForProfilePicture();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        clearForProfilePicture();
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

                        userMap.put("profilePhotoUrl", profilePictureUri);
                        documentReference.update("user", userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        toaster.text("Uploaded profile picture successfully");
                                        progressDialogProfilePicture.dismiss();
                                        FragmentUser.clearProfilePictureUrl();

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
                                        clearForProfilePicture();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        clearForProfilePicture();
                    }
                });
    }

    @Override
    public String loadProfilePictureFromCache(String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("profilePhotoUrl", "");

        Log.d("Tag", "User image loaded from shared pref '" + sharedPrefName + "'");
        return imageUrl;
    }

    @Override
    public void saveProfilePictureToCache(String imageUrl, String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("profilePhotoUrl", imageUrl);
        editor.apply();

        Log.d("Tag", "User image saved to shared pref '" + sharedPrefName + "'");
    }

    @Override
    public boolean profileCacheIsEmpty(String sharedPrefName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString("profilePhotoUrl", "");

        return imageUrl.isEmpty();
    }


    /**
     * Cover Photo CRUD
     * or parts of it are done here
     */

    @Override
    public void loadCoverPhotoFromFirebase() {
        String email = firebaseAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> userMap = (Map<String, Object>) documentSnapshot.get("user");
                        String coverPhotoUrl = String.valueOf(userMap.get("coverPhotoUrl"));

                        if (coverPhotoUrl.isEmpty()) return;

                        Picasso.get().load(coverPhotoUrl).into(fragmentUserBinding.imageViewCoverPhoto);
                        Log.d("Tag", "Loaded cover picture from firebase");
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
    public void uploadAndAddCoverPhotoToDocument(String email, InputStream inputStream) {
        progressDialogCoverPicture = new ProgressDialog(context);
        progressDialogCoverPicture.setTitle("Uploading cover picture");
        progressDialogCoverPicture.setCancelable(false);
        progressDialogCoverPicture.show();

        String imageName = System.currentTimeMillis() + ".jpg";
        String fileName = "/" + email + "/coverPictures/" + imageName;

        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imageRef = storageReference.child(fileName);


        UploadTask uploadTask = (UploadTask) imageRef.putStream(inputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getTask().isSuccessful()){
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String coverImageUri = String.valueOf(uri);

                                            // SECOND PART
                                            // ADDITION TO DOCUMENT
                                            // STARTS HERE

                                            DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
                                            documentReference.get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Map<String, Object> userMap = (Map<String, Object>) documentSnapshot.get("user");
                                                            userMap.put("coverPhotoUrl", coverImageUri);

                                                            documentReference.update("user", userMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            toaster.text("Uploaded cover picture successfully");
                                                                            progressDialogCoverPicture.dismiss();
                                                                            FragmentUser.clearCoverPictureUrl();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("Tag", e.getLocalizedMessage());
                                                                            toaster.text(e.getLocalizedMessage());
                                                                            clearForCoverPicture();
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Tag", e.getLocalizedMessage());
                                                            toaster.text(e.getLocalizedMessage());
                                                            clearForCoverPicture();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toaster.text(e.getLocalizedMessage());
                                            Log.d("Tag", e.getLocalizedMessage());
                                            clearForCoverPicture();
                                        }
                                    });

                        } else {
                            toaster.text("Failed to upload cover image");
                            clearForCoverPicture();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        clearForCoverPicture();
                    }
                });
    }


    private void clearForProfilePicture(){
        progressDialogProfilePicture.dismiss();
        fragmentUserBinding.imageViewUserPicture.setImageResource(0);
        FragmentUser.clearProfilePictureUrl();
    }

    private void clearForCoverPicture(){
        progressDialogCoverPicture.dismiss();
        FragmentUser.clearCoverPictureUrl();
        fragmentUserBinding.imageViewCoverPhoto.setImageResource(0);
    }

}












