package com.example.famreen.translateApi

import com.example.famreen.translateApi.gson.TranslateLangs
import com.example.famreen.translateApi.gson.TranslateResp
import com.example.famreen.translateApi.gson.TranslateWhatIsLang
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslateAPI {
    @POST("/api/v1.5/tr.json/translate?key=get your own key")
    fun getTranslate(@Query("text") text: String?, @Query("lang") lang: String?): Single<TranslateResp?>?

    @POST("/api/v1.5/tr.json/getLangs?key=get your own key")
    fun getLanguages(@Query("ui") ui: String?): Single<TranslateLangs?>?

    @POST("/api/v1.5/tr.json/detect?key=get your own key")
    fun whatIsLang(@Query("text") text: String?): Single<TranslateWhatIsLang?>?
}