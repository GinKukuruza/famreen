package com.example.famreen.application.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider


class TranslateTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        setTextSize()
        setTextColor()
        setTextFont()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setTextSize()
        setTextColor()
        setTextFont()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTextSize()
        setTextColor()
        setTextFont()
    }

    private fun setTextSize() {
        setTextSize(TypedValue.COMPLEX_UNIT_PT, getProvider()!!.readTranslateTextSize().toFloat())
    }

    private fun setTextColor() {
        setTextColor(getProvider()!!.readTranslateTextColor())
    }

    private fun setTextFont() {
        val typeface = ResourcesCompat.getFont(context, getProvider()!!.readTranslateTextFont())
        setTypeface(typeface)
    }
}