package com.finki.courses.Fragments.FragmentHelpers;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.ImageSliderFragment;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
        if (allPostsList.isEmpty()){
            fragmentGalleryBinding.linearLayoutEmptyGalleryFragment.setVisibility(View.VISIBLE);
            return;
        }

        fragmentGalleryBinding.linearLayoutEmptyGalleryFragment.setVisibility(View.GONE);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        Log.d("Tag", "Width: " + width);
        Log.d("Tag", "Height: " + height);

        int totalMargin = 8;
        int halfMargin = totalMargin / 2;
        int columnCount = 4;

        int imageSide = (width / columnCount) - totalMargin;
        Log.d("Tag", "Image side: " + imageSide);

        Map<String, List<Map<String, Object>>> datePostsMappingsMap = new HashMap<>();

        for (int i = 0; i < allPostsList.size(); i++) {
            Map<String, Object> postMap = allPostsList.get(i);
            String wholeDate = calculateDateFromPost(postMap);

            if (!datePostsMappingsMap.containsKey(wholeDate))
                datePostsMappingsMap.put(wholeDate, new ArrayList<>());
            datePostsMappingsMap.get(wholeDate).add(postMap);

            Log.d("Tag", datePostsMappingsMap.toString());
        }

        datePostsMappingsMap.forEach(new BiConsumer<String, List<Map<String, Object>>>() {
            @Override
            public void accept(String s, List<Map<String, Object>> maps) {
                LinearLayout.LayoutParams layoutParamsTextViewTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsTextViewTitle.setMargins(24,24,24,16);

                TextView textViewDate = new TextView(context);
                textViewDate.setText(s);
                textViewDate.setLayoutParams(layoutParamsTextViewTitle);
                textViewDate.setTextSize(16);
                textViewDate.setTextColor(ContextCompat.getColor(context, R.color.white));


                LinearLayout.LayoutParams layoutParamsGridLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsGridLayout.setMargins(halfMargin,0,halfMargin,48);
                layoutParamsGridLayout.gravity = Gravity.START;

                GridLayout gridLayout = new GridLayout(context);
                gridLayout.setColumnCount(4);
                gridLayout.setLayoutParams(layoutParamsGridLayout);

                for (int i=0;i<maps.size();i++){
                    Map<String, Object> postMap = maps.get(i);

                    LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(imageSide, imageSide);
                    layoutParamsImage.setMargins(halfMargin, halfMargin, halfMargin, halfMargin);

                    Uri imageUri = Uri.parse(String.valueOf(postMap.get("imageUrl")));

                    ImageView imageView = new ImageView(context);
                    Picasso.get().load(imageUri).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(layoutParamsImage);
                    int finalI = i;
                    imageView.setOnClickListener(view -> {
                        mainActivityHelper.changeFragments(new ImageSliderFragment(mainActivityHelper, maps, finalI), true);
                    });

                    gridLayout.addView(imageView);
                }

                fragmentGalleryBinding.mainLinearLayoutGalleryFragment.addView(textViewDate);
                fragmentGalleryBinding.mainLinearLayoutGalleryFragment.addView(gridLayout);
            }
        });
    }

    private String calculateDateFromPost(Map<String, Object> postMap){
        Map<String, Object> postedAtMap = (Map<String, Object>) postMap.get("postedAt");

        long dayOfMonth = (long) postedAtMap.get("dayOfMonth");
        String dayOfWeek = (String) postedAtMap.get("dayOfWeek");
        String month = (String) postedAtMap.get("month");
        long year = (long) postedAtMap.get("year");

        Log.d("Tag", String.format("%s, %d %s %d", dayOfWeek,  dayOfMonth, month, year));
        return dayOfWeek + " " + dayOfMonth + " " + month + " " + year;
    }

}
