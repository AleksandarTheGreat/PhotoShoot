package com.finki.courses.Fragments.FragmentHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentAddPost;
import com.finki.courses.Fragments.FragmentGallery;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Model.User;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

public class FragmentHomeHelper {

    private Context context;
    private FragmentHomeBinding binding;
    private MainActivityHelper mainActivityHelper;

    private CategoryRepository categoryRepository;
    private Toaster toaster;

    public FragmentHomeHelper(Context context, FragmentHomeBinding binding, MainActivityHelper mainActivityHelper, Toaster toaster) {
        this.context = context;
        this.binding = binding;
        this.mainActivityHelper = mainActivityHelper;

        this.categoryRepository = new CategoryRepository(context, binding, this);
        this.toaster = toaster;
    }

    public void showCategoryInputDialog() {
        @SuppressLint("InflateParams") View inputView = LayoutInflater.from(context)
                .inflate(R.layout.input_category_layout, null);

        TextInputEditText textInputEditTextCategory = inputView.findViewById(R.id.textInputEditText);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Category")
                .setMessage("Name a category")
                .setView(inputView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = textInputEditTextCategory.getText().toString().trim();
                        if (text.isEmpty())
                            return;

                        // Save the new category to firebase
                        // Take the now saved category

                        categoryRepository.add(text);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toaster.text("That's what I thought");
                    }
                })
                .setCancelable(true)
                .show();
    }

    public void createAWholeCategoryLayout(Category category) {
        createAHeaderForCategory(category.getName());
        if (category.getPostList().isEmpty()) {
            createEmptyLayout();
        } else {
            createPostsLayout(category.getPostList());
        }
    }

    private void createAHeaderForCategory(String title) {
        LinearLayout.LayoutParams layoutParamsLinearHeader = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLinearHeader.setMargins(48, 0, 48, 42);

        RelativeLayout relativeLayoutHeader = new RelativeLayout(context);
        relativeLayoutHeader.setLayoutParams(layoutParamsLinearHeader);
        relativeLayoutHeader.setGravity(Gravity.START);


        RelativeLayout.LayoutParams layoutParamsTextView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.setMargins(0, 0, 0, 24);
        layoutParamsTextView.addRule(RelativeLayout.CENTER_VERTICAL);

        TextView textViewCategoryTitle = new TextView(context);
        textViewCategoryTitle.setTextSize(18);
        textViewCategoryTitle.setTypeface(Typeface.DEFAULT_BOLD);
        textViewCategoryTitle.setText(title);
        textViewCategoryTitle.setLayoutParams(layoutParamsTextView);


        // Icon view all
        RelativeLayout.LayoutParams layoutParamsImageIcon1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon1.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon1.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParamsImageIcon1.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconViewAll = new ImageView(context);
        imageViewIconViewAll.setId(View.generateViewId());
        imageViewIconViewAll.setImageResource(R.drawable.ic_test);
        imageViewIconViewAll.setLayoutParams(layoutParamsImageIcon1);
        imageViewIconViewAll.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentGallery());
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemGallery);
        });


        // Icon add post
        RelativeLayout.LayoutParams layoutParamsImageIcon2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon2.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon2.addRule(RelativeLayout.LEFT_OF, imageViewIconViewAll.getId());
        layoutParamsImageIcon2.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconAddPost = new ImageView(context);
        imageViewIconAddPost.setId(View.generateViewId());
        imageViewIconAddPost.setImageResource(R.drawable.ic_test);
        imageViewIconAddPost.setLayoutParams(layoutParamsImageIcon2);
        imageViewIconAddPost.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentAddPost());
        });


        // Icon delete category
        RelativeLayout.LayoutParams layoutParamsImageIcon3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon3.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon3.addRule(RelativeLayout.LEFT_OF, imageViewIconAddPost.getId());
        layoutParamsImageIcon3.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconDelete = new ImageView(context);
        imageViewIconDelete.setId(View.generateViewId());
        imageViewIconDelete.setImageResource(R.drawable.ic_test);
        imageViewIconDelete.setLayoutParams(layoutParamsImageIcon3);
        imageViewIconDelete.setOnClickListener(view -> {
            categoryRepository.delete(title);
        });


        relativeLayoutHeader.addView(imageViewIconViewAll);
        relativeLayoutHeader.addView(imageViewIconAddPost);
        relativeLayoutHeader.addView(imageViewIconDelete);
        relativeLayoutHeader.addView(textViewCategoryTitle);

        binding.linearLayoutCategories.addView(relativeLayoutHeader);
    }
    private void createEmptyLayout() {
        LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLinearLayout.setMargins(48, 0, 48, 64);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);


        LinearLayout.LayoutParams layoutParamsImageView = new LinearLayout.LayoutParams(250, 250);
        layoutParamsImageView.setMargins(0, 0, 0, 48);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setLayoutParams(layoutParamsImageView);


        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.setMargins(0, 0, 0, 24);

        TextView textViewNoPostsYet = new TextView(context);
        textViewNoPostsYet.setText("Sadly, you have no posts yet");
        textViewNoPostsYet.setTextSize(16);
        textViewNoPostsYet.setLayoutParams(layoutParamsTextView);


        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.setMargins(0, 0, 0, 24);

        Button button = new Button(context);
        button.setText("Create a post");
        button.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentAddPost());
        });
        button.setLayoutParams(layoutParamsButton);


        linearLayout.addView(imageView);
        linearLayout.addView(textViewNoPostsYet);
        linearLayout.addView(button);

        binding.linearLayoutCategories.addView(linearLayout);
    }
    private void createPostsLayout(List<Post> postList) {
        LinearLayout.LayoutParams layoutParamsHorizontalScrollView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
        layoutParamsHorizontalScrollView.setMargins(48, 0, 48, 64);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setLayoutParams(layoutParamsHorizontalScrollView);


        LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);

        for (int i = 0; i < postList.size(); i++) {
            LinearLayout.LayoutParams layoutParamsMaterialCardView = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.MATCH_PARENT);

            if (i == 0) {
                layoutParamsMaterialCardView.setMargins(0, 0, 16, 0);
            } else if (i == 5) {
                layoutParamsMaterialCardView.setMargins(16, 0, 0, 0);
            } else {
                layoutParamsMaterialCardView.setMargins(16, 0, 16, 0);
            }

            MaterialCardView materialCardView = new MaterialCardView(context);
            materialCardView.setLayoutParams(layoutParamsMaterialCardView);
            materialCardView.setClickable(true);
            materialCardView.setCheckable(true);
            materialCardView.setFocusable(true);

            materialCardView.setOnCheckedChangeListener(new MaterialCardView.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
                    materialCardView.setChecked(isChecked);
                }
            });

            linearLayout.addView(materialCardView);
        }

        horizontalScrollView.addView(linearLayout);
        binding.linearLayoutCategories.addView(horizontalScrollView);
    }


    public void showLogoAndTitle() {
        binding.imageViewLogo.setVisibility(View.VISIBLE);
        binding.textViewTitle.setVisibility(View.VISIBLE);
    }

    public void hideLogoAndTitle() {
        binding.imageViewLogo.setVisibility(View.GONE);
        binding.textViewTitle.setVisibility(View.GONE);
    }

    public void showScrollViewAndHideLinearLayout() {
        binding.scrollViewFragmentHome.setVisibility(View.VISIBLE);
        binding.linearLayoutEmptyList.setVisibility(View.INVISIBLE);
    }

    public void hideScrollViewAndShowLinearLayout() {
        binding.scrollViewFragmentHome.setVisibility(View.INVISIBLE);
        binding.linearLayoutEmptyList.setVisibility(View.VISIBLE);
    }

}













