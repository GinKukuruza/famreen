package com.example.famreen.application.di

import com.example.famreen.application.interfaces.*
import com.example.famreen.application.room.repositories.DiaryRoomRepositoryImpl
import com.example.famreen.application.room.repositories.TranslateRoomRepositoryImpl
import com.example.famreen.application.room.repositories.UserRoomRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RoomModule {
    @Provides
    fun provideDiaryRoomRepository(diaryRepositoryImpl: DiaryRepository): DiaryRoomRepository {
        return DiaryRoomRepositoryImpl(diaryRepositoryImpl = diaryRepositoryImpl)
    }
    @Provides
    fun provideTranslateRoomRepository(translateRepositoryImpl: TranslateRepository): TranslateRoomRepository {
        return TranslateRoomRepositoryImpl(translateRepositoryImpl = translateRepositoryImpl)
    }
    @Provides
    fun provideUserRoomRepository(): UserRoomRepository {
        return UserRoomRepositoryImpl()
    }
}