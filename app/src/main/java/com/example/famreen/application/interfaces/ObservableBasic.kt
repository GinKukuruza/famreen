package com.example.famreen.application.interfaces

import com.example.famreen.states.RoomStates
import io.reactivex.Observer

interface ObservableBasic {
    fun subscribe(observer: Observer<RoomStates>)
    fun unsubscribe(observer: Observer<RoomStates>)
}