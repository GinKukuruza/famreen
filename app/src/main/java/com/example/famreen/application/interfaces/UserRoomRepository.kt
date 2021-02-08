package com.example.famreen.application.interfaces

import com.example.famreen.firebase.db.User
import io.reactivex.disposables.Disposable

interface UserRoomRepository: ObservableBasic{
    fun insertUser(user: User?, listener: ItemListener<Any>): Disposable?
    fun getUser(listener: ItemListener<User>): Disposable?
    fun deleteUserById(id: Int?): Disposable?
}