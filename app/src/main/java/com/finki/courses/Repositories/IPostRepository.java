package com.finki.courses.Repositories;

import com.finki.courses.Fragments.FragmentHelpers.FragmentGalleryHelper;
import com.finki.courses.Fragments.FragmentHelpers.FragmentUserHelper;
import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;
import com.finki.courses.databinding.FragmentGalleryBinding;
import com.finki.courses.databinding.FragmentUserBinding;

import java.io.InputStream;
import java.util.List;

public interface IPostRepository {
    void add(long categoryId, Post post);
    void uploadImage(Category category, InputStream inputStream);
    void deleteById(long categoryId, long postId);
    void listAllForGallery(FragmentGalleryHelper fragmentGalleryHelper);
    void listAllForUser(FragmentUserHelper fragmentUserHelper);
}
