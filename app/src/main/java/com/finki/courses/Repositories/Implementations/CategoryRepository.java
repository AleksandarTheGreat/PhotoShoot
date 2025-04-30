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
import com.finki.courses.Repositories.Callbacks.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.OnCategoryAddedCallback;
import com.finki.courses.Repositories.Callbacks.OnCategoryDeletedCallback;
import com.finki.courses.Repositories.ICategoriesRepository;
import com.finki.courses.databinding.FragmentHomeBinding;
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
import com.google.firestore.v1.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class CategoryRepository implements ICategoriesRepository {

    private static final String COLLECTION_NAME = "Users";
    private final Context context;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final String email;
    private final Toaster toaster;
    private List<Category> categoryList;

    public CategoryRepository(Context context){
        this.context = context;

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.email = firebaseAuth.getCurrentUser().getEmail();

        this.categoryList = new ArrayList<>();
        this.toaster = new Toaster(context);
    }

    @Override
    public void listAll(OnCategoriesLoadedCallBack onCategoriesLoadedCallBack) {
        categoryList = new ArrayList<>();

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                                List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                                Objects.requireNonNull(listOfCategories).forEach(new Consumer<Map<String, Object>>() {
                                    @Override
                                    public void accept(Map<String, Object> stringObjectMap) {
                                        long id = Long.parseLong(String.valueOf(stringObjectMap.get("id")));
                                        String name = (String) stringObjectMap.get("name");
                                        List<Map<String, Object>> postList = (List<Map<String, Object>>) stringObjectMap.get("postList");

                                        Category category = new Category(id, name, postList);
                                        categoryList.add(category);
                                    }
                                });

                                onCategoriesLoadedCallBack.onLoaded(categoryList);

                            } else {
                                toaster.text("Document doesn't exist, yet");
                                onCategoriesLoadedCallBack.onLoaded(categoryList);
                            }
                        } else {
                            toaster.text("Task failed");
                            onCategoriesLoadedCallBack.onLoaded(categoryList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toaster.text("Failed to retrieve the document");
                        onCategoriesLoadedCallBack.onLoaded(categoryList);
                    }
                });
    }

    // Not needed for now
    // I will figure it out later
    @Override
    public Category findCategoryById(long id) {
        Category category;
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> userMap = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategoriesMap = (List<Map<String, Object>>) userMap.get("categoryList");

                        for (Map<String, Object> map : listOfCategoriesMap) {
                            long cid = Long.parseLong(String.valueOf(map.get("id")));
                            if (id == cid) {
                                List<Post> postList = (List<Post>) map.get("postList");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                    }
                });

        return null;
    }

    @Override
    public void add(String name, OnCategoryAddedCallback onCategoryAddedCallback) {
        // Get the document corresponding to that email
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                            List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                            long categoryID = UUID.randomUUID().getLeastSignificantBits() * -1;
                            Map<String, Object> categoryMap = new HashMap<>();
                            categoryMap.put("id", categoryID);
                            categoryMap.put("name", name);
                            categoryMap.put("postList", new ArrayList<>());

                            listOfCategories.add(categoryMap);
                            map.put("categoryList", listOfCategories);
                            documentReference.update("user", map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            toaster.text("Added new category");
                                            onCategoryAddedCallback.onCategoryAdded(true, new Category(categoryID, name, new ArrayList<>()));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", "Failed to add new category");
                                            onCategoryAddedCallback.onCategoryAdded(false, null);
                                        }
                                    });
                        } else {

                            // This is in case the document has not been created when the user is created
                            // This is more of a safety precaution rather than actually being used
                            // Since with every user creation there is a document for that user being created

                            long categoryId = UUID.randomUUID().getLeastSignificantBits() * -1;
                            Map<String, Object> categoryMap = new HashMap<>();
                            categoryMap.put("id", categoryId);
                            categoryMap.put("name", name);
                            categoryMap.put("postList", new ArrayList<>());

                            List<Map<String, Object>> categoryList = new ArrayList<>();
                            categoryList.add(categoryMap);

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("profilePhotoUrl", "");
                            userMap.put("coverPhotoUrl", "");
                            userMap.put("categoryList", categoryList);
                            userMap.put("email", email);

                            Map<String, Object> finalMap = new HashMap<>();
                            finalMap.put("user", userMap);
                            documentReference.set(finalMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            toaster.text("Added new category");
                                            onCategoryAddedCallback.onCategoryAdded(true, new Category(categoryId, name, new ArrayList<>()));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Tag", e.getLocalizedMessage());
                                            onCategoryAddedCallback.onCategoryAdded(false, null);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getLocalizedMessage());
                        onCategoryAddedCallback.onCategoryAdded(false, null);
                    }
                });
    }

    @Override
    public void deleteById(long id, OnCategoryDeletedCallback onCategoryDeletedCallback) {
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(email);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) documentSnapshot.get("user");
                        List<Map<String, Object>> listOfCategories = (List<Map<String, Object>>) map.get("categoryList");

                        for (Map<String, Object> categoryMap : listOfCategories) {
                            long cid = Long.parseLong(String.valueOf(categoryMap.get("id")));
                            if (id == cid) {

                                // Find the posts and delete all of them from storage

                                List<Map<String, Object>> postList = (List<Map<String, Object>>) categoryMap.get("postList");
                                for (Map<String, Object> postMap: postList){
                                    long idd = (long) postMap.get("id");
                                    String imageUrl = (String) postMap.get("imageUrl");
                                    String name = (String) postMap.get("name");

                                    Log.d("Tag", "ID: " + idd + "\nImage url: " + imageUrl + "\nName: " + name);
                                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(name);

                                    // Somehow wait for all of these async deletes to finish,
                                    // Then continue to remove the list I guess ??

                                    imageRef.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Tag", "Successfully deleted image at location: '" + name + "'");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Tag", e.getLocalizedMessage());
                                                }
                                            });
                                }

                                // Delete the document itself
                                listOfCategories.remove(categoryMap);
                                map.put("categoryList", listOfCategories);
                                documentReference.update("user", map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                onCategoryDeletedCallback.onCategoryDeleted(true, id);
                                                toaster.text("Deleted category");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Tag", e.getLocalizedMessage());
                                                onCategoryDeletedCallback.onCategoryDeleted(false, id);
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
                        onCategoryDeletedCallback.onCategoryDeleted(false, id);
                    }
                });
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
