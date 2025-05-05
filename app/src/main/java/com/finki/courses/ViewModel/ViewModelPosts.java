package com.finki.courses.ViewModel;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentGalleryHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.Callbacks.Post.OnPostAddedCallback;
import com.finki.courses.Repositories.Callbacks.Post.OnPostDeletedCallback;
import com.finki.courses.Repositories.IPostRepository;
import com.finki.courses.Repositories.Implementations.PostRepository;

import java.io.InputStream;
import java.util.zip.CheckedOutputStream;

public class ViewModelPosts extends ViewModel implements IPostRepository {

    private Context context;
    private PostRepository postRepository;

    public void init(Context context, MainActivityHelper mainActivityHelper){
        this.context = context;
        this.postRepository = new PostRepository(context, mainActivityHelper);
    }

    // This can be removed since
    // it is not called directly, but uploadImage calls (postRepository.add()) internally

    @Override
    public void add(long categoryId, Post post, OnPostAddedCallback onPostAddedCallback) {
        postRepository.add(categoryId, post, onPostAddedCallback);
    }

    @Override
    public void uploadImage(Category category, InputStream inputStream, OnPostAddedCallback onPostAddedCallback) {
        postRepository.uploadImage(category, inputStream, onPostAddedCallback);
    }

    @Override
    public void deleteById(long categoryId, long postId, OnPostDeletedCallback onPostDeletedCallback) {
        postRepository.deleteById(categoryId, postId, onPostDeletedCallback);
    }
}
