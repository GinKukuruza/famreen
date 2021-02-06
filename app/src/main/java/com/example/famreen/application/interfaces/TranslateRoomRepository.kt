package com.example.famreen.application.interfaces

import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.disposables.Disposable

interface TranslateRoomRepository : ObservableBasic{
    fun insertTranslate(item: TranslateItem?): Disposable?
    fun deleteAllTranslates(): Disposable?
    fun deleteTranslate(item: TranslateItem?): Disposable?
    fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?): Disposable?
    fun insertAllTranslates(list: List<TranslateItem>?): Disposable?
    fun getTranslates(listener: ItemObserver<List<TranslateItem>?>): Disposable?
}