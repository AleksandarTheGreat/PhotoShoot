package com.finki.courses.Repositories;

import com.finki.courses.Model.Post;

import java.io.InputStream;

public interface IPostRepository {
    void add(String categoryName, Post post);
    void delete();

    void uploadImage(String categoryName, InputStream inputStream);
}
