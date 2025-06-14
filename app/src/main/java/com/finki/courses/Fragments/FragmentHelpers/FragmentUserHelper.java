package com.finki.courses.Fragments.FragmentHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.ImageSliderFragment;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentUserBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class FragmentUserHelper {

    private Context context;
    private FragmentUserBinding fragmentUserBinding;
    private Toaster toaster;

    public FragmentUserHelper(Context context, FragmentUserBinding fragmentUserBinding) {
        this.context = context;
        this.fragmentUserBinding = fragmentUserBinding;
        this.toaster = new Toaster(context);
    }

    @SuppressLint("SetTextI18n")
    public void buildImages(MainActivityHelper mainActivityHelper, List<Map<String, Object>> postList) {
        if (postList.isEmpty()) {
            fragmentUserBinding.linearLayoutEmptyUserFragment.setVisibility(View.VISIBLE);
            fragmentUserBinding.gridLayoutPostsUserFragment.setVisibility(View.GONE);
            return;
        }
        fragmentUserBinding.linearLayoutEmptyUserFragment.setVisibility(View.GONE);
        fragmentUserBinding.gridLayoutPostsUserFragment.setVisibility(View.VISIBLE);

        fragmentUserBinding.textViewTotalPosts.setText(postList.size() + " posts.");

        int windowWidth = context.getResources().getDisplayMetrics().widthPixels;
        int windowHeight = context.getResources().getDisplayMetrics().heightPixels;

        Log.d("Tag", "Width: " + windowWidth + ", Height: " + windowHeight);

        int columnCount = 3;
        int imageSide = (windowWidth / columnCount) - 8;
        int margin = 4;

        for (int i = 0; i < postList.size(); i++) {
            Map<String, Object> postMap = postList.get(i);
            Uri imageUri = Uri.parse(String.valueOf(postMap.get("imageUrl")));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSide, imageSide);
            layoutParams.setMargins(margin, margin, margin, margin);

            ImageView imageView = new ImageView(context);
            Glide.with(context)
                    .load(imageUri)
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(layoutParams);
            int finalI = i;
            imageView.setOnClickListener(view -> {
                int width = view.getWidth();
                int height = view.getHeight();

                toaster.text("Width: " + width + ", Height: " + height);
                mainActivityHelper.changeFragments(new ImageSliderFragment(mainActivityHelper, postList, finalI), true);
            });

            fragmentUserBinding.gridLayoutPostsUserFragment.addView(imageView);
        }
    }

    public FragmentUserBinding getFragmentUserBinding() {
        return fragmentUserBinding;
    }
}
