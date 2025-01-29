package com.finki.courses.Repositories.Implementations;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Adapters.FeedPostAdapter;
import com.finki.courses.Fragments.FragmentFeed;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.FeedPost;
import com.finki.courses.Repositories.IFeedPostRepository;
import com.finki.courses.databinding.FragmentFeedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedPostRepository implements IFeedPostRepository {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Toaster toaster;
    private Context context;
    private MainActivityHelper mainActivityHelper;
    private static final String COLLECTION_FEED_POSTS = "FeedPosts";
    private FeedPostRepository feedPostRepository;

    public FeedPostRepository(Context context, MainActivityHelper mainActivityHelper) {
        this.context = context;
        this.mainActivityHelper = mainActivityHelper;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();

        this.storageReference = firebaseStorage.getReference();
        this.toaster = new Toaster(context);

        this.feedPostRepository = this;
    }

    @Override
    public void listAll(FragmentFeedBinding fragmentFeedBinding) {
        firebaseFirestore.collection(COLLECTION_FEED_POSTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<FeedPost> feedPosts = new ArrayList<>();
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for (int i=0;i<documentSnapshots.size();i++){
                            DocumentSnapshot documentSnapshot = documentSnapshots.get(i);

                            List<Map<String, Object>> list = (List<Map<String, Object>>) documentSnapshot.get("list");
                            for (int j=0;j<list.size();j++){
                                Map<String, Object> map = list.get(j);

                                long id = (long) map.get("id");
                                String email = (String) map.get("email");
                                String imageUrl = (String) map.get("imageUrl");
                                String fileLocation = (String) map.get("fileLocation");
                                List<String> listComments = (List<String>) map.get("listComments");
                                List<String> listLikes = (List<String>) map.get("listLikes");

                                Set<String> setLikes = new HashSet<>(listLikes);
                                FeedPost feedPost = new FeedPost(id, email, imageUrl, fileLocation, setLikes, listComments);
                                feedPosts.add(feedPost);
                            }
                        }

                        Collections.shuffle(feedPosts);
                        FeedPostAdapter feedPostAdapter = new FeedPostAdapter(context, feedPosts, feedPostRepository, mainActivityHelper);

                        fragmentFeedBinding.recyclerViewFeed.setLayoutManager(new LinearLayoutManager(context));
                        fragmentFeedBinding.recyclerViewFeed.setHasFixedSize(true);
                        fragmentFeedBinding.recyclerViewFeed.setAdapter(feedPostAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        toaster.text(e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void addFeedPostToDocument(FeedPost feedPost, String documentName) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Adding image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_FEED_POSTS).document(documentName);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) documentSnapshot.get("list");

                Map<String, Object> mapFeedPost = new HashMap<>();

                mapFeedPost.put("id", feedPost.getId());
                mapFeedPost.put("email", feedPost.getEmail());
                mapFeedPost.put("imageUrl", feedPost.getImageUrl());
                mapFeedPost.put("fileLocation", feedPost.getFileLocation());
                mapFeedPost.put("listLikes", feedPost.likesSetToList());
                mapFeedPost.put("listComments", feedPost.getListComments());

                list.add(mapFeedPost);

                firebaseFirestore.collection(COLLECTION_FEED_POSTS)
                        .document(documentName)
                        .update("list", list)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Tag", "Added feed post to document successfully");
                                toaster.text("Added successfully");
                                progressDialog.dismiss();

                                mainActivityHelper.changeFragments(new FragmentFeed(mainActivityHelper), false);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Tag", e.getLocalizedMessage());
                                toaster.text(e.getLocalizedMessage());
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag", e.getLocalizedMessage());
                toaster.text(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void uploadPostToStorage(FeedPost feedPost) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String email = firebaseAuth.getCurrentUser().getEmail();
        String filePath = "/" + email + "/feedPosts/" + System.currentTimeMillis() + ".jpg";
        StorageReference feedPostFile = storageReference.child(filePath);
        feedPostFile.putFile(Uri.parse(feedPost.getImageUrl()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        feedPostFile.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Change the local feedPost url from the gallery,
                                        // to the uploaded, new url in storage, before passing add to document
                                        feedPost.setImageUrl(String.valueOf(uri));
                                        feedPost.setFileLocation(filePath);

                                        addFeedPostToDocument(feedPost, email);

                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", e.getLocalizedMessage());
                                        toaster.text(e.getLocalizedMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        toaster.text(e.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void delete(FeedPost feedPost) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Deletion in process...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String email = firebaseAuth.getCurrentUser().getEmail();
        String fileLocation = feedPost.getFileLocation();
        StorageReference imageFile = storageReference.child(fileLocation);
        imageFile.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseFirestore.collection(COLLECTION_FEED_POSTS)
                                .document(email)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<Map<String, Object>> listOfFeedPosts = (List<Map<String, Object>>) documentSnapshot.get("list");
                                        for (int i=0;i<listOfFeedPosts.size();i++){
                                            Map<String, Object> feedPostMap = listOfFeedPosts.get(i);
                                            long id = (long) feedPostMap.get("id");

                                            // FeedPost found
                                            if (id == feedPost.getId()){
                                                listOfFeedPosts.remove(feedPostMap);

                                                firebaseFirestore.collection(COLLECTION_FEED_POSTS)
                                                        .document(email)
                                                        .update("list", listOfFeedPosts)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                progressDialog.dismiss();
                                                                toaster.text("Deleted post with id '" + feedPost.getId() + "'");
                                                                mainActivityHelper.changeFragments(new FragmentFeed(mainActivityHelper), false);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Tag", e.getLocalizedMessage());
                                                                toaster.text(e.getLocalizedMessage());
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                break;
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", e.getLocalizedMessage());
                                        toaster.text(e.getLocalizedMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        toaster.text(e.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                });
    }
}
