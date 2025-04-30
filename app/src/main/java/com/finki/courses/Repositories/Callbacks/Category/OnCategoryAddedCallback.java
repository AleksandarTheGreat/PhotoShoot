package com.finki.courses.Repositories.Callbacks.Category;

import com.finki.courses.Model.Category;

public interface OnCategoryAddedCallback {
    void onCategoryAdded(boolean addedSuccessfully, Category category);
}
