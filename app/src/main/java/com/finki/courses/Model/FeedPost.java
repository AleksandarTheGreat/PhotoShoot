package com.finki.courses.Model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FeedPost {

    private long id;
    private String email;
    private String imageUrl;
    private String fileLocation;
    private Set<String> setLikes;
    private List<String> listComments;
    private LocalDateTime postedAt;

    // This shall be used for uploading a single feed post
    @SuppressLint("NewApi")
    public FeedPost(String email, String imageUrl) {
        this.id = UUID.randomUUID().getLeastSignificantBits() * -1;
        this.email = email;
        this.imageUrl = imageUrl;
        this.setLikes = new HashSet<>();
        this.listComments = new ArrayList<>();
        this.postedAt = LocalDateTime.now();
    }

    // This shall be used when reading all feed posts
    public FeedPost(long id, String email, String imageUrl, String fileLocation, Set<String> setLikes, List<String> listComments, LocalDateTime postedAt) {
        this.id = id;
        this.email = email;
        this.imageUrl = imageUrl;
        this.fileLocation = fileLocation;
        this.setLikes = setLikes;
        this.listComments = listComments;
        this.postedAt = postedAt;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("Id: %d\nEmail: %s", id, email);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (getClass() != obj.getClass())
            return false;
        return id == ((FeedPost) obj).id;
    }

    public int totalLikes(){
        return setLikes == null? 0 : setLikes.size();
    }

    public int totalComments(){
        return listComments == null? 0 : listComments.size();
    }

    @SuppressLint("NewApi")
    public List<String> likesSetToList(){
        return setLikes.stream().toList();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Set<String> getSetLikes() {
        return setLikes;
    }

    public void setSetLikes(Set<String> setLikes) {
        this.setLikes = setLikes;
    }

    public List<String> getListComments() {
        return listComments;
    }

    public void setListComments(List<String> listComments) {
        this.listComments = listComments;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }
}
