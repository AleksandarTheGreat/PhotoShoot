package com.finki.courses.Fragments.FragmentHelpers;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.finki.courses.Fragments.FragmentSingleImage;
import com.finki.courses.Fragments.ImageSliderFragment;
import com.finki.courses.Model.Post;

import java.util.List;
import java.util.Map;

public class CustomPagerAdapter extends FragmentStateAdapter {

    private Context context;
    private Activity activity;
    private List<Map<String, Object>> postList;
    public CustomPagerAdapter(Context context, FragmentActivity fragmentActivity, List<Map<String, Object>> postList) {
        super(fragmentActivity);

        this.context = context;
        this.activity = fragmentActivity;
        this.postList = postList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Map<String, Object> postMap = postList.get(position);
        return new FragmentSingleImage(postMap);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}








