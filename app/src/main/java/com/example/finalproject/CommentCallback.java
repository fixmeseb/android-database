package com.example.finalproject;

public interface CommentCallback {
    void onSuccess(String text);
    void onFailure(Exception e);
}
