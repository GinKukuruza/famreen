package com.example.famreen.application.room.repositories

import android.util.Log
import com.example.famreen.application.interfaces.TranslateRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.network.TranslateObserver
import com.example.famreen.application.room.DBConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.RoomStates
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TranslateRoomRepositoryImpl(val translateRepositoryImpl: TranslateRepository) : TranslateRoomRepository {
    private val mRoomObserver: TranslateObserver = TranslateObserver()

    @Throws(NullPointerException::class)
    override fun insertTranslate(item: TranslateItem?) {
        if(item == null) throw NullPointerException("translate item is null")
        val disposables = CompositeDisposable()
        val dbConnection = DBConnection.getDbConnection()
        disposables.add(dbConnection?.translateDAO?.insert(item)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
        !!.subscribeWith(object : DisposableSingleObserver<Long?>() {
                override fun onSuccess(aLong: Long) {
                    disposables.add(
                        dbConnection.translateDAO.getById(aLong)
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribeOn(Schedulers.io())
                        !!.subscribeWith(object : DisposableSingleObserver<TranslateItem?>() {
                                override fun onSuccess(translateItem: TranslateItem) {
                                    mRoomObserver.onInsert(true)
                                    if (FirebaseProvider.userIsLogIn())
                                        translateRepositoryImpl.addTranslate(translateItem)
                                    disposables.clear()
                                    disposables.dispose()
                                }

                                override fun onError(e: Throwable) {
                                    Logger.log(Log.ERROR, "local translate db exception", e)
                                    mRoomObserver.onInsert(false)
                                    disposables.clear()
                                    disposables.dispose()
                                }
                            }))
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomObserver.onInsert(false)
                    disposables.clear()
                    disposables.dispose()
                }
            }))
    }

    override fun deleteAllTranslates() {
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.deleteAll()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    mRoomObserver.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        translateRepositoryImpl.deleteAllTranslates()
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomObserver.onDelete(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun deleteTranslate(item: TranslateItem?) {
        if(item == null) throw NullPointerException("translate item is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.delete(item)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    mRoomObserver.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        translateRepositoryImpl.deleteTranslate(item.id)
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomObserver.onDelete(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?) {
        if(list == null) throw NullPointerException("languages list is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.insertAll(list)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun insertAllTranslates(list: List<TranslateItem>?) {
        if(list == null) throw NullPointerException("list of translate items is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.insertAllTranslates(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    mRoomObserver.onInsert(true)
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomObserver.onInsert(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }

    override fun subscribe(observer: Observer<RoomStates>){
        mRoomObserver.subscribe(observer = observer)
    }
    override fun unsubscribe(observer: Observer<RoomStates>){
        mRoomObserver.unsubscribe(observer = observer)
    }
}