package com.example.famreen.application.interfaces

import com.example.famreen.states.RoomStates
import io.reactivex.Observer

interface SubjectRoom {
    fun subscribe(observer: Observer<RoomStates>)
    fun unsubscribe(observer: Observer<RoomStates>)
    fun onInsert(isSuccess: Boolean)
    fun onDelete(isDeleted: Boolean)
}