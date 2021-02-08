package com.example.famreen.application.interfaces

import com.example.famreen.translateApi.gson.TranslateResp

interface YandexTranslateRepository {
    /**
     * Вызывается для загрузки текущих поддерживаемых языков для перевода
     * **/
    fun setUpLanguages()
    /**
     * Вызывается для перевода текста
     * **/
    fun translate(text: String, language: String, listener: ItemListener<TranslateResp>)
}