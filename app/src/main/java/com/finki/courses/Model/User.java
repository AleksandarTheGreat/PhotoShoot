package com.finki.courses.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {

    private String email;
    private String imageUrl;

    private List<Category> categoryList;
    public User(){}
    public User(String email, String imageUrl, List<Category> categoryList){
        this.email = email;
        this.imageUrl = imageUrl;
        this.categoryList = categoryList;
    }

    public User(String email, String imageUrl) {
        this.email = email;
        this.imageUrl = imageUrl;
        this.categoryList = new ArrayList<>();
    }

    public User(String email){
        this.email = email;
        this.imageUrl = "";
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s %s", email, imageUrl, categoryList.toString());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

}
