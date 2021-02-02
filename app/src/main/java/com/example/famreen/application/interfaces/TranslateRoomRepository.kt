package com.example.famreen.application.interfaces

import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem

interface TranslateRoomRepository : ObservableBasic{
    fun insertTranslate(item: TranslateItem?)
    fun deleteAllTranslates()
    fun deleteTranslate(item: TranslateItem?)
    fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?)
    fun insertAllTranslates(list: List<TranslateItem>?)
}