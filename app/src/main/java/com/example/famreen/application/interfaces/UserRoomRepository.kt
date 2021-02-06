package com.example.famreen.application.interfaces

import com.example.famreen.firebase.db.User
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.disposables.Disposable

interface UserRoomRepository: ObservableBasic{
    fun insertUser(user: User?, observer: ItemObserver<Any>): Disposable?
    fun getUser(observer: ItemObserver<User>): Disposable?
    fun deleteUserById(id: Int?): Disposable?
}