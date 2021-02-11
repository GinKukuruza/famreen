package com.example.famreen.application.viewmodels

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.services.MainService
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import com.example.famreen.states.States
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainActivityViewModel(private val mUserRoomRepositoryImpl: UserRoomRepository) {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private val mDisposables = CompositeDisposable()
    /**
     * Вызывается что бы получить текущего пользователя и отправить его в mState
     * **/
    fun prepareUser(){
        val d = mUserRoomRepositoryImpl.getUser(object : CallbackListener<User> {
            override fun onItem(s: ItemStates.ItemState<User>) {
                FirebaseConnection.setUser(s.item)
                mState.set(States.UserState(s.item))
            }

            override fun onFailure(state: ThrowableStates) {
                val msg = (state as ThrowableStates.ErrorStates).msg
                mState.set(States.ErrorState(msg))
            }
        })
        d.let {
            addDisposable(it)
        }
    }
    /**
     * Запускает основной сервис со всеми жкранами (см. Screens)
     * **/
    fun startService(){
        val intentService = Intent(App.getAppContext(), MainService::class.java)
        ContextCompat.startForegroundService(App.getAppContext(), intentService)
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
     * Очищает временные ресурсы
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