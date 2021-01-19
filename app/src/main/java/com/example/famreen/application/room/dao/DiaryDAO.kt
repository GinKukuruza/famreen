package com.example.famreen.application.room.dao

import androidx.room.*
import com.example.famreen.application.items.NoteItem
import io.reactivex.Single

@Dao
interface DiaryDAO {
    @Query("SELECT * FROM NoteItem WHERE id = :id")
    fun getById(id: Long?): Single<NoteItem>?

    @get:Query("SELECT * FROM NoteItem")
    val all: Single<List<NoteItem>?>?

    @Query("DELETE FROM NoteItem")
    fun deleteAll()

    @Query("DELETE FROM NoteItem WHERE id in (:notes)")
    fun deleteAll(notes: List<Int?>?)

    @Insert
    fun insert(noteItem: NoteItem?): Single<Long?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<NoteItem>?)

    @Delete
    fun delete(noteItem: NoteItem?)
}