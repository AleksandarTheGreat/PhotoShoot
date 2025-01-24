package com.finki.courses.Repositories;

import com.finki.courses.Model.FeedPost;
import com.finki.courses.Model.Post;
import com.finki.courses.databinding.FragmentFeedBinding;

public interface IFeedPostRepository {

    void listAll(FragmentFeedBinding fragmentFeedBinding);
    void addFeedPostToDocument(FeedPost feedPost, String documentName);
    void uploadPostToStorage(FeedPost feedPost);
    void delete(FeedPost feedPost);

}
