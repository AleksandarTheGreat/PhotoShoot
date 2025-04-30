package com.finki.courses.Repositories.Callbacks.Post;

import com.finki.courses.Model.Post;

public interface OnPostAddedCallback {
    void onPostAdded(long categoryId, Post post, boolean addedSuccessfully);
}
