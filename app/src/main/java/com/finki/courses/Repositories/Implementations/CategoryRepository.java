package com.finki.courses.Repositories.Implementations;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.finki.courses.Fragments.FragmentHelpers.FragmentHomeHelper;
import com.finki.courses.Fragments.FragmentHome;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.ICategoriesRepository;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CategoryRepository implements ICategoriesRepository {

    private final Context context;
    private final FragmentHomeBinding binding;
    private final FragmentHomeHelper fragmentHomeHelper;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final String email;

    private final Toaster toaster;

    public CategoryRepository(Context context, FragmentHomeBinding binding, FragmentHomeHelper fragmentHomeHelper){
        this.context = context;
        this.binding = binding;
        this.fragmentHomeHelper = fragmentHomeHelper;
        this.toaster = new Toaster(context);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();
    }

    @Override
    public void listAll() {
        binding.linearLayoutCategories.removeAllViews();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfMaps = (List<Map<String, Object>>) map.get("categoryList");

                        List<Category> categoryList = new ArrayList<>();
                        Objects.requireNonNull(listOfMaps).forEach(new Consumer<Map<String, Object>>() {
                            @Override
                            public void accept(Map<String, Object> stringObjectMap) {
                                String name = (String) stringObjectMap.get("name");
                                List<Post> postList = (List<Post>) stringObjectMap.get("postList");

                                Category category = new Category(name, postList);
                                categoryList.add(category);
                            }
                        });

                        if (!categoryList.isEmpty()){
                            fragmentHomeHelper.showScrollViewAndHideLinearLayout();
                        } else {
                            fragmentHomeHelper.hideScrollViewAndShowLinearLayout();
                        }

                        for (Category category: categoryList){
                            fragmentHomeHelper.createAWholeCategoryLayout(category);
                        }

                        Log.d("Tag", "All categories are : " + categoryList);
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
    public Category findCategoryByName(String name) {
        return null;
    }

    @Override
    public void add(String name) {
        // Get the document corresponding to that email
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Category> categoryList = (List<Category>) map.get("categoryList");

                        Category category = new Category(name);
                        categoryList.add(category);

                        map.put("categoryList", categoryList);

                        documentReference.update("user", map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        toaster.text("Added new category");

                                        fragmentHomeHelper.showScrollViewAndHideLinearLayout();
                                        fragmentHomeHelper.createAWholeCategoryLayout(new Category(name, new ArrayList<>()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", "Failed to add new category");
                                    }
                                });
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
    public void delete(String name) {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                        for (Map<String, Object> map1: listOfCategories){
                            String title = (String) map1.get("name");
                            if (title.equalsIgnoreCase(name)){
                                listOfCategories.remove(map1);
                                break;
                            }
                        }

                        map.put("categoryList", listOfCategories);

                        documentReference.update("user", map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Neka se izbrishe samo voa VIEW GROUP a ne cela da se pre brishe od pochetok
                                        // No za toa ke ni e potrebno da gi grupirame header i content layouts
                                        listAll();
                                        toaster.text("Deleted category");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Tag", e.getLocalizedMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                    }
                });
    }
}
