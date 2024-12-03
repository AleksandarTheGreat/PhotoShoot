package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;
import com.finki.courses.Model.Post;

import java.io.InputStream;

public interface IPostRepository {
    void add(long categoryId, Post post);
    void deleteById(long categoryId, long postId);

    void uploadImage(Category category, InputStream inputStream);
}
