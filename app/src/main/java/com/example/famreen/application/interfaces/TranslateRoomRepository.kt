package com.example.famreen.application.interfaces

import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.states.RoomStates
import io.reactivex.Observer

interface TranslateRoomRepository {
    fun insertTranslate(item: TranslateItem?)
    fun deleteAllTranslates()
    fun deleteTranslate(item: TranslateItem?)
    fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?)
    fun insertAllTranslates(list: List<TranslateItem>?)
    fun subscribe(observer: Observer<RoomStates>)
    fun unsubscribe(observer: Observer<RoomStates>)
}