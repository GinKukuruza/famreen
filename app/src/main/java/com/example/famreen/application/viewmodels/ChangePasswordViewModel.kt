package com.example.famreen.application.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.exceptions.LoginException
import com.example.famreen.application.interfaces.SuccessListener
import com.example.famreen.application.interfaces.UserRepository
import com.example.famreen.application.logging.Logger
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set

class ChangePasswordViewModel(private val mUserRepository: UserRepository) {
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
        Logger.log(Log.ERROR,"network change user password exception",e)
        mState.set(States.ErrorState(ex.mMessage))
    }
    /**
     * Основная функция изменения пароля
     * **/
    @Throws(java.lang.NullPointerException::class,java.lang.IllegalArgumentException::class)
    fun changePassword(email:String,oldPassword: String, newPassword: String) {
        mState.set(States.LoadingState())
        if (authCheck(email,oldPassword, newPassword)) {
            mUserRepository.changePassword(email, oldPassword, newPassword,object : SuccessListener{
                override fun onSuccess() {
                    mState.set(States.ErrorState("Вы успешно изменили пароль"))
                    mState.set(States.DefaultState())
                }
                override fun onError(e: Exception) {
                    catchException(e)
                }

            })
        }
    }
    /**
     * **/
    fun getState() = mState
}