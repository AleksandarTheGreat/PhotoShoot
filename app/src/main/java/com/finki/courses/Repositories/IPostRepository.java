package com.finki.courses.Repositories;

import com.finki.courses.Fragments.FragmentHelpers.FragmentGalleryHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.Repositories.Callbacks.Post.OnPostAddedCallback;
import com.finki.courses.Repositories.Callbacks.Post.OnPostDeletedCallback;
import com.finki.courses.databinding.FragmentGalleryBinding;
import com.finki.courses.databinding.FragmentUserBinding;

import java.io.InputStream;
import java.util.List;

public interface IPostRepository {
    void add(long categoryId, Post post, OnPostAddedCallback onPostAddedCallback);
    void uploadImage(Category category, InputStream inputStream, OnPostAddedCallback onPostAddedCallback);
    void deleteById(long categoryId, long postId, OnPostDeletedCallback onPostDeletedCallback);
    void listAllForGallery(FragmentGalleryHelper fragmentGalleryHelper);
    void listAllForUser(FragmentUserHelper fragmentUserHelper);
}
