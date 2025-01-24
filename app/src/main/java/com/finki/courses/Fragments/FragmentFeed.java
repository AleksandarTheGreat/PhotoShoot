package com.finki.courses.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Adapters.FeedPostAdapter;
import com.finki.courses.Helper.IEssentials;
import com.finki.courses.Helper.IToaster;
import com.finki.courses.Model.FeedPost;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.FeedPostRepository;
import com.finki.courses.databinding.FragmentFeedBinding;

import java.util.ArrayList;
import java.util.List;

public class FragmentFeed extends Fragment implements IEssentials {

    private FragmentFeedBinding binding;
    private MainActivityHelper mainActivityHelper;
    private FeedPostRepository feedPostRepository;

    public FragmentFeed() {
        // Required empty public constructor
    }

    public FragmentFeed(MainActivityHelper mainActivityHelper){
        this.mainActivityHelper = mainActivityHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.bind(inflater.inflate(R.layout.fragment_feed, container, false));

        instantiateObjects();
        addEventListeners();

        return binding.getRoot();
    }

    @Override
    public void instantiateObjects() {
        feedPostRepository = new FeedPostRepository(getContext(), mainActivityHelper);
        feedPostRepository.listAll(binding);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.recyclerViewFeed);
    }

    @Override
    public void addEventListeners() {
        binding.floatingActionButtonAddFeedPost.setOnClickListener(view -> {
            mainActivityHelper.changeFragments(new FragmentAddFeedPost(mainActivityHelper), true);
        });
    }

    @Override
    public void additionalThemeChanges() {

    }
}