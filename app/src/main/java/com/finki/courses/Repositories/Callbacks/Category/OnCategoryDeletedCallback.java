package com.finki.courses.Repositories.Callbacks.Category;

public interface OnCategoryDeletedCallback {
    void onCategoryDeleted(boolean deletedSuccessfully, long id);
}
