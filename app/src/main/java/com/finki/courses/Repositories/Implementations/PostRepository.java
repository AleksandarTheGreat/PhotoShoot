package com.finki.courses.Repositories.Implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentAddPost;
import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
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
import java.util.UUID;

public class PostRepository implements IPostRepository {

    private Context context;
    private Toaster toaster;
    private final String email;
    private final StorageReference storageReference;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final FragmentAddPostBinding fragmentAddPostBinding;
    private final MainActivityHelper mainActivityHelper;
    private final ProgressDialog progressDialog;

    public PostRepository(Context context, FragmentAddPostBinding fragmentAddPostBinding,
                          MainActivityHelper mainActivityHelper, StorageReference storageReference) {
        this.context = context;
        this.mainActivityHelper = mainActivityHelper;
        this.fragmentAddPostBinding = fragmentAddPostBinding;
        this.storageReference = storageReference;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.toaster = new Toaster(context);
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    public void add(long categoryId, Post post) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // If I can add a post, the document must exist first
                        // there fore this cannot be null nor empty ?

                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                        boolean check = false;
                        for (Map<String, Object> map1: listOfCategories){
                            long cid = Long.parseLong(String.valueOf(map1.get("id")));
                            List<Post> postList = (List<Post>) map1.get("postList");

                            if (cid == categoryId){
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
                                            progressDialog.dismiss();

                                            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
                                            // redirect:/home fragment
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", e.getLocalizedMessage());
                                            toaster.text(e.getLocalizedMessage());
                                            fragmentAddPostBinding.imageViewAdd.setVisibility(View.VISIBLE);
                                            fragmentAddPostBinding.imageViewPickedImage.setImageResource(0);
                                            FragmentAddPost.clearImageUri();
                                            progressDialog.dismiss();
                                        }
                                    });
                        } else {
                            toaster.text("Category not found");
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        toaster.text(e.getLocalizedMessage());
                        fragmentAddPostBinding.imageViewAdd.setVisibility(View.VISIBLE);
                        fragmentAddPostBinding.imageViewPickedImage.setImageResource(0);
                        FragmentAddPost.clearImageUri();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void deleteById(long categoryId, long postId) {

    }


    @Override
    public void uploadImage(Category category, InputStream inputStream) {
        // File location for the image should be
        // /email/category/imageName.jpg

        String email = firebaseAuth.getCurrentUser().getEmail();
        String categoryName = category.getName();
        String fileName = "/" + email + "/" + categoryName + "/" + System.currentTimeMillis() + ".jpg";

        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference imageRef = storageReference.child(fileName);

        UploadTask uploadTask = (UploadTask) imageRef.putStream(inputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUriToString = String.valueOf(uri);

                                        long postId = UUID.randomUUID().getLeastSignificantBits() * -1;
                                        Post post = new Post(postId, fileName, imageUriToString);
                                        add(category.getId(), post);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        toaster.text(e.getMessage());
                                        Log.d("Tag", e.getLocalizedMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                });
    }
}
