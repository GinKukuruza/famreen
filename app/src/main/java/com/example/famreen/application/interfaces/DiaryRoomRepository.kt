package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem
import com.example.famreen.states.RoomStates
import io.reactivex.Observer

interface DiaryRoomRepository {
    fun insertNote(item: NoteItem?)
    fun deleteNote(item: NoteItem?)
    fun deleteAllNotes()
    fun deleteAllNotes(list: List<Int>?)
    fun insertAllNotes(list: List<NoteItem>?)
    fun subscribe(observer: Observer<RoomStates>)
    fun unsubscribe(observer: Observer<RoomStates>)
}