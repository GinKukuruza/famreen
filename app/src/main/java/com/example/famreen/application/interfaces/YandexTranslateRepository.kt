package com.example.famreen.application.interfaces

import com.example.famreen.translateApi.gson.TranslateResp
import io.reactivex.disposables.Disposable

interface YandexTranslateRepository {
    /**
     * Вызывается для загрузки текущих поддерживаемых языков для перевода
     * **/
    fun setUpLanguages(): Disposable
    /**
     * Вызывается для перевода текста
     * **/
    fun translate(text: String, language: String, listener: CallbackListener<TranslateResp>): Disposable
}