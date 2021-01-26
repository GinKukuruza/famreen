package com.example.famreen.application.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.application.exceptions.LoginException
import com.example.famreen.application.logging.Logger
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.google.firebase.auth.EmailAuthProvider
import java.lang.Exception

class ChangePasswordViewModel {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())

    private fun authCheck(email:String,oldPassword: String, newPassword: String): Boolean {
        return if (TextUtils.isEmpty(email)
            || TextUtils.isEmpty(oldPassword)
            || TextUtils.isEmpty(newPassword)) {
            mState.set(States.ErrorState("Пустые поля"))
            //TODO Добавить больше полей проверки
            false
        } else true
    }

    private fun catchException(e: Exception?){
        val ex = LoginException(e)
        Logger.log(2,"network change user password exception",e)
        mState.set(States.ErrorState(ex.mMessage))
    }
    /**
     * Основная функция изменения пароля
     * **/
    @Throws(java.lang.NullPointerException::class,java.lang.IllegalArgumentException::class)
    fun changePassword(email:String,oldPassword: String, newPassword: String) {
        val firebaseUser = FirebaseConnection.firebaseAuth?.currentUser ?: throw NullPointerException("User is null")
        if (!firebaseUser.isEmailVerified) throw java.lang.IllegalArgumentException("Wrong auth type of the user")
        mState.set(States.LoadingState())
        if (authCheck(email,oldPassword, newPassword)) {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            firebaseUser.reauthenticate(credential)
                .addOnSuccessListener {
                    firebaseUser.updatePassword(newPassword)
                        .addOnSuccessListener {
                            mState.set(States.ErrorState("Вы успешно изменили пароль"))
                            mState.set(States.DefaultState())
                        }
                        .addOnFailureListener {
                            catchException(it)
                        }
                }
                .addOnFailureListener {
                    catchException(it)
                }
        }
    }
    /**
     * **/
    fun getState() = mState
}