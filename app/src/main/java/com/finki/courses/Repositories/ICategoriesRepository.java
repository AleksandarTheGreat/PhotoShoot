package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;
import com.finki.courses.Repositories.Callbacks.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.OnCategoryAddedCallback;
import com.finki.courses.Repositories.Callbacks.OnCategoryDeletedCallback;

import java.util.List;

public interface ICategoriesRepository {

    void listAll(OnCategoriesLoadedCallBack onCategoriesLoadedCallBack);
    Category findCategoryById(long id);
    void add(String name, OnCategoryAddedCallback onCategoryAddedCallback);
    void deleteById(long id, OnCategoryDeletedCallback onCategoryDeletedCallback);
}
