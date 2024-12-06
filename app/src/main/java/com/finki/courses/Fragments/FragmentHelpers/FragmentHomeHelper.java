package com.finki.courses.Fragments.FragmentHelpers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.finki.courses.Fragments.ImageSliderFragment;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Model.User;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.Utils.ThemeUtils;
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
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

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
    private PostRepository postRepository;
    private Toaster toaster;

    public FragmentHomeHelper(Context context, FragmentHomeBinding binding, MainActivityHelper mainActivityHelper) {
        this.context = context;
        this.binding = binding;
        this.mainActivityHelper = mainActivityHelper;

        this.postRepository = new PostRepository(context, mainActivityHelper);
        this.categoryRepository = new CategoryRepository(context, binding, this);
        this.toaster = new Toaster(context);
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

    public void buildUILayoutForCategory(Category category) {
        List<Map<String, Object>> postList = category.getPostList();

        LinearLayout.LayoutParams layoutParamsFullLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsFullLinearLayout.setMargins(0,0,0,0);

        LinearLayout linearLayoutFullCategory = new LinearLayout(context);
        linearLayoutFullCategory.setOrientation(LinearLayout.VERTICAL);
        linearLayoutFullCategory.setLayoutParams(layoutParamsFullLinearLayout);
        linearLayoutFullCategory.setTag(String.valueOf(category.getId()));

        RelativeLayout relativeLayoutHeader = buildUIHeaderForCategory(category);
        linearLayoutFullCategory.addView(relativeLayoutHeader);
        if (postList.isEmpty()) {
            LinearLayout linearLayoutEmptyCategories = buildAnEmptyUILayoutForCategory(category);
            linearLayoutFullCategory.addView(linearLayoutEmptyCategories);
        } else {
            HorizontalScrollView horizontalScrollViewCategories = buildPostsUILayoutForCategory(category, postList);
            linearLayoutFullCategory.addView(horizontalScrollViewCategories);
        }

        binding.linearLayoutCategories.addView(linearLayoutFullCategory);
    }

    private RelativeLayout buildUIHeaderForCategory(Category category) {
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
        textViewCategoryTitle.setText(category.getName());
        textViewCategoryTitle.setLayoutParams(layoutParamsTextView);


        // Icon view all
        RelativeLayout.LayoutParams layoutParamsImageIcon1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon1.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon1.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParamsImageIcon1.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconViewAll = new ImageView(context);
        imageViewIconViewAll.setId(View.generateViewId());
        imageViewIconViewAll.setLayoutParams(layoutParamsImageIcon1);
        imageViewIconViewAll.setOnClickListener(view -> {
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemGallery);
            mainActivityHelper.changeFragments(new FragmentGallery(mainActivityHelper), true);
        });


        // Icon add post
        RelativeLayout.LayoutParams layoutParamsImageIcon2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon2.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon2.addRule(RelativeLayout.LEFT_OF, imageViewIconViewAll.getId());
        layoutParamsImageIcon2.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconAddPost = new ImageView(context);
        imageViewIconAddPost.setId(View.generateViewId());
        imageViewIconAddPost.setLayoutParams(layoutParamsImageIcon2);
        imageViewIconAddPost.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentAddPost(category, mainActivityHelper), true);
        });


        // Icon delete category
        RelativeLayout.LayoutParams layoutParamsImageIcon3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsImageIcon3.setMargins(8, 0, 8, 0);
        layoutParamsImageIcon3.addRule(RelativeLayout.LEFT_OF, imageViewIconAddPost.getId());
        layoutParamsImageIcon3.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView imageViewIconDelete = new ImageView(context);
        imageViewIconDelete.setId(View.generateViewId());
        imageViewIconDelete.setLayoutParams(layoutParamsImageIcon3);
        imageViewIconDelete.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Alert")
                    .setMessage("Are you sure you want to delete '" + category.getName() + "' category")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categoryRepository.deleteById(category.getId());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();
        });


        if (ThemeUtils.isNightModeOn(context)) {
            imageViewIconViewAll.setImageResource(R.drawable.ic_list_night);
            imageViewIconAddPost.setImageResource(R.drawable.ic_add_night);
            imageViewIconDelete.setImageResource(R.drawable.ic_delete_night);
        } else {
            imageViewIconViewAll.setImageResource(R.drawable.ic_list_day);
            imageViewIconAddPost.setImageResource(R.drawable.ic_add_day);
            imageViewIconDelete.setImageResource(R.drawable.ic_delete_day);
        }

        relativeLayoutHeader.addView(imageViewIconViewAll);
        relativeLayoutHeader.addView(imageViewIconAddPost);
        relativeLayoutHeader.addView(imageViewIconDelete);
        relativeLayoutHeader.addView(textViewCategoryTitle);

        return relativeLayoutHeader;
    }

    private LinearLayout buildAnEmptyUILayoutForCategory(Category category) {
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
            mainActivityHelper.changeFragments(new FragmentAddPost(category, mainActivityHelper), true);
        });
        button.setLayoutParams(layoutParamsButton);


        linearLayout.addView(imageView);
        linearLayout.addView(textViewNoPostsYet);
        linearLayout.addView(button);

        return linearLayout;
    }

    private HorizontalScrollView buildPostsUILayoutForCategory(Category category, List<Map<String, Object>> postList) {
        LinearLayout.LayoutParams layoutParamsHorizontalScrollView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650);
        layoutParamsHorizontalScrollView.setMargins(48, 0, 48, 64);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setLayoutParams(layoutParamsHorizontalScrollView);


        LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);

        for (int i = 0; i < postList.size(); i++) {
            // This represents a single post
            // A post in firebase document is a map
            // this is a map.
            Map<String, Object> postMap = (Map<String, Object>) postList.get(i);
            Uri imageUri = Uri.parse((String) postMap.get("imageUrl"));

            LinearLayout.LayoutParams layoutParamsMaterialCardView = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.MATCH_PARENT);

            if (i == 0) {
                layoutParamsMaterialCardView.setMargins(0, 0, 8, 0);
            } else if (i == 5) {
                layoutParamsMaterialCardView.setMargins(8, 0, 0, 0);
            } else {
                layoutParamsMaterialCardView.setMargins(8, 0, 8, 0);
            }

            MaterialCardView materialCardView = new MaterialCardView(context);
            materialCardView.setLayoutParams(layoutParamsMaterialCardView);
            materialCardView.setClickable(true);
            materialCardView.setCheckable(true);
            materialCardView.setFocusable(true);
            int finalI = i;
            materialCardView.setOnClickListener(view -> {
                // Change to imageViewSliderFragment
                mainActivityHelper.changeFragments(new ImageSliderFragment(mainActivityHelper, postList, finalI), true);
            });

            materialCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure you want to delete this post ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    long postId = Long.parseLong(String.valueOf(postMap.get("id")));

                                    postRepository.deleteById(category.getId(), postId);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    toaster.text("That's what I thought");
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .show();

                    return true;
                }
            });


            LinearLayout.LayoutParams layoutParamsForImageView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParamsForImageView.setMargins(0, 0, 0, 0);

            ImageView imageViewPost = new ImageView(context);
            if (postMap.get("imageUrl").equals("")) {
                imageViewPost.setImageResource(0);
            } else
                Picasso.get().load(imageUri).into(imageViewPost);
            imageViewPost.setLayoutParams(layoutParamsForImageView);
            imageViewPost.setScaleType(ImageView.ScaleType.CENTER_CROP);


            materialCardView.addView(imageViewPost);
            linearLayout.addView(materialCardView);
        }

        horizontalScrollView.addView(linearLayout);

        return horizontalScrollView;
    }

    public void deleteUILayoutForCategory(long id){
        LinearLayout mainLinearLayout = binding.linearLayoutCategories;

        for (int i=0;i<mainLinearLayout.getChildCount();i++){
            LinearLayout linearLayoutCategory = (LinearLayout) mainLinearLayout.getChildAt(i);
            long tag = Long.parseLong(linearLayoutCategory.getTag().toString());

            if (tag == id){
                mainLinearLayout.removeViewAt(i);
                break;
            }
        }

        if (mainLinearLayout.getChildCount() <= 0)
            hideScrollViewAndShowLinearLayout();
        else
            showScrollViewAndHideLinearLayout();
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













