package com.example.famreen.application.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData

import com.example.famreen.R
import com.example.famreen.states.States
import com.example.famreen.application.comparators.DiaryComparator
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.NoteSortItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.DBConnection
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.states.RoomStates
import com.example.famreen.utils.default
import com.example.famreen.utils.set
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DiaryViewModel(private val diaryRoomRepository: DiaryRoomRepository) {
    init{
        initObserver()
        diaryRoomRepository.subscribe(observer = observer)
    }
    private val tag = DiaryViewModel::class.java.name
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private lateinit var observer: Observer<RoomStates>

    private fun initObserver(){
        observer = object : DisposableObserver<RoomStates>(), Observer<RoomStates> {
            override fun onNext(it: RoomStates) {
                when(it){
                    is RoomStates.DeleteState ->{ }
                    is RoomStates.InsertState ->{
                        if(it.isSuccess) getNotes()
                    }
                }
            }
            override fun onError(e: Throwable) {
                Logger.log(3, "diary room observer exception", e)
            }
            override fun onComplete() {}
        }
    }
    fun deleteAllNotes(list: List<Int>){
        diaryRoomRepository.deleteAllNotes(list)
    }
    fun deleteAllNotes(){
        diaryRoomRepository.deleteAllNotes()
    }
    fun getSortAdapterItems(): List<NoteSortItem> {
        val items: MutableList<NoteSortItem> = ArrayList()
        items.add(NoteSortItem("Data", R.drawable.img_arrow_up, 0))
        items.add(NoteSortItem("Data", R.drawable.img_arrow_down, 1))
        items.add(NoteSortItem("Title", R.drawable.img_arrow_up, 2))
        items.add(NoteSortItem("Title", R.drawable.img_arrow_down, 3))
        items.add(NoteSortItem("Tag", R.drawable.img_arrow_up, 4))
        items.add(NoteSortItem("Tag", R.drawable.img_arrow_down, 5))
        items.add(NoteSortItem("Important", R.drawable.img_arrow_up, 6))
        items.add(NoteSortItem("Important", R.drawable.img_arrow_down, 7))
        return items
    }

    fun getNotes(){
        val dbConnection = DBConnection.getDbConnection()
        dbConnection!!.diaryDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableSingleObserver<List<NoteItem?>?>() {
                override fun onSuccess(items: List<NoteItem?>) {
                    @Suppress("UNCHECKED_CAST")
                    state.set(States.SuccessState(filter(items as List<NoteItem>)))
                }
                override fun onError(e: Throwable) {
                    Logger.log(9, "local diary db exception", e)
                }
            })
    }
    @Throws(NullPointerException::class)
    private fun filter(list: List<NoteItem>?): List<NoteItem> {
        if(list == null) throw NullPointerException("List is null")
        val comparator = DiaryComparator()
        var items: List<NoteItem> = list
        items = if (AppPreferences.getProvider()?.readNoteSortIsImportant() as Boolean) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                comparator.sortOnlyImportantLowerApi24(items)
            }else{
                comparator.sortOnlyImportant(items)
            }
        } else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                comparator.sortAllImportantLowerApi24(items)
            }else{
                comparator.sortAllImportant(items)
            }
        }
        if ( AppPreferences.getProvider()?.readNoteSortTitle() as String != "")
            items = comparator.sortByTitle(
                items,
                AppPreferences.getProvider()?.readNoteSortTitle() as String
            )
        if (AppPreferences.getProvider()?.readNoteSortTag() as String != "")
            items = comparator.sortByTag(
                items,
                AppPreferences.getProvider()?.readNoteSortTag() as String
            )
        when (AppPreferences.getProvider()!!.readNoteSortType()) {
            0 -> comparator.sortByDataUp(items)
            1 -> comparator.sortByDataDown(items)
            2 -> comparator.sortByTitleUp(items)
            3 -> comparator.sortByTitleDown(items)
            4 -> comparator.sortByTagUp(items)
            5 -> comparator.sortByTagDown(items)
            6 -> comparator.sortByImportantUp(items)
            7 -> comparator.sortByImportantDown(items)
        }
        return items
    }
    fun clear(){
        diaryRoomRepository.unsubscribe(observer = observer)
    }
}