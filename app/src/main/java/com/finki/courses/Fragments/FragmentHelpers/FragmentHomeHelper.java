package com.finki.courses.Fragments.FragmentHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentHomeBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class FragmentHomeHelper {

    private Context context;
    private FragmentHomeBinding binding;
    private Toaster toaster;
    public FragmentHomeHelper(Context context, FragmentHomeBinding binding, Toaster toaster){
        this.context = context;
        this.binding = binding;
        this.toaster = toaster;
    }

    public void showLogoAndTitle(){
        binding.imageViewLogo.setVisibility(View.VISIBLE);
        binding.textViewTitle.setVisibility(View.VISIBLE);
    }

    public void hideLogoAndTitle(){
        binding.imageViewLogo.setVisibility(View.GONE);
        binding.textViewTitle.setVisibility(View.GONE);
    }

    public void showScrollViewAndHideLinearLayout(){
        binding.scrollViewFragmentHome.setVisibility(View.VISIBLE);
        binding.linearLayoutEmptyList.setVisibility(View.INVISIBLE);
    }

    public void hideScrollViewAndShowLinearLayout(){
        binding.scrollViewFragmentHome.setVisibility(View.INVISIBLE);
        binding.linearLayoutEmptyList.setVisibility(View.VISIBLE);
    }

    public void showCategoryInputDialog(){
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
                        toaster.text(text + " saved nagjomiti");
                        // Take the now saved category

                        showScrollViewAndHideLinearLayout();
                        createAWholeCategoryLayout(text, true);
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

    public void createAWholeCategoryLayout(String title, boolean isEmpty){
        createAHeaderForCategory(title);

        // If the category list is empty
        if (isEmpty){
            createEmptyLayout();
        } else {
            createPostsLayout();
        }
    }

    private void createAHeaderForCategory(String title){
        LinearLayout.LayoutParams layoutParamsLinearHeader = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLinearHeader.setMargins(48, 0, 48, 32);

        LinearLayout linearLayoutHeader = new LinearLayout(context);
        linearLayoutHeader.setLayoutParams(layoutParamsLinearHeader);
        linearLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        linearLayoutHeader.setGravity(Gravity.START);



        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.setMargins(0, 0, 0, 24);

        TextView textViewCategoryTitle = new TextView(context);
        textViewCategoryTitle.setTextSize(18);
        textViewCategoryTitle.setTypeface(Typeface.DEFAULT_BOLD);
        textViewCategoryTitle.setText(title);
        textViewCategoryTitle.setLayoutParams(layoutParamsTextView);



        LinearLayout.LayoutParams layoutParamsButtonViewAll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsButtonViewAll.setMargins(0, 0, 0, 0);

        TextView textViewViewAll = new TextView(context);
        textViewViewAll.setText("View all");
        textViewViewAll.setTextSize(14);
        textViewViewAll.setTextColor(Color.BLUE);
        textViewViewAll.setLayoutParams(layoutParamsButtonViewAll);



        linearLayoutHeader.addView(textViewCategoryTitle);
        linearLayoutHeader.addView(textViewViewAll);

        binding.linearLayoutCategories.addView(linearLayoutHeader);
    }

    private void createEmptyLayout(){
        LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLinearLayout.setMargins(48, 0, 48, 64);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);



        LinearLayout.LayoutParams layoutParamsImageView = new LinearLayout.LayoutParams(300, 300);
        layoutParamsImageView.setMargins(0, 0,0, 48);

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setLayoutParams(layoutParamsImageView);



        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.setMargins(0,0,0,24);

        TextView textViewNoPostsYet = new TextView(context);
        textViewNoPostsYet.setText("Sadly, you have no posts yet");
        textViewNoPostsYet.setTextSize(16);
        textViewNoPostsYet.setLayoutParams(layoutParamsTextView);



        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.setMargins(0,0,0,24);

        Button button = new Button(context);
        button.setText("Create a post");
        button.setOnClickListener(view -> {
            toaster.text("Not ready yet, bae");
        });
        button.setLayoutParams(layoutParamsButton);



        linearLayout.addView(imageView);
        linearLayout.addView(textViewNoPostsYet);
        linearLayout.addView(button);

        binding.linearLayoutCategories.addView(linearLayout);
    }


    private void createPostsLayout(){
        LinearLayout.LayoutParams layoutParamsHorizontalScrollView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
        layoutParamsHorizontalScrollView.setMargins(48, 0, 48, 64);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setLayoutParams(layoutParamsHorizontalScrollView);


        LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);

        for (int i=0;i<6;i++){
            LinearLayout.LayoutParams layoutParamsMaterialCardView = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.MATCH_PARENT);

            if (i == 0){
                layoutParamsMaterialCardView.setMargins(0, 0, 16, 0);
            } else if (i == 5){
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
}













