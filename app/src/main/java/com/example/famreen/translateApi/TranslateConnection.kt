package com.example.famreen.translateApi

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TranslateConnection private constructor() {
    val api: TranslateAPI
    private val retrofit: Retrofit

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
        retrofit = Retrofit.Builder()
                .baseUrl("https://translate.yandex.net")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        api = retrofit.create(TranslateAPI::class.java)
    }
}