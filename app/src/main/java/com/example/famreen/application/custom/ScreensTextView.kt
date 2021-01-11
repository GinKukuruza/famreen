package com.example.famreen.application.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider

class ScreensTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setTextColor()
        setTextFont()
    }

    private fun setTextColor() {
        setTextColor(getProvider()!!.readScreensTextColor())
    }

    private fun setTextFont() {
        val typeface = ResourcesCompat.getFont(context, getProvider()!!.readAppTextFont())
        setTypeface(typeface)
    }
}