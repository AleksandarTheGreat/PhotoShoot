package com.finki.courses.Repositories.Callbacks.Category;

import java.util.List;
import java.util.Map;

public interface OnPostsListedCallback {
    void onListed(List<Map<String, Object>> list);
}
