package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;

import java.util.List;

public interface ICategoriesRepository {

    void listAll();
    Category findCategoryById(long id);
    void add(String name);
    void deleteById(long id);
}
