package com.example.famreen.application.room.dao

import androidx.room.*
import com.example.famreen.firebase.db.User
import io.reactivex.Single

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User?)

    @Delete
    fun delete(user: User?)

    @Query("SELECT * FROM user WHERE id = :id")
    operator fun get(id: Int?): Single<User?>?

    @Query("DELETE FROM user WHERE id = :id")
    fun deleteById(id: Int?)
}