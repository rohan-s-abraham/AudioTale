package com.example.audiotale;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newTypeface;

    public CustomTypefaceSpan(String family, Typeface typeface) {
        super(family);
        newTypeface = typeface;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        applyCustomTypeFace(textPaint, newTypeface);
    }

    @Override
    public void updateMeasureState(TextPaint textPaint) {
        applyCustomTypeFace(textPaint, newTypeface);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface typeface) {
        paint.setTypeface(typeface);
    }
}

