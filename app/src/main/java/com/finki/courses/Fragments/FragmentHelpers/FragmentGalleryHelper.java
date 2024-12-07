package com.finki.courses.Fragments.FragmentHelpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.ImageSliderFragment;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class FragmentGalleryHelper {

    private final Context context;
    private final FragmentGalleryBinding fragmentGalleryBinding;
    private final MainActivityHelper mainActivityHelper;
    private final Toaster toaster;

    public FragmentGalleryHelper(Context context, FragmentGalleryBinding fragmentGalleryBinding, MainActivityHelper mainActivityHelper) {
        this.context = context;
        this.fragmentGalleryBinding = fragmentGalleryBinding;
        this.mainActivityHelper = mainActivityHelper;
        this.toaster = new Toaster(context);
    }

    public void buildImages(List<Map<String, Object>> allPostsList) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        Log.d("Tag", "Width: " + width);
        Log.d("Tag", "Height: " + height);

        int totalMargin = 8;
        int halfMargin = totalMargin / 2;
        int columnCount = 4;

        int imageSide = (width / columnCount) - totalMargin;
        Log.d("Tag", "Image side: " + imageSide);

        for (int i = 0; i < allPostsList.size(); i++) {
            Map<String, Object> postMap = allPostsList.get(i);

            LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(imageSide, imageSide);
            layoutParamsImage.setMargins(halfMargin, halfMargin, halfMargin, halfMargin);

            Uri imageUri = Uri.parse(String.valueOf(postMap.get("imageUrl")));

            ImageView imageView = new ImageView(context);
            Picasso.get().load(imageUri).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(layoutParamsImage);
            int finalI = i;
            imageView.setOnClickListener(view -> {
                mainActivityHelper.changeFragments(new ImageSliderFragment(mainActivityHelper, allPostsList, finalI), true);
            });

            fragmentGalleryBinding.gridLayout.addView(imageView);
        }
    }

}
