package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.famreen.R
import com.example.famreen.states.States
import com.example.famreen.application.items.TextFontItem
import com.example.famreen.utils.extensions.default
import kotlin.collections.ArrayList

class DialogTextFontViewModel {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun getFonts(): List<TextFontItem>{
        val list: MutableList<TextFontItem> = ArrayList()
        val listt = listOf(
            TextFontItem("Andika", R.drawable.font_andika, R.font.andika)
            ,TextFontItem("Actor", R.drawable.font_actor, R.font.actor)
            ,TextFontItem("Atomic Age", R.drawable.font_atomic_age, R.font.atomic_age)
        )
        list.add(TextFontItem("Andika", R.drawable.font_andika, R.font.andika))
        list.add(TextFontItem("Actor", R.drawable.font_actor, R.font.actor))
        list.add(TextFontItem("Atomic Age", R.drawable.font_atomic_age, R.font.atomic_age))
        list.add(TextFontItem("Kranky", R.drawable.font_kranky, R.font.kranky))
        list.add(TextFontItem("Anonymous Pro", R.drawable.font_anonymous_pro, R.font.anonymous_pro))
        list.add(TextFontItem("Antic Didone", R.drawable.font_antic_didone, R.font.antic_didone))
        return listt
    }
}