package com.example.famreen.application.viewmodels

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.services.MainService
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.example.famreen.utils.observers.ItemObserver

class MainActivityViewModel(private val mUserRoomRepositoryImpl: UserRoomRepository) {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())

    /**
     * Вызывается что бы получить текущего пользователя и отправить его в mState
     * **/
    fun prepareUser(){
        mUserRoomRepositoryImpl.getUser(object : ItemObserver<User> {
            override fun getItem(item: User) {
                FirebaseConnection.setUser(item)
                mState.set(States.UserState(item))
            }
        })
    }
    /**
     * Запускает основной сервис со всеми жкранами (см. Screens)
     * **/
    fun startService(){
        val intentService = Intent(App.getAppContext(), MainService::class.java)
        App.getAppContext().startService(intentService)
    }
    /**
     * **/
    fun getState() = mState
}