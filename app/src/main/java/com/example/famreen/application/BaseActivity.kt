package com.example.famreen.application

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(){
    /**
     * Обновляет UI в зависимости от пользователя
     * Параметром передается пользователь
     * Рабочие варианты типов пользователя: User, FirebaseUser, UninitializedUser, EmptyUser
     * **/
    abstract fun <T>updateUI(user: T)
    /**
     * Используется для выхода из аккаунта
     * **/
    abstract fun exit()
}