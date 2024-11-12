package com.example.audiotale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {

    // Method to convert byte array to Bitmap
    public static Bitmap getImage(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
