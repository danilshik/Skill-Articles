package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop

fun View.setMarginOptionally(left:Int = marginStart, top : Int = marginTop, right : Int = marginEnd, bottom : Int = marginBottom) {
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.apply {
        leftMargin = left
        topMargin = top
        rightMargin = right
        bottomMargin = bottom
    }
    requestLayout()
}