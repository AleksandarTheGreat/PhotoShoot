package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentGalleryHelper;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.R;
import com.finki.courses.Repositories.Callbacks.Category.OnPostsListedCallback;
import com.finki.courses.Repositories.Implementations.CategoryRepository;
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.ViewModel.ViewModelCategories;
import com.finki.courses.databinding.FragmentGalleryBinding;

import java.util.List;
import java.util.Map;


public class FragmentGallery extends Fragment implements IEssentials {

    private FragmentGalleryBinding binding;
    private FragmentGalleryHelper fragmentGalleryHelper;
    private Toaster toaster;
    private ViewModelCategories viewModelCategories;
    private MainActivityHelper mainActivityHelper;

    public FragmentGallery(MainActivityHelper mainActivityHelper) {
        // Required empty public constructor
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.bind(inflater.inflate(R.layout.fragment_gallery, container, false));

        instantiateObjects();
        additionalThemeChanges();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        toaster = new Toaster(getContext());

        fragmentGalleryHelper = new FragmentGalleryHelper(getContext(), binding, mainActivityHelper);

        viewModelCategories = new ViewModelProvider(getActivity()).get(ViewModelCategories.class);
        viewModelCategories.listAllPosts(new OnPostsListedCallback() {
            @Override
            public void onListed(List<Map<String, Object>> list) {
                if (!list.isEmpty()){
                    fragmentGalleryHelper.buildImages(list);
                } else {
                    toaster.text("Somehow all posts are empty");
                }
            }
        });

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        Log.d("Tag", "Width: " + width + ", Height: " + height);
    }

    @Override
    public void addEventListeners() {
        binding.buttonICanFixThat.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentHome(mainActivityHelper), false);
            mainActivityHelper.getBinding().bottomNavigationView.setSelectedItemId(R.id.itemHome);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}