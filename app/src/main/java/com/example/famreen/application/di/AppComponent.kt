package com.example.famreen.application.di

import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.DiaryAdapter
import com.example.famreen.application.adapters.TranslateAdapter
import com.example.famreen.application.fragments.*
import com.example.famreen.application.screens.DefaultScreen
import com.example.famreen.application.screens.DiaryScreen
import com.example.famreen.application.screens.SearchScreen
import com.example.famreen.application.screens.TranslationScreen
import com.example.famreen.application.viewmodels.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class, RoomModule::class, ModelsModule::class])
@Singleton
interface AppComponent {
    //Fragments
    fun inject(fragment: DiaryFragment)
    fun inject(fragment: TranslateFragment)
    fun inject(fragment: RegistrationFragment)
    fun inject(fragment: ChangePasswordFragment)
    fun inject(fragment: DevConnectionFragment)
    fun inject(fragment: DialogTextFontFragment)
    fun inject(fragment: DialogTextSizeFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: MainFragment)
    fun inject(fragment: PreferencesFragment)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: AboutAppFragment)
    //Activity
    fun inject(activity: MainActivity)
    //App
    fun inject(application: App)
    //Adapter
    fun inject(adapter: TranslateAdapter)
    fun inject(adapter: DiaryAdapter)
    //Screens
    fun inject(screen: DefaultScreen)
    fun inject(screen: DiaryScreen)
    fun inject(screen: TranslationScreen)
    fun inject(screen: SearchScreen)
    //viewModels
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: ChangePasswordViewModel)
    fun inject(viewModel: RegistrationViewModel)
    fun inject(viewModel: DialogTextSizeViewModel)
    fun inject(viewModel: DialogTextFontViewModel)
}