package com.finki.courses.Repositories;

import com.finki.courses.Model.Post;

public interface IPostRepository {
    void add(String category, Post post);
    void delete();
}
