package com.example.famreen.application.di

import com.example.famreen.network.DiaryRepository
import com.example.famreen.network.TranslateRepository
import com.example.famreen.network.UserRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideDiaryRepository(): DiaryRepository{
        return DiaryRepository()
    }
    @Provides
    fun provideTranslateRepository(): TranslateRepository{
        return TranslateRepository()
    }
    @Provides
    fun provideUserRepository(): UserRepository{
        return UserRepository()
    }
}