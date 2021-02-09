package com.example.famreen.translateApi.repositories

import android.util.Log
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.YandexTranslateRepository
import com.example.famreen.application.logging.Logger
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.translateApi.ServiceGenerator
import com.example.famreen.translateApi.TranslateAPI
import com.example.famreen.translateApi.gson.TranslateLangs
import com.example.famreen.translateApi.gson.TranslateResp
import com.example.famreen.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class YandexTranslateRepositoryImpl(private val translateRoomRepositoryImpl: TranslateRoomRepository) : YandexTranslateRepository{
    override fun setUpLanguages(): Disposable{
        //Format: "en"
        val lang = Locale.getDefault().language
        val single = ServiceGenerator.createService(TranslateAPI::class.java).getLanguages(lang)!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        return single.subscribeWith<DisposableSingleObserver<TranslateLangs?>>(object : DisposableSingleObserver<TranslateLangs?>() {
            override fun onSuccess(langs: TranslateLangs) {
                if (langs.mLangs != null) {
                    translateRoomRepositoryImpl.insertAllLanguages(Utils.initLanguages(langs.mLangs))
                }
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "network translate exception", e)
            }
        })
    }
    override fun translate(text: String, language: String, listener: CallbackListener<TranslateResp>): Disposable{
        val single = ServiceGenerator.createService(TranslateAPI::class.java).getTranslate(text, language)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        return single.subscribeWith(object : DisposableSingleObserver<TranslateResp?>() {
                    override fun onSuccess(translateResp: TranslateResp) {
                        listener.onItem(ItemStates.ItemState(translateResp))
                    }

                    override fun onError(e: Throwable) {
                        Logger.log(Log.ERROR, "network translate and local db exception", e)
                    }
                })
    }
}