package com.example.famreen.application.interfaces

import com.example.famreen.firebase.db.User
import com.example.famreen.utils.observers.ItemObserver
import com.google.firebase.auth.AuthResult

interface UserRepository {
    /**
     * Вызывается при регистрации нового пользователя
     * **/
    fun addUser(result: AuthResult, user: User)
    /**
     * Вызывается для получения данных текущего пользователя
     * **/
    fun getUser(userRoomRepositoryImpl: UserRoomRepository, observer: ItemObserver<Any>)
    /**
     * Берет из локального хранилища данные и сохраняет в firebase, при условии если пользователь зарегестрировался впервые и уже успел создать данные
     * **/
    fun saveNewUserData(diaryRepositoryImpl: DiaryRepository, translateRepositoryImpl: TranslateRepository)
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
}