package com.example.famreen.application.room

import androidx.room.*
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import io.reactivex.Single

@Dao
interface TranslateDAO {
    @Query("SELECT * FROM TranslateItem WHERE id = :id")
    fun getById(id: Long): Single<TranslateItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ScreenSpinnerTranslateItem>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: TranslateItem): Single<Long?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTranslates(items: List<TranslateItem>?)

    @Delete
    fun delete(item: TranslateItem)

    @get:Query("SELECT * FROM TranslateItem")
    val all: Single<List<TranslateItem>?>?

    @get:Query("SELECT * FROM screenspinnertranslateitem")
    val allLangs: Single<List<ScreenSpinnerTranslateItem>?>?

    @Query("DELETE FROM TranslateItem")
    fun deleteAll()
}