package com.finki.courses.Repositories.Callbacks.Category;

import com.finki.courses.Model.Category;

import java.util.List;

public interface OnCategoriesLoadedCallBack {
    void onLoaded(List<Category> categories);
}
