package com.finki.courses.Repositories.Implementations;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.finki.courses.Fragments.FragmentAddPost;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.IPostRepository;
import com.finki.courses.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PostRepository implements IPostRepository {

    private Context context;
    private Toaster toaster;
    private final String email;
    private final StorageReference storageReference;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final FragmentAddPostBinding fragmentAddPostBinding;

    public PostRepository(Context context, FragmentAddPostBinding fragmentAddPostBinding, StorageReference storageReference) {
        this.context = context;
        this.fragmentAddPostBinding = fragmentAddPostBinding;
        this.storageReference = storageReference;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.toaster = new Toaster(context);
    }

    @Override
    public void add(String categoryName, Post post) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                        boolean check = false;
                        for (Map<String, Object> map1: listOfCategories){
                            String name = (String) map1.get("name");
                            List<Post> postList = (List<Post>) map1.get("postList");

                            if (name.equalsIgnoreCase(categoryName)){
                                postList.add(post);
                                check = true;
                                break;
                            }
                        }

                        if (check){
                            map.put("categoryList", listOfCategories);
                            documentReference.update("user", map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            toaster.text("Added new post");
                                            fragmentAddPostBinding.imageViewAdd.setVisibility(View.VISIBLE);
                                            fragmentAddPostBinding.imageViewPickedImage.setImageResource(0);
                                            FragmentAddPost.clearImageUri();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", e.getLocalizedMessage());
                                            fragmentAddPostBinding.imageViewAdd.setVisibility(View.VISIBLE);
                                            fragmentAddPostBinding.imageViewPickedImage.setImageResource(0);
                                            FragmentAddPost.clearImageUri();
                                        }
                                    });
                        } else {
                            toaster.text("Category not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void delete() {

    }

    @Override
    public void uploadImage(String categoryName, InputStream inputStream, String filePath) {
        StorageReference imageRef = storageReference.child(filePath);

        UploadTask uploadTask = (UploadTask) imageRef.putStream(inputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUriToString = String.valueOf(uri);

                                        Post post = new Post(filePath, imageUriToString);
                                        add(categoryName, post);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        toaster.text(e.getMessage());
                                        Log.d("Tag", e.getLocalizedMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                    }
                });
    }
}
