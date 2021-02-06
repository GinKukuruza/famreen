package com.example.famreen.application.room.repositories

import android.util.Log
import com.example.famreen.application.interfaces.SubjectRoom
import com.example.famreen.application.interfaces.TranslateRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.network.TranslateSubject
import com.example.famreen.application.room.DBConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.RoomStates
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TranslateRoomRepositoryImpl(val translateRepositoryImpl: TranslateRepository) : TranslateRoomRepository {
    private val mRoomSubject: SubjectRoom = TranslateSubject()

    @Throws(NullPointerException::class)
    override fun insertTranslate(item: TranslateItem?): Disposable {
        if(item == null) throw NullPointerException("translate item is null")
        val dbConnection = DBConnection.getDbConnection()
        val dispose = object : DisposableSingleObserver<Long?>() {
            override fun onSuccess(aLong: Long) {
                insertTranslateById(aLong)
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onInsert(false)
            }
        }
        dbConnection?.translateDAO?.insert(item)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(dispose)
        return dispose
    }

    override fun deleteAllTranslates(): Disposable {
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mRoomSubject.onDelete(true)
                if (FirebaseProvider.userIsLogIn())
                    translateRepositoryImpl.deleteAllTranslates()
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onDelete(false)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.deleteAll()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(dispose)
        return dispose
    }

    @Throws(NullPointerException::class)
    override fun deleteTranslate(item: TranslateItem?): Disposable {
        if(item == null) throw NullPointerException("translate item is null")
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mRoomSubject.onDelete(true)
                if (FirebaseProvider.userIsLogIn())
                    translateRepositoryImpl.deleteTranslate(item.id)
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onDelete(false)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.delete(item)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(dispose)
        return dispose
    }

    @Throws(NullPointerException::class)
    override fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?): Disposable {
        if(list == null) throw NullPointerException("languages list is null")
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mRoomSubject.onInsert(true)
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onInsert(false)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.translateDAO.insertAll(list)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(dispose)
        return dispose
    }

    @Throws(NullPointerException::class)
    override fun insertAllTranslates(list: List<TranslateItem>?): Disposable {
        if(list == null) throw NullPointerException("list of translate items is null")
        val dispose = object : DisposableCompletableObserver() {
            override fun onComplete() {
                mRoomSubject.onInsert(true)
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onInsert(false)
            }
        }
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.translateDAO?.insertAllTranslates(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(dispose)
        return dispose
    }

    override fun getTranslates(listener: ItemObserver<List<TranslateItem>?>): Disposable? {
        val dbConnection = DBConnection.getDbConnection()
        val dispose = object : DisposableSingleObserver<List<TranslateItem>?>() {
            override fun onSuccess(list: List<TranslateItem>) {
                listener.getItem(list)
            }
            override fun onError(e: Throwable) {
                listener.onFailure("Impossible to get data")
                Logger.log(Log.ERROR, "local translate db exception", e)
            }
        }
        dbConnection!!.translateDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(dispose)
        return dispose
    }

    override fun subscribe(observer: Observer<RoomStates>){
        mRoomSubject.subscribe(observer = observer)
    }
    override fun unsubscribe(observer: Observer<RoomStates>){
        mRoomSubject.unsubscribe(observer = observer)
    }

    @Throws(NullPointerException::class)
    private fun insertTranslateById(id: Long?): Disposable {
        if(id == null) throw NullPointerException("translate id is null")
        val dbConnection = DBConnection.getDbConnection()
        val dispose = object : DisposableSingleObserver<TranslateItem?>() {
            override fun onSuccess(translateItem: TranslateItem) {
                if (FirebaseProvider.userIsLogIn()){
                    mRoomSubject.onInsert(true)
                    translateRepositoryImpl.addTranslate(translateItem)
                }else mRoomSubject.onInsert(false)
            }

            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "local translate db exception", e)
                mRoomSubject.onInsert(false)
            }
        }
        dbConnection!!.translateDAO.getById(id)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(dispose)
        return dispose
    }
}