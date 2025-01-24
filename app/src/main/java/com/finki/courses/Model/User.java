package com.finki.courses.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {

    private String email;
    private String profilePhotoUrl;
    private String coverPhotoUrl;
    private String nickname;
    private String bio;

    private List<Category> categoryList;
    public User(){}
    public User(String email, String profilePhotoUrl, String coverPhotoUrl, List<Category> categoryList){
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
        this.coverPhotoUrl = coverPhotoUrl;
        this.categoryList = categoryList;
    }

    public User(String email, String profilePhotoUrl, String coverPhotoUrl) {
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;
        this.coverPhotoUrl = coverPhotoUrl;
        this.categoryList = new ArrayList<>();
    }

    public User(String email){
        this.email = email;
        this.profilePhotoUrl = "";
        this.coverPhotoUrl = "";
        this.nickname = "";
        this.bio = "";
        this.categoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s", email, categoryList.toString());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
