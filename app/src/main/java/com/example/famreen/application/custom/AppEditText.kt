package com.example.famreen.application.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider

class AppEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        setTextFont(context)
    }

    private fun setTextFont(context: Context) {
        val typeface = ResourcesCompat.getFont(context, getProvider()!!.readAppTextFont())
        setTypeface(typeface)
    }
}