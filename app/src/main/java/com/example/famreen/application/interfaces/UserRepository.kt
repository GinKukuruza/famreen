package com.example.famreen.application.interfaces

import com.example.famreen.firebase.db.User
import com.google.firebase.auth.AuthResult
import io.reactivex.disposables.Disposable

interface UserRepository {
    /**
     * Вызывается при регистрации нового пользователя
     * **/
    fun addUser(result: AuthResult, user: User)
    /**
     * Вызывается для получения данных текущего пользователя
     * **/
    fun getUser(userRoomRepositoryImpl: UserRoomRepository, listener: CallbackListener<Boolean>)
    /**
     * Берет из локального хранилища данные и сохраняет в firebase, при условии если пользователь зарегестрировался впервые и уже успел создать данные
     * **/
    fun saveNewUserData(diaryRepositoryImpl: DiaryRepository, translateRepositoryImpl: TranslateRepository): List<Disposable>?
    /**
     * Создается новый пользователь
     * Вызывается каждый раз, когда пользователь входит в аккаунт
     * **/
    fun createUser(name: String, email: String, imageUri: String?): User
    /**
     *Добавляется доп информация о пользователе, если он зашел через google , github etc
     * **/
    fun addOAuthUser(result: AuthResult)
    /**
     * Вызывается когда пользователь входит в учетную запись и его сохраненные данные загружаются из firebase
     * **/
    fun getAndSetValues(result: AuthResult, translateRoomRepositoryImpl: TranslateRoomRepository, diaryRoomRepositoryImpl: DiaryRoomRepository)
    /**
     * Изменение пароля текущего пользователя
     * */
    fun changePassword(email:String,oldPassword: String, newPassword: String, listener: SuccessListener)

    fun signUp(email: String,password: String, name: String,imageUri: String?,listener: SuccessListener)
}