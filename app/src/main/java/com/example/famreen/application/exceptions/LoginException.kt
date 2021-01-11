package com.example.famreen.application.exceptions

import com.firebase.ui.auth.util.FirebaseAuthError
import com.google.firebase.auth.FirebaseAuthException


class LoginException(e: Exception?) {
    var message: String = "Unexpected error"
    var exception: java.lang.Exception? = null
    init {
        exception = e
        catch(e)
    }
    private fun catch(e: Exception?){
        if(e == null) return
        if(e is FirebaseAuthException){
            when(e.errorCode){
                FirebaseAuthError.ERROR_CREDENTIAL_ALREADY_IN_USE.name ->  message = "Данный аккаунт уже зарегестрирован"
                FirebaseAuthError.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL.name -> message = "Данный email уже зарегестрирован"
                FirebaseAuthError.ERROR_INVALID_CREDENTIAL.name ->   message =  "Не действительные данные"
                FirebaseAuthError.ERROR_USER_DISABLED.name -> message = "Пользователь был удален"
                FirebaseAuthError.ERROR_USER_NOT_FOUND.name -> message =  "Пользователь не найден"
                FirebaseAuthError.ERROR_INVALID_USER_TOKEN.name -> message = "Не действительный токен"
            }
        }
        exception?.message.let { message = it.toString() }
    }
}