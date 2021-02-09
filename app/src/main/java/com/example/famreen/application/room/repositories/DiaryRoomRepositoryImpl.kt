package com.example.famreen.application.room.repositories

import android.util.Log
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.interfaces.DiaryRepository
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.SubjectRoom
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.network.DiarySubject
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

class DiaryRoomRepositoryImpl(val diaryRepositoryImpl: DiaryRepository) : DiaryRoomRepository {
    private val mTag = DiaryRoomRepositoryImpl::class.java.name
    private val mRoomSubject: SubjectRoom = DiarySubject()

    @Throws(NullPointerException::class)
    override fun insertNote(item: NoteItem?): Disposable {
        if(item == null) throw java.lang.NullPointerException("list of notes is null")
        val dbConnection = DBConnection.getDbConnection()
        val single = dbConnection!!.diaryDAO.insert(item)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())

        return single!!.subscribeWith<DisposableSingleObserver<Long?>>(object : DisposableSingleObserver<Long?>() {
                override fun onSuccess(aLong: Long) {
                    insertNoteById(aLong)
                    mRoomSubject.onInsert(true)
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }

    @Throws(NullPointerException::class)
    override fun deleteNote(item: NoteItem?): Disposable {
        if(item == null) throw java.lang.NullPointerException("note item is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.diaryDAO.delete(item)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteNote(item.id)
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onDelete(false)
                }
            })
    }
    override fun deleteAllNotes(): Disposable {
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.diaryDAO.deleteAll()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onDelete(true)
                    if(FirebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteAllNotes()
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onDelete(false)
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun deleteAllNotes(list: List<Int>?): Disposable {
        if(list == null) throw java.lang.NullPointerException("list of notes is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.diaryDAO.deleteAll(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onDelete(true)
                    if (FirebaseProvider.userIsLogIn())
                        diaryRepositoryImpl.deleteNotes(list)
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onDelete(false)
                }
            })
    }
    @Throws(NullPointerException::class)
    override fun insertAllNotes(list: List<NoteItem>?): Disposable {
        if(list == null) throw java.lang.NullPointerException("list of notes is null")
        return Completable.fromAction {
            val dbConnection = DBConnection.getDbConnection()
            dbConnection!!.diaryDAO.insertAll(list)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith<DisposableCompletableObserver>(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    mRoomSubject.onInsert(true)
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }

    override fun getNotes(listener: CallbackListener<List<NoteItem>>): Disposable {
        val dbConnection = DBConnection.getDbConnection()
        val single = dbConnection!!.diaryDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
        return single!!.subscribeWith<DisposableSingleObserver<List<NoteItem>?>>(object : DisposableSingleObserver<List<NoteItem>?>() {
                override fun onSuccess(list: List<NoteItem>) {
                    Logger.i(mTag,"on GetNotes() success","test")
                    listener.onList(ItemStates.ListState(list))
                }
                override fun onError(e: Throwable) {
                    Logger.e(mTag,"on GetNotes() fail","test")
                    listener.onFailure(ThrowableStates.ErrorStates("Impossible to get data",e))
                    Logger.log(Log.ERROR, "local diary db exception", e)
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
    private fun insertNoteById(id: Long?): Disposable {
        if(id == null) throw java.lang.NullPointerException("id of the note is null")
        val dbConnection = DBConnection.getDbConnection()
        val single = dbConnection!!.diaryDAO.getById(id)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
        return single!!.subscribeWith<DisposableSingleObserver<NoteItem?>>(object : DisposableSingleObserver<NoteItem?>() {
                override fun onSuccess(noteItem: NoteItem) {
                    mRoomSubject.onInsert(true)
                    if (FirebaseProvider.userIsLogIn()){
                        diaryRepositoryImpl.addNote(noteItem)
                    }
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "local diary db exception", e)
                    mRoomSubject.onInsert(false)
                }
            })
    }
}