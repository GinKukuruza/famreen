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
    fun provideRegisterViewModel(userRepositoryImpl: UserRepository,userRoomRepositoryImpl: UserRoomRepository): RegistrationViewModel{
        return RegistrationViewModel(mUserRepositoryImpl = userRepositoryImpl,mUserRoomRepositoryImpl = userRoomRepositoryImpl)
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
    @Provides
    fun provideChangePasswordViewModel(userRepositoryImpl: UserRepository): ChangePasswordViewModel{
        return ChangePasswordViewModel(mUserRepository = userRepositoryImpl)
    }
    @Provides
    fun provideAboutAppViewModel(): AboutAppViewModel{
        return AboutAppViewModel()
    }
    @Provides
    fun provideDevConnectionViewModel(): DevConnectionViewModel{
        return DevConnectionViewModel()
    }
    @Provides
    fun provideDialogTextFontViewModel(): DialogTextFontViewModel{
        return DialogTextFontViewModel()
    }
    @Provides
    fun provideDialogTextSizeViewModel(): DialogTextSizeViewModel{
        return DialogTextSizeViewModel()
    }
    @Provides
    fun provideMainViewModel(): MainViewModel{
        return MainViewModel()
    }
    @Provides
    fun providePreferencesViewModel(): PreferencesViewModel{
        return PreferencesViewModel()
    }
    @Provides
    fun provideSearchViewModel(): SearchViewModel{
        return SearchViewModel()
    }
}