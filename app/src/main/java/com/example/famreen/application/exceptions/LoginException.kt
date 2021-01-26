package com.example.famreen.application.exceptions

import android.util.Log
import com.example.famreen.application.logging.Logger
import com.firebase.ui.auth.util.FirebaseAuthError
import com.google.firebase.auth.FirebaseAuthException

/**
 * Класс управляет обработкой исключение, связанных с входом в учетную запись через Firebase
 * **/
class LoginException(e: Exception?) {
    var mMessage: String = "Unexpected error"
    private var mException: java.lang.Exception? = null
    init {
        mException = e
        catch(e)
    }
    private fun catch(e: Exception?){
        if(e == null) return
        Logger.log(Log.ERROR,"network login exception",e)
        if(e is FirebaseAuthException){
            when(e.errorCode){
                FirebaseAuthError.ERROR_CREDENTIAL_ALREADY_IN_USE.name ->  mMessage = "Данный аккаунт уже зарегестрирован"
                FirebaseAuthError.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL.name -> mMessage = "Данный email уже зарегестрирован"
                FirebaseAuthError.ERROR_INVALID_CREDENTIAL.name ->   mMessage =  "Не действительные данные"
                FirebaseAuthError.ERROR_USER_DISABLED.name -> mMessage = "Пользователь был удален"
                FirebaseAuthError.ERROR_USER_NOT_FOUND.name -> mMessage =  "Пользователь не найден"
                FirebaseAuthError.ERROR_INVALID_USER_TOKEN.name -> mMessage = "Не действительный токен"
            }
        }
        mException?.message.let { mMessage = it.toString() }
    }
}