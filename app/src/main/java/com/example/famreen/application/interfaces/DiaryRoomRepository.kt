package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.disposables.Disposable

interface DiaryRoomRepository : ObservableBasic{
    fun insertNote(item: NoteItem?): Disposable?
    fun deleteNote(item: NoteItem?): Disposable?
    fun deleteAllNotes(): Disposable?
    fun deleteAllNotes(list: List<Int>?): Disposable?
    fun insertAllNotes(list: List<NoteItem>?): Disposable?
    fun getNotes(observer: ItemObserver<List<NoteItem>?>): Disposable?
}