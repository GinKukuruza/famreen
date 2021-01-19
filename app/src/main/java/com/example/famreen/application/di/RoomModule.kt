package com.example.famreen.application.di

import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.repositories.DiaryRepository
import com.example.famreen.firebase.repositories.TranslateRepository
import dagger.Module
import dagger.Provides

@Module
class RoomModule {
    @Provides
    fun provideDiaryRoomRepository(diaryRepository: DiaryRepository): DiaryRoomRepository {
        return DiaryRoomRepository(diaryRepository = diaryRepository)
    }
    @Provides
    fun provideTranslateRoomRepository(translateRepository: TranslateRepository): TranslateRoomRepository {
        return TranslateRoomRepository(translateRepository = translateRepository)
    }
    @Provides
    fun provideUserRoomRepository(): UserRoomRepository {
        return UserRoomRepository()
    }
}