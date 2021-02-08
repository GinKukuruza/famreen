package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem
import io.reactivex.disposables.Disposable

interface DiaryRoomRepository : ObservableBasic{
    fun insertNote(item: NoteItem?): Disposable?
    fun deleteNote(item: NoteItem?): Disposable?
    fun deleteAllNotes(): Disposable?
    fun deleteAllNotes(list: List<Int>?): Disposable?
    fun insertAllNotes(list: List<NoteItem>?): Disposable?
    fun getNotes(listener: ItemListener<List<NoteItem>?>): Disposable?
}