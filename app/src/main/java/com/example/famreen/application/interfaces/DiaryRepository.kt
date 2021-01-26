package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem

interface DiaryRepository {
    fun deleteNotes(list: List<Int>?)
    fun deleteAllNotes()
    fun addNote(item: NoteItem?)
    fun addAllNotes(list: List<NoteItem>?)
    fun deleteNote(id: Int?)
}