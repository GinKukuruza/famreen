package com.example.famreen.translateApi.repositories

import com.example.famreen.application.logging.Logger
import com.example.famreen.utils.observers.ItemObserver
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.translateApi.TranslateConnection
import com.example.famreen.translateApi.gson.TranslateLangs
import com.example.famreen.translateApi.gson.TranslateResp
import com.example.famreen.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class YandexTranslateRepository(private val translateRoomRepository: TranslateRoomRepository) {
    fun setUpLanguages(){
        val disposables = CompositeDisposable()
        //Format: "en"
        val lang = Locale.getDefault().language
        disposables.add(
            TranslateConnection.createConnection()!!.api.getLangs(lang)!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<TranslateLangs?>() {
                override fun onSuccess(langs: TranslateLangs) {
                    if (langs.langs != null) {
                        translateRoomRepository.insertAllLanguages(Utils.initLanguages(langs.langs))
                    }
                    disposables.clear()
                    disposables.dispose()
                }
                override fun onError(e: Throwable) {
                    Logger.log(9, "network translate exception", e)
                    disposables.clear()
                    disposables.dispose()
                }
            }))
    }
    fun translate(text: String, language: String,observer: ItemObserver<TranslateResp>){
        val disposables = CompositeDisposable()
        disposables.add(
            TranslateConnection.createConnection()!!.api.getTranslate(text, language)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TranslateResp?>() {
                    override fun onSuccess(translateResp: TranslateResp) {
                        observer.getItem(translateResp)
                        disposables.clear()
                        disposables.dispose()
                    }

                    override fun onError(e: Throwable) {
                        Logger.log(9, "network translate and local db exception", e)
                        disposables.clear()
                        disposables.dispose()
                    }
                }))
    }
}