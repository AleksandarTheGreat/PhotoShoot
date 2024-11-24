package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;

import java.util.List;

public interface ICategoriesRepository {

    void listAll();
    Category findCategoryByName(String name);
    void add(String name);
    void delete(String name);
}
