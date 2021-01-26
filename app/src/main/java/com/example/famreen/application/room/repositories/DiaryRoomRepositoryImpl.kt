package com.example.famreen.application.room.repositories

import com.example.famreen.application.App
import com.example.famreen.application.interfaces.DiaryRepository
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.network.DiaryObserver
import com.example.famreen.application.room.DBConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.repositories.DiaryRepositoryImpl
import com.example.famreen.states.RoomStates
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DiaryRoomRepositoryImpl(val diaryRepositoryImpl: DiaryRepository) : DiaryRoomRepository {
    @Inject
    lateinit var firebaseProvider: FirebaseProvider
    private val mRoomObserver: DiaryObserver = DiaryObserver()
    init {
        App.appComponent.inject(this@DiaryRoomRepositoryImpl)
    }
    @Throws(NullPointerException::class)
    override fun insertNote(item: NoteItem?) {
        if(item == null) throw java.lang.NullPointerException("list of notes is null")
        val disposables = CompositeDisposable()
        val dbConnection = DBConnection.getDbConnection()
        disposables.add(dbConnection?.diaryDAO?.insert(item)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
        !!.subscribeWith(object : DisposableSingleObserver<Long?>() {
                override fun onSuccess(aLong: Long) {
                    disposables.add(
                        dbConnection.diaryDAO.getById(aLong)
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribeOn(Schedulers.io())
                        !!.subscribeWith(object : DisposableSingleObserver<NoteItem?>() {
                                override fun onSuccess(noteItem: NoteItem) {
                                    mRoomObserver.onInsert(true)
                                    if (firebaseProvider.userIsLogIn())
                                        diaryRepositoryImpl.addNote(noteItem)
                                    disposables.clear()
                                    disposables.dispose()
                                }
                                override fun onError(e: Throwable) {
                                    Logger.log(9, "local diary db exception", e)
                                    mRoomObserver.onInsert(false)
                                    disposables.clear()
                                    disposables.dispose()
                                }
                            }))
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "local diary db exception", e)
                    mRoomObserver.onInsert(false)
                    disposables.clear()
                    disposables.dispose()
                }
            }))
    }
    @Throws(NullPointerException::class)
    override fun deleteNote(item: NoteItem?) {
        if(item == null) throw java.lang.NullPointerException("note item is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.diaryDAO?.delete(item)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    mRoomObserver.onDelete(true)
                    if (firebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteNote(item.id)
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "local diary db exception", e)
                    mRoomObserver.onDelete(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    override fun deleteAllNotes() {
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.diaryDAO?.deleteAll()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }
                override fun onComplete() {
                    mRoomObserver.onDelete(true)
                    if(firebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteAllNotes()
                    disposables.clear()
                    disposables.dispose()
                }
                override fun onError(e: Throwable) {
                    Logger.log(9, "local diary db exception", e)
                    mRoomObserver.onDelete(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun deleteAllNotes(list: List<Int>?) {
        if(list == null) throw java.lang.NullPointerException("list of notes is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.diaryDAO?.deleteAll(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onComplete() {
                    mRoomObserver.onDelete(true)
                    if (firebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteNotes(list)
                    disposables.clear()
                    disposables.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "local diary db exception", e)
                    mRoomObserver.onDelete(false)
                    disposables.clear()
                    disposables.dispose()
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun insertAllNotes(list: List<NoteItem>?) {
        if(list == null) throw java.lang.NullPointerException("list of notes is null")
        val disposables = CompositeDisposable()
        Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection?.diaryDAO?.insertAll(list)
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
                    Logger.log(9, "local diary db exception", e)
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