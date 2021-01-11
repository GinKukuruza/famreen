package com.example.famreen.application.room

import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.FirebaseConnection

object DBProvider {

    fun deleteAll(translateRoomRepository: TranslateRoomRepository,diaryRoomRepository: DiaryRoomRepository,userRoomRepository: UserRoomRepository) {
        diaryRoomRepository.deleteAllNotes()
        translateRoomRepository.deleteAllTranslates()
        userRoomRepository.deleteUserById(FirebaseConnection.CURRENT_USER)
        AppPreferences.getProvider()?.deleteAll()
    }
}