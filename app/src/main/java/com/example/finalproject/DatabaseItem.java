package com.example.finalproject;

import android.graphics.Bitmap;

public class DatabaseItem {
    Bitmap photo;
    String name, tags;
    DatabaseItem(Bitmap photo, String name, String tags) {
        this.photo = photo;
        this.name = name;
        this.tags = tags;
    }
}
