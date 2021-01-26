package com.example.famreen.application.interfaces

import com.example.famreen.translateApi.gson.TranslateResp
import com.example.famreen.utils.observers.ItemObserver

interface YandexTranslateRepository {
    /**
     * Вызывается для загрузки текущих поддерживаемых языков для перевода
     * **/
    fun setUpLanguages()
    /**
     * Вызывается для перевода текста
     * **/
    fun translate(text: String, language: String,observer: ItemObserver<TranslateResp>)
}