package com.example.finalproject;

import android.graphics.Bitmap;

public class CheckItem {
    Bitmap photo;
    String tags, date;
    boolean checked;
    CheckItem(Bitmap photo, String tags, String date, boolean checked) {
        this.photo = photo;
        this.tags = tags;
        this.date = date;
        this.checked = checked;
    }
}
