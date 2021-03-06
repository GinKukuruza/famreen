package com.example.famreen.application.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.application.exceptions.RegistrationException
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.interfaces.SuccessListener
import com.example.famreen.application.interfaces.UserRepository
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.logging.Logger
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.User
import com.example.famreen.states.States
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class RegistrationViewModel(private val mUserRepositoryImpl: UserRepository,
                            private val mUserRoomRepositoryImpl: UserRoomRepository) {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private val mDisposables = CompositeDisposable()
    init {
        App.appComponent.inject(this@RegistrationViewModel)
    }

    private fun getUser() {
        val d = mUserRoomRepositoryImpl.getUser(object : CallbackListener<User> {
            override fun onItem(s: ItemStates.ItemState<User>) {
                mState.set(States.UserState(s.item))
            }

            override fun onFailure(state: ThrowableStates) {
                val msg = (state as ThrowableStates.ErrorStates).msg
                mState.set(States.ErrorState(msg))
            }

        })
        d?.let {
            addDisposable(it)
        }
    }

    private fun checkFields(email: String?, password: String?,name: String?): Boolean {
        return if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            mState.set(States.ErrorState("Пустые поля"))
            //TODO добавить проверок
            false
        } else true
    }
    private fun checkPassword(password: String?,passwordToCheck: String?): Boolean{
        if(TextUtils.isEmpty(password) && TextUtils.isEmpty(passwordToCheck)) {
            mState.set(States.ErrorState("Некорректно введен пароль"))
            return false
        }
        if(password!! != passwordToCheck){
            mState.set(States.ErrorState("Пароли не совпадают"))
            return false
        }
        return true
    }
    private fun catchException(ex: Exception?) {
        Logger.log(Log.ERROR,"comparator parse data exception",ex)
        val msg = RegistrationException(ex).message
        mState.set(States.ErrorState(msg))
    }
    /**
     * Вызывается для прохождения регистрации через email и password(основной метод)
     * **/
    fun signUp(email: String?,password: String?,passwordToCheck: String?, name: String?,imageUri: String?) {
        if (!FirebaseProvider.exit()) return
        if(!checkFields(email,password, name)) return
        if(!checkPassword(password, passwordToCheck)) return
        mState.set(States.LoadingState())
        mUserRepositoryImpl.signUp(email as String,password as String,name as String,imageUri,object : SuccessListener{
            override fun onSuccess() {
                mState.set(States.DefaultState())
                getUser()
            }

            override fun onError(e: Exception) {
                catchException(e)
            }

        })
    }
    /**
     * **/
    fun getState() = mState
    /**
     * Окончательно высвобождает ресурсы при полном завершении работы фрагмента, вызывается в onDestroy()
     * **/
    fun release(){
        mDisposables.dispose()
    }
    /**
     * Очищает временные ресурсы, вызывается в onDestroyView()
     * **/
    fun clear(){
        mDisposables.clear()
    }
    /**
     * **/
    fun addDisposable(disposable: Disposable){
        mDisposables.add(disposable)
    }
}