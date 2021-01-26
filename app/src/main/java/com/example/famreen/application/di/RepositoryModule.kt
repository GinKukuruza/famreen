package com.example.famreen.application.di

import com.example.famreen.application.interfaces.*
import com.example.famreen.firebase.repositories.DiaryRepositoryImpl
import com.example.famreen.firebase.repositories.TranslateRepositoryImpl
import com.example.famreen.firebase.repositories.UserRepositoryImpl
import com.example.famreen.translateApi.repositories.YandexTranslateRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideDiaryRepository(): DiaryRepositoryImpl {
        return DiaryRepositoryImpl()
    }
    @Provides
    fun provideTranslateRepository(): TranslateRepositoryImpl {
        return TranslateRepositoryImpl()
    }
    @Provides
    fun provideUserRepository(): UserRepositoryImpl {
        return UserRepositoryImpl()
    }
    @Provides
    fun provideYandexTranslateRepository(translateRoomRepositoryImpl: TranslateRoomRepository): YandexTranslateRepositoryImpl {
        return YandexTranslateRepositoryImpl(translateRoomRepositoryImpl = translateRoomRepositoryImpl)
    }
}