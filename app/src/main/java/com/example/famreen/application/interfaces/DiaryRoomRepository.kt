package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem

interface DiaryRoomRepository : ObservableBasic{
    fun insertNote(item: NoteItem?)
    fun deleteNote(item: NoteItem?)
    fun deleteAllNotes()
    fun deleteAllNotes(list: List<Int>?)
    fun insertAllNotes(list: List<NoteItem>?)
}