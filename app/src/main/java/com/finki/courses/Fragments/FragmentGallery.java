package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.finki.courses.Repositories.Implementations.PostRepository;
import com.finki.courses.databinding.FragmentGalleryBinding;


public class FragmentGallery extends Fragment implements IEssentials {

    private FragmentGalleryBinding binding;
    private FragmentGalleryHelper fragmentGalleryHelper;
    private Toaster toaster;
    private PostRepository postRepository;
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

        postRepository = new PostRepository(getContext());
        postRepository.listAllForGallery(fragmentGalleryHelper);

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