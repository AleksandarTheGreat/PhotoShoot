package com.finki.courses.Repositories.Callbacks;

public interface OnCategoryDeletedCallback {
    void onCategoryDeleted(boolean deletedSuccessfully, long id);
}
