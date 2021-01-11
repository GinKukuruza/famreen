package com.example.famreen.application.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider

class AppTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        setTextSize(context)
        setTextFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setTextSize(context)
        setTextFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTextSize(context)
        setTextFont(context)
    }

    private fun setTextSize(context: Context) {
        setTextSize(TypedValue.COMPLEX_UNIT_PT, getProvider()!!.readAppTextSize().toFloat())
    }

    private fun setTextFont(context: Context) {
        val typeface = ResourcesCompat.getFont(context, getProvider()!!.readAppTextFont())
        setTypeface(typeface)
    }
}