package com.example.famreen.translateApi

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TranslateConnection private constructor() {
    val api: TranslateAPI
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://translate.yandex.net")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    companion object {
        private var connProvider: TranslateConnection? = null
        @JvmStatic
        fun createConnection(): TranslateConnection? {
            if (connProvider != null) return connProvider
            connProvider = TranslateConnection()
            return connProvider
        }
    }

    init {
        api = retrofit.create(TranslateAPI::class.java)
    }
}