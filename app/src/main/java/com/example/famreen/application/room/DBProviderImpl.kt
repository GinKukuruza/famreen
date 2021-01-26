package com.example.famreen.application.room

import com.example.famreen.application.interfaces.DBProvider
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.firebase.FirebaseConnection

object DBProviderImpl : DBProvider {

    override fun deleteAll(translateRoomRepositoryImpl: TranslateRoomRepository, diaryRoomRepositoryImpl: DiaryRoomRepository, userRoomRepositoryImpl: UserRoomRepository) {
        diaryRoomRepositoryImpl.deleteAllNotes()
        translateRoomRepositoryImpl.deleteAllTranslates()
        userRoomRepositoryImpl.deleteUserById(FirebaseConnection.CURRENT_USER)
        AppPreferences.getProvider()?.deleteAll()
    }
}