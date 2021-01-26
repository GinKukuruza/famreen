package com.example.famreen.application.interfaces

interface DBProvider {
    /**
     * Должна удалять все данные на локальном устройстве, очищать все репозитории и натсройки(preferences)
     * **/
    fun deleteAll(translateRoomRepositoryImpl: TranslateRoomRepository, diaryRoomRepositoryImpl: DiaryRoomRepository, userRoomRepositoryImpl: UserRoomRepository)
}