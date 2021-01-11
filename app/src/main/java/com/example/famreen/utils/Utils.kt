package com.example.famreen.utils

import android.content.Context
import androidx.navigation.NavOptions
import com.example.famreen.R
import com.example.famreen.application.App
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getNoteTime(): String {
        val calendar = Calendar.getInstance()
        val datePattern = getStringFromResourcesByName(App.getAppContext(), R.string.diary_time_pattern)
        val format = SimpleDateFormat(datePattern, Locale.getDefault())
        return format.format(calendar.time)
    }
    fun getStringFromResourcesByName(context: Context, strId: Int): String {
        return context.resources.getString(strId)
    }
    fun getDefaultNavigationOptions(): NavOptions{
        return NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_swipe_to_right)
            .setExitAnim(R.anim.fragment_swipe_to_left)
            .setPopEnterAnim(R.anim.fragment_swipe_to_right)
            .setPopExitAnim(R.anim.fragment_swipe_to_left)
            .build()
    }
}