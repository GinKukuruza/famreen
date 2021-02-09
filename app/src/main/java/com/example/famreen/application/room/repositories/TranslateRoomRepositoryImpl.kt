package com.example.famreen.application.room.repositories

import android.util.Log
import com.example.famreen.application.interfaces.CallbackListener
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
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates
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
        val single = dbConnection!!.translateDAO.insert(item)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
        return single!!.subscribeWith<DisposableSingleObserver<Long?>>(object : DisposableSingleObserver<Long?>() {
                override fun onSuccess(aLong: Long) {
                    insertTranslateById(aLong)
                    mRoomSubject.onInsert(true)
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }

    override fun deleteAllTranslates(): Disposable {
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.translateDAO.deleteAll()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        translateRepositoryImpl.deleteAllTranslates()
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onDelete(false)
                }
            })
    }

    @Throws(NullPointerException::class)
    override fun deleteTranslate(item: TranslateItem?): Disposable {
        if(item == null) throw NullPointerException("translate item is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.translateDAO.delete(item)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        translateRepositoryImpl.deleteTranslate(item.id)
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onDelete(false)
                }
            })
    }

    @Throws(NullPointerException::class)
    override fun insertAllLanguages(list: List<ScreenSpinnerTranslateItem>?): Disposable {
        if(list == null) throw NullPointerException("languages list is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.translateDAO.insertAll(list)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onInsert(true)
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }

    @Throws(NullPointerException::class)
    override fun insertAllTranslates(list: List<TranslateItem>?): Disposable {
        if(list == null) throw NullPointerException("list of translate items is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.translateDAO.insertAllTranslates(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onInsert(true)
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }

    override fun getTranslates(listener: CallbackListener<List<TranslateItem>?>): Disposable {
        val dbConnection = DBConnection.getDbConnection()
        val single = dbConnection!!.translateDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
        return single!!.subscribeWith<DisposableSingleObserver<List<TranslateItem>?>>(object : DisposableSingleObserver<List<TranslateItem>?>() {
                override fun onSuccess(list: List<TranslateItem>) {
                    listener.onItem(ItemStates.ItemState(list))
                }
                override fun onError(e: Throwable) {
                    listener.onFailure(ThrowableStates.ErrorStates("Impossible to get data",e))
                    Logger.log(Log.ERROR, "local translate db exception", e)
                }
            })
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
        val single = dbConnection!!.translateDAO.getById(id)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
        return single!!.subscribeWith<DisposableSingleObserver<TranslateItem?>>(object : DisposableSingleObserver<TranslateItem?>() {
                override fun onSuccess(translateItem: TranslateItem) {
                    if (FirebaseProvider.userIsLogIn()){
                        translateRepositoryImpl.addTranslate(translateItem)
                    }
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local translate db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }
}