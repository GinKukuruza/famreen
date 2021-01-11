package com.example.famreen.application.di

import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.application.viewmodels.*
import com.example.famreen.network.UserRepository
import dagger.Module
import dagger.Provides

@Module
class ModelsModule {

    @Provides
    fun provideDiaryViewModel(diaryRoomRepository: DiaryRoomRepository): DiaryViewModel{
        return DiaryViewModel(diaryRoomRepository = diaryRoomRepository)
    }
    @Provides
    fun provideTranslateViewModel(diaryRoomRepository: DiaryRoomRepository,translateRoomRepository: TranslateRoomRepository): TranslateViewModel{
        return TranslateViewModel(diaryRoomRepository = diaryRoomRepository,translateRoomRepository = translateRoomRepository)
    }
    @Provides
    fun provideRegisterViewModel(userRepository: UserRepository): RegisterViewModel{
        return RegisterViewModel(userRepository = userRepository)
    }
    @Provides
    fun provideLoginViewModel(userRepository: UserRepository,userRoomRepository: UserRoomRepository
                              ,translateRoomRepository: TranslateRoomRepository,diaryRoomRepository: DiaryRoomRepository): LoginViewModel{
        return LoginViewModel(userRepository = userRepository,userRoomRepository = userRoomRepository
            ,translateRoomRepository = translateRoomRepository,diaryRoomRepository = diaryRoomRepository)
    }
    @Provides
    fun provideMainActivityViewModel(userRoomRepository: UserRoomRepository): MainActivityViewModel{
        return MainActivityViewModel(userRoomRepository = userRoomRepository)
    }
}