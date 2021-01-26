package com.example.famreen.application.interfaces

import com.example.famreen.application.items.TranslateItem

interface TranslateRepository {
    fun addTranslate(item: TranslateItem?)
    fun addAllTranslates(items: List<TranslateItem>?)
    fun deleteTranslate(id: Int?)
    fun deleteAllTranslates()
}