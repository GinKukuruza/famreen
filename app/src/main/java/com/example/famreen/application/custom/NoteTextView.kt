package com.example.famreen.application.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider


class NoteTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        setTextSize(context)
        setTextColor(context)
        setTextFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setTextSize(context)
        setTextColor(context)
        setTextFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTextSize(context)
        setTextColor(context)
        setTextFont(context)
    }

    private fun setTextSize(context: Context) {
        setTextSize(TypedValue.COMPLEX_UNIT_PT, getProvider()!!.readNoteTextSize().toFloat())
    }

    private fun setTextColor(context: Context) {
        setTextColor(getProvider()!!.readNoteTextColor())
    }

    private fun setTextFont(context: Context) {
        val typeface = ResourcesCompat.getFont(context, getProvider()!!.readNoteTextFont())
        setTypeface(typeface)
    }
}