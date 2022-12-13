package com.example.triangles

import android.graphics.Paint
import android.graphics.Rect

fun calculateTextWidth(paint: Paint, text: String): Float = paint.measureText(text)

fun calculateTextHeight(paint: Paint, text: String): Float {
    val mTextBoundRect = Rect()
    paint.getTextBounds(text, 0, text.length, mTextBoundRect)
    return mTextBoundRect.height().toFloat()
}