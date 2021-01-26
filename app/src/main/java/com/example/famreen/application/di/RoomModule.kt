package com.example.famreen.application.di

import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.room.repositories.DiaryRoomRepositoryImpl
import com.example.famreen.application.room.repositories.TranslateRoomRepositoryImpl
import com.example.famreen.application.room.repositories.UserRoomRepositoryImpl
import com.example.famreen.firebase.repositories.DiaryRepositoryImpl
import com.example.famreen.firebase.repositories.TranslateRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RoomModule {
    @Provides
    fun provideDiaryRoomRepository(diaryRepositoryImpl: DiaryRepositoryImpl): DiaryRoomRepository {
        return DiaryRoomRepositoryImpl(diaryRepositoryImpl = diaryRepositoryImpl)
    }
    @Provides
    fun provideTranslateRoomRepository(translateRepositoryImpl: TranslateRepositoryImpl): TranslateRoomRepository {
        return TranslateRoomRepositoryImpl(translateRepositoryImpl = translateRepositoryImpl)
    }
    @Provides
    fun provideUserRoomRepository(): UserRoomRepository {
        return UserRoomRepositoryImpl()
    }
}