package com.finki.courses.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.CustomPagerAdapter;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.Post;
import com.finki.courses.R;
import com.finki.courses.databinding.FragmentImageSliderBinding;

import java.util.List;
import java.util.Map;

public class ImageSliderFragment extends Fragment implements IEssentials {

    private MainActivityHelper mainActivityHelper;
    private FragmentImageSliderBinding binding;
    private List<Map<String, Object>> listOfMaps;
    private CustomPagerAdapter customPagerAdapter;
    private int currentImagePosition;
    private Toaster toaster;

    public ImageSliderFragment(){

    }

    public ImageSliderFragment(MainActivityHelper mainActivityHelper, List<Map<String, Object>> listOfMaps, int currentImagePosition) {
        this.mainActivityHelper = mainActivityHelper;
        this.listOfMaps = listOfMaps;
        this.currentImagePosition = currentImagePosition;
        toaster = new Toaster(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageSliderBinding.bind(inflater.inflate(R.layout.fragment_image_slider, container, false));

        instantiateObjects();
        additionalThemeChanges();

        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        customPagerAdapter = new CustomPagerAdapter(getContext(), getActivity(), listOfMaps);
        binding.viewPager2FragmentImageSlider.setAdapter(customPagerAdapter);
        binding.viewPager2FragmentImageSlider.setCurrentItem(currentImagePosition);

        hideSystemBarsAndNavigationView();
    }

    @Override
    public void addEventListeners() {
        binding.viewPager2FragmentImageSlider.setOnClickListener(view -> {
            toaster.text("Clicked");
        });
    }

    @Override
    public void additionalThemeChanges() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showSystemBarsAndNavigationView();
    }

    private void hideSystemBarsAndNavigationView(){
        mainActivityHelper.getBinding().bottomNavigationView.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getActivity().getWindow().getInsetsController();
            if (controller != null){
                controller.hide(WindowInsets.Type.systemBars());
            }
        }
    }

    private void showSystemBarsAndNavigationView(){
        mainActivityHelper.getBinding().bottomNavigationView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getActivity().getWindow().getInsetsController();
            if (controller != null){
                controller.show(WindowInsets.Type.systemBars());
            }
        }
    }
}





