package com.finki.courses.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String name;
    private List<Post> postList;

    public Category(){};
    public Category(String name){
        this.name = name;
        this.postList = new ArrayList<>();
    }

    public Category(String name, List<Post> postList){
        this.name = name;
        this.postList = postList;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s", name, postList.toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
}
