package com.example.finalproject;

public class CommentItem {
    int photo;
    String name, tags, timestamp;
    CommentItem(int photo, String name, String tags, String timestamp) {
        this.photo = photo;
        this.name = name;
        this.tags = tags;
        this.timestamp = timestamp;
    }
}
