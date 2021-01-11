package com.example.famreen.translate

import com.example.famreen.translate.gson.TranslateLangs
import com.example.famreen.translate.gson.TranslateResp
import com.example.famreen.translate.gson.TranslateWhatIsLang
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslateAPI {
    @POST("/api/v1.5/tr.json/translate?key=trnsl.1.1.20200112T171902Z.e5b74a7b9eefa16c.b1373eed727f22f96db0f0ce0935f1a2c1be17bb")
    fun getTranslate(@Query("text") text: String?, @Query("lang") lang: String?): Single<TranslateResp?>?

    @POST("/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20200112T171902Z.e5b74a7b9eefa16c.b1373eed727f22f96db0f0ce0935f1a2c1be17bb")
    fun getLangs(@Query("ui") ui: String?): Single<TranslateLangs?>?

    @POST("/api/v1.5/tr.json/detect?key=trnsl.1.1.20200112T171902Z.e5b74a7b9eefa16c.b1373eed727f22f96db0f0ce0935f1a2c1be17bb")
    fun whatIsLang(@Query("text") text: String?): Single<TranslateWhatIsLang?>?
}