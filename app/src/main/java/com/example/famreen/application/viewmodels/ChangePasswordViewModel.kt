package com.example.famreen.application.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.application.exceptions.LoginException
import com.example.famreen.application.logging.Logger
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.utils.default
import com.example.famreen.utils.set
import com.google.firebase.auth.EmailAuthProvider
import java.lang.Exception

class ChangePasswordViewModel {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun changePassword(email:String,oldPassword: String, newPassword: String) {
        val firebaseUser = FirebaseConnection.firebaseAuth?.currentUser ?: throw NullPointerException("User is null")
        if (!firebaseUser.isEmailVerified) throw java.lang.IllegalArgumentException("Wrong auth type of the user")
            if (authCheck(email,oldPassword, newPassword)) {
                val credential = EmailAuthProvider.getCredential(email, oldPassword)
                firebaseUser.reauthenticate(credential)
                    .addOnSuccessListener {
                        firebaseUser.updatePassword(newPassword)
                            .addOnSuccessListener {
                                state.set(States.ErrorState("Вы успешно изменили пароль"))
                                state.set(States.DefaultState())
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

    private fun authCheck(email:String,oldPassword: String, newPassword: String): Boolean {
        return if (TextUtils.isEmpty(email)
            || TextUtils.isEmpty(oldPassword)
            || TextUtils.isEmpty(newPassword)) {
            state.set(States.ErrorState("Пустые поля"))
            //TODO Добавить больше полей проверки
            false
        } else true
    }

    private fun catchException(e: Exception?){
        val ex = LoginException(e)
        Logger.log(2,"network change user password exception",e)
        state.set(States.ErrorState(ex.message))
    }
}