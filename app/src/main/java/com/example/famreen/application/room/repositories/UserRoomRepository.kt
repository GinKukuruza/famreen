package com.example.famreen.application.room.repositories

import com.example.famreen.application.logging.Logger
import com.example.famreen.application.room.DBConnection
import com.example.famreen.utils.observers.ItemObserver
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class UserRoomRepository {
    @Throws(NullPointerException::class)
    fun insertUser(user: User?,observer: ItemObserver<Any>) {
        if(user == null) throw java.lang.NullPointerException("user is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.userDAO?.insert(user)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }
                override fun onComplete() {
                    observer.getItem(true)
                    disposables.clear()
                    disposables.dispose()
                }
                override fun onError(e: Throwable) {
                    Logger.log(9, "local user db exception", e)
                    observer.getItem(e)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    fun getUser(observer: ItemObserver<User>){
        val dbConnection = DBConnection.getDbConnection()
        dbConnection!!.userDAO[FirebaseConnection.CURRENT_USER]
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableSingleObserver<User?>() {
                override fun onSuccess(user: User) {
                    observer.getItem(user)
                }
                override fun onError(e: Throwable) {
                    Logger.log(9, "local user db exception", e)
                }
            })
    }
    @Throws(NullPointerException::class)
    fun deleteUserById(id: Int?) {
        if(id == null) throw java.lang.NullPointerException("id is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.userDAO?.deleteById(id)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "local user db exception", e)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }

}