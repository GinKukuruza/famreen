package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import com.example.famreen.utils.default
import com.example.famreen.utils.set

class MainActivityViewModel(private val userRoomRepository: UserRoomRepository) {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun prepareUser(){
        userRoomRepository.getUser(object : ItemObserver<User> {
            override fun getItem(item: User) {
                FirebaseConnection.user = item
                state.set(States.UserState(item))
            }
        })
    }
}