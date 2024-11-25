package com.finki.courses.Repositories.Implementations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.IPostRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class PostRepository implements IPostRepository {

    private Context context;
    private Toaster toaster;
    private final String email;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public PostRepository(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.toaster = new Toaster(context);
    }

    @Override
    public void add(String category, Post post) {
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

                            if (name.equalsIgnoreCase(category)){
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
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", e.getLocalizedMessage());
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
}
