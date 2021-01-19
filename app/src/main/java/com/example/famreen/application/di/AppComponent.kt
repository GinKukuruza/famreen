package com.example.famreen.application.di

import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.fragments.DiaryFragment
import com.example.famreen.application.fragments.LoginFragment
import com.example.famreen.application.fragments.RegistrationFragment
import com.example.famreen.application.fragments.TranslateFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class, RoomModule::class, ModelsModule::class])
@Singleton
interface AppComponent {
    //Fragments
    fun inject(fragment: DiaryFragment)
    fun inject(fragment: TranslateFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: LoginFragment)
    //Activity
    fun inject(activity: MainActivity)
    //App
    fun inject(application: App)
}