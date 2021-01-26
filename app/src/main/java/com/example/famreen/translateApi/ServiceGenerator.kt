package com.example.famreen.translateApi

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator{
    private val API_BASE_URL = "https://translate.yandex.net"
    private val mHttpClient = okhttp3.OkHttpClient.Builder()
    private val mRetrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    private var mRetrofit = mRetrofitBuilder.build()
    fun <T> createService(clazz: Class<T>) : T{
        mRetrofitBuilder.client(mHttpClient.build())
        mRetrofit = mRetrofitBuilder.build()
        return mRetrofit.create(clazz)
    }
}