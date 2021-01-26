package com.example.famreen.application.interfaces

import com.example.famreen.firebase.db.User
import com.example.famreen.utils.observers.ItemObserver

interface UserRoomRepository {
    fun insertUser(user: User?, observer: ItemObserver<Any>)
    fun getUser(observer: ItemObserver<User>)
    fun deleteUserById(id: Int?)
}