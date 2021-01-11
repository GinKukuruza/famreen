package com.example.famreen.application.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.famreen.application.App
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.firebase.db.User

@Database(entities = [NoteItem::class, ScreenSpinnerTranslateItem::class, User::class, TranslateItem::class], version = 1, exportSchema = false)
abstract class DBConnection : RoomDatabase() {
    abstract val diaryDAO: DiaryDAO
    abstract val translateDAO: TranslateDAO
    abstract val userDAO: UserDAO

    companion object {
        private var mDBConnection: DBConnection? = null
        @JvmStatic
        fun getDbConnection(): DBConnection? {
            if (mDBConnection == null) synchronized(DBConnection::class.java) {
                if (mDBConnection == null) mDBConnection = Room.databaseBuilder(App.getAppContext(), DBConnection::class.java, "localDB").build()
            }
            return mDBConnection
        }
    }
}