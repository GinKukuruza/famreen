package com.example.famreen.application.exceptions

import android.util.Log
import com.example.famreen.application.logging.Logger
import com.firebase.ui.auth.util.FirebaseAuthError
import com.google.firebase.auth.FirebaseAuthException

/**
 * Класс управляет обработкой исключений, связанных с регистрацией через Firebase
 * **/
class RegistrationException(e: Exception?) {
    var message: String = "Unexpected error"
    var exception: java.lang.Exception? = null
    init {
        exception = e
        catch(e)
    }
    private fun catch(e: Exception?){
        if(e == null) return
        Logger.log(Log.ERROR,"network registration exception",e)
        if(e is FirebaseAuthException){
            when(e.errorCode){
                FirebaseAuthError.ERROR_EMAIL_ALREADY_IN_USE.name -> {
                    message = "Такой email уже зарегестрирован"
                }
                FirebaseAuthError.ERROR_INVALID_EMAIL.name -> {
                    message = "Неверная форма email"
                }
                FirebaseAuthError.ERROR_WEAK_PASSWORD.name -> {
                    message = "Слишком слабый пароль"
                }
            }
        }
        exception!!.message.let { message = it.toString() }
    }
}