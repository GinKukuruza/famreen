package com.example.famreen.application.di

import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.UserRepository
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.viewmodels.*
import dagger.Module
import dagger.Provides

@Module
class ModelsModule {

    @Provides
    fun provideDiaryViewModel(diaryRoomRepositoryImpl: DiaryRoomRepository): DiaryViewModel{
        return DiaryViewModel(mDiaryRoomRepositoryImpl = diaryRoomRepositoryImpl)
    }
    @Provides
    fun provideTranslateViewModel(diaryRoomRepositoryImpl: DiaryRoomRepository
                                  ,translateRoomRepositoryImpl: TranslateRoomRepository): TranslateViewModel{
        return TranslateViewModel(mDiaryRoomRepositoryImpl = diaryRoomRepositoryImpl
            ,mTranslateRoomRepositoryImpl = translateRoomRepositoryImpl)
    }
    @Provides
    fun provideRegisterViewModel(userRepositoryImpl: UserRepository): RegistrationViewModel{
        return RegistrationViewModel(mUserRepositoryImpl = userRepositoryImpl)
    }
    @Provides
    fun provideLoginViewModel(userRepositoryImpl: UserRepository
                              , userRoomRepositoryImpl: UserRoomRepository
                              , translateRoomRepositoryImpl: TranslateRoomRepository
                              , diaryRoomRepositoryImpl: DiaryRoomRepository): LoginViewModel{
        return LoginViewModel(mUserRepositoryImpl = userRepositoryImpl
            ,mUserRoomRepositoryImpl = userRoomRepositoryImpl
            ,mTranslateRoomRepositoryImpl = translateRoomRepositoryImpl
            ,mDiaryRoomRepositoryImpl = diaryRoomRepositoryImpl)
    }
    @Provides
    fun provideMainActivityViewModel(userRoomRepositoryImpl: UserRoomRepository): MainActivityViewModel{
        return MainActivityViewModel(mUserRoomRepositoryImpl = userRoomRepositoryImpl)
    }
}