package com.example.famreen.application.di

import com.example.famreen.firebase.FirebaseProvider
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {
    @Provides
    fun provideFirebaseProvider() = FirebaseProvider()
}