package com.example.famreen.application.interfaces

interface MainUIUpdater {
    /**
     * Обновляет UI в зависимости от пользователя
     * Параметром передается пользователь
     * Рабочие варианты типов пользователя: User, FirebaseUser, UninitializedUser, EmptyUser
     * **/
    fun <T>updateUI(user: T)
    /**
     * Используется для выхода из аккаунта
     * **/
    fun exit()
}