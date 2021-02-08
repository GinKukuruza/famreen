package com.example.famreen.application.room.repositories

import android.util.Log
import com.example.famreen.application.interfaces.ItemListener
import com.example.famreen.application.interfaces.SubjectRoom
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.network.UserSubject
import com.example.famreen.application.room.DBConnection
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import com.example.famreen.states.RoomStates
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class UserRoomRepositoryImpl : UserRoomRepository{
    private val mUserSubject: SubjectRoom = UserSubject()

    @Throws(NullPointerException::class)
    override fun insertUser(user: User?, listener: ItemListener<Any>): Disposable {
        if(user == null) throw java.lang.NullPointerException("user is null")
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mUserSubject.onInsert(true)
                listener.getItem(true)
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local user db exception", e)
                mUserSubject.onInsert(false)
                listener.getItem(e)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.userDAO?.insert(user)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(dispose)
        return dispose
    }

    override fun getUser(listener: ItemListener<User>): Disposable{
        val dbConnection = DBConnection.getDbConnection()
        val dispose = object : DisposableSingleObserver<User?>() {
            override fun onSuccess(user: User) {
                mUserSubject.onInsert(true)
                listener.getItem(user)
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local user db exception", e)
                listener.onFailure("Impossible to get data")
                mUserSubject.onInsert(false)
            }
        }
        dbConnection!!.userDAO[FirebaseConnection.CURRENT_USER]
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(dispose)
        return dispose
    }

    @Throws(NullPointerException::class)
    override fun deleteUserById(id: Int?): Disposable {
        if(id == null) throw java.lang.NullPointerException("id is null")
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mUserSubject.onDelete(true)
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local user db exception", e)
                mUserSubject.onInsert(false)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.userDAO?.deleteById(id)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(dispose)
        return dispose
    }

    override fun subscribe(observer: Observer<RoomStates>) {
        mUserSubject.subscribe(observer)
    }

    override fun unsubscribe(observer: Observer<RoomStates>) {
        mUserSubject.unsubscribe(observer)
    }

}