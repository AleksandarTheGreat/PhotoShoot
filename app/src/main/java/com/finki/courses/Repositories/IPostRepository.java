package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;

import java.io.InputStream;
import java.util.List;

public interface IPostRepository {
    void add(long categoryId, Post post);
    void uploadImage(Category category, InputStream inputStream);
    void deleteById(long categoryId, long postId);
    void listAllForGallery();
    void listAllForUser();
}
