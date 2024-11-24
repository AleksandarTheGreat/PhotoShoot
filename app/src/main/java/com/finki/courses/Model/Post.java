package com.finki.courses.Model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {

    private String name;
    private LocalDateTime postedAt;
    private String imageUrl;

    public Post(){}
    public Post(String name, LocalDateTime postedAt, String imageUrl) {
        this.name = name;
        this.postedAt = postedAt;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s %s", name, nicelyFormattedDateTime(), imageUrl);
    }

    @SuppressLint("NewApi")
    public String nicelyFormattedDateTime(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' hh:mm").format(postedAt);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
