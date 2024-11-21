package com.finki.courses.Model;

import java.time.LocalDateTime;

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


}
