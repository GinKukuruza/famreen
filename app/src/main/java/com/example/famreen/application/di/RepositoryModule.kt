package com.example.famreen.application.di

import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.firebase.repositories.DiaryRepository
import com.example.famreen.firebase.repositories.TranslateRepository
import com.example.famreen.firebase.repositories.UserRepository
import com.example.famreen.translateApi.repositories.YandexTranslateRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideDiaryRepository(): DiaryRepository {
        return DiaryRepository()
    }
    @Provides
    fun provideTranslateRepository(): TranslateRepository {
        return TranslateRepository()
    }
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }
    @Provides
    fun provideYandexTranslateRepository(translateRoomRepository: TranslateRoomRepository): YandexTranslateRepository {
        return YandexTranslateRepository(translateRoomRepository = translateRoomRepository)
    }
}