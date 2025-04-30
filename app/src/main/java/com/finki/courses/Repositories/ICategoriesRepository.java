package com.finki.courses.Repositories;

import com.finki.courses.Model.Category;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoriesLoadedCallBack;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoryAddedCallback;
import com.finki.courses.Repositories.Callbacks.Category.OnCategoryDeletedCallback;

public interface ICategoriesRepository {

    void listAll(OnCategoriesLoadedCallBack onCategoriesLoadedCallBack);
    Category findCategoryById(long id);
    void add(String name, OnCategoryAddedCallback onCategoryAddedCallback);
    void deleteById(long id, OnCategoryDeletedCallback onCategoryDeletedCallback);
}
