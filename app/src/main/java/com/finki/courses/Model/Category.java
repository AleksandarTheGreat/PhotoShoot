package com.finki.courses.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Category {

    private long id;
    private String name;
    private List<Map<String, Object>> postList;

    public Category(){};
    public Category(String name){
        this.name = name;
        this.postList = new ArrayList<>();
    }

    public Category(String name, List<Map<String, Object>> postList){
        this.name = name;
        this.postList = postList;
    }

    public Category(long id, String name, List<Map<String, Object>> postList) {
        this.id = id;
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

    public long getId() {
        return id;
    }

    public List<Map<String, Object>> getPostList() {
        return postList;
    }

    public void setPostList(List<Map<String, Object>> postList) {
        this.postList = postList;
    }
}
