package com.finki.courses.Repositories.Implementations;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentAddPost;
import com.finki.courses.Fragments.FragmentHelpers.FragmentGalleryHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentHomeHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.Callbacks.Post.OnPostAddedCallback;
import com.finki.courses.Repositories.Callbacks.Post.OnPostDeletedCallback;
import com.finki.courses.Repositories.IPostRepository;
import com.finki.courses.databinding.FragmentAddPostBinding;
import com.finki.courses.databinding.FragmentGalleryBinding;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.finki.courses.databinding.FragmentUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostRepository implements IPostRepository {

    private static final String COLLECTION_NAME = "Users";
    private Context context;
    private Toaster toaster;
    private String email;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FragmentAddPostBinding fragmentAddPostBinding;
    private MainActivityHelper mainActivityHelper;


    // FragmentHome usage
    public PostRepository(Context context, MainActivityHelper mainActivityHelper) {
        this.context = context;
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.email = firebaseAuth.getCurrentUser().getEmail();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.toaster = new Toaster(context);
        this.mainActivityHelper = mainActivityHelper;
    }

    // FragmentAddPost usage
    public PostRepository(Context context, FragmentAddPostBinding fragmentAddPostBinding, MainActivityHelper mainActivityHelper) {
        this.context = context;
        this.mainActivityHelper = mainActivityHelper;
        this.fragmentAddPostBinding = fragmentAddPostBinding;
        this.storageReference = FirebaseStorage.getInstance().getReference();

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.toaster = new Toaster(context);
    }

    // FragmentGallery usage
    public PostRepository(Context context){
        this.context = context;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.toaster = new Toaster(context);
    }

    @Override
    public void add(long categoryId, Post post, OnPostAddedCallback onPostAddedCallback) {
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // If I can add a post, the document must exist first
                        // therefore this cannot be null nor empty ?

                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                        boolean check = false;
                        for (Map<String, Object> map1 : listOfCategories) {
                            long cid = Long.parseLong(String.valueOf(map1.get("id")));
                            List<Post> postList = (List<Post>) map1.get("postList");

                            if (cid == categoryId) {
                                postList.add(post);
                                check = true;
                                break;
                            }
                        }

                        if (check) {
                            map.put("categoryList", listOfCategories);
                            documentReference.update("user", map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            onPostAddedCallback.onPostAdded(categoryId, post, true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", e.getLocalizedMessage());
                                            toaster.text(e.getLocalizedMessage());
                                            onPostAddedCallback.onPostAdded(categoryId, post, false);
                                        }
                                    });
                        } else {
                            toaster.text("Category not found");
                            onPostAddedCallback.onPostAdded(categoryId, post, false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        toaster.text(e.getLocalizedMessage());
                        onPostAddedCallback.onPostAdded(categoryId, post, false);
                    }
                });
    }

    @Override
    public void uploadImage(Category category, InputStream inputStream, OnPostAddedCallback onPostAddedCallback) {
        // File location for the image should be
        // /email/category/imageName.jpg

        String email = firebaseAuth.getCurrentUser().getEmail();
        String categoryName = category.getName();
        String fileName = "/" + email + "/" + categoryName + "/" + System.currentTimeMillis() + ".jpg";

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
                                        add(category.getId(), post, onPostAddedCallback);
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

    @Override
    public void deleteById(long categoryId, long postId, OnPostDeletedCallback onPostDeletedCallback) {
        String email = firebaseAuth.getCurrentUser().getEmail();
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();

                            Map<String, Object> userMap = (Map<String, Object>) snapshot.get("user");
                            List<Map<String, Object>> categoryList = (List<Map<String, Object>>) userMap.get("categoryList");

                            boolean checkRemoval = false;
                            String imageName;

                            for (Map<String, Object> mapCategory : categoryList) {
                                long catId = Long.parseLong(String.valueOf(mapCategory.get("id")));
                                if (catId == categoryId) {
                                    List<Map<String, Object>> postList = (List<Map<String, Object>>) mapCategory.get("postList");
                                    for (Map<String, Object> mapPost : postList) {
                                        long pId = Long.parseLong(String.valueOf(mapPost.get("id")));
                                        if (postId == pId) {
                                            // Post found //

                                            postList.remove(mapPost);
                                            checkRemoval = true;

                                            mapCategory.put("postList", postList);
                                            userMap.put("categoryList", categoryList);

                                            imageName = String.valueOf(mapPost.get("name"));


                                            StorageReference imageRef = storageReference.child(imageName);
                                            imageRef.delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("Tag", "Removed from cloud storage");
                                                            documentReference.update("user", userMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            toaster.text("Removed successfully");
                                                                            Log.d("Tag", "Removed from firebase firestore");

                                                                            // Find the layout with categoryID => then find the post with post id

                                                                            onPostDeletedCallback.onPostDeleted(true);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("Tag", e.getLocalizedMessage());
                                                                            onPostDeletedCallback.onPostDeleted(false);
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Tag", e.getLocalizedMessage());
                                                            onPostDeletedCallback.onPostDeleted(false);
                                                        }
                                                    });

                                            break;
                                        }
                                    }
                                }

                                if (checkRemoval) {
                                    onPostDeletedCallback.onPostDeleted(false);
                                    break;
                                }
                            }

                        } else {
                            toaster.text("Task failed");
                            Log.d("Tag", "Task failed");
                            onPostDeletedCallback.onPostDeleted(false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text(e.getLocalizedMessage());
                        Log.d("Tag", e.getLocalizedMessage());
                        onPostDeletedCallback.onPostDeleted(false);
                    }
                });
    }
}














