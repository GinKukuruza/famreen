package com.example.famreen.application.viewmodels

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.famreen.R
import com.example.famreen.application.comparators.DiaryComparator
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.NoteSortItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.states.RoomStates
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver

class DiaryViewModel(private val mDiaryRoomRepositoryImpl: DiaryRoomRepository) {
    private val mTag = DiaryViewModel::class.java.name
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private lateinit var mObserver: Observer<RoomStates>

    init{
        initObserver()
        mDiaryRoomRepositoryImpl.subscribe(observer = mObserver)
    }

    private fun initObserver(){
        mObserver = object : DisposableObserver<RoomStates>(), Observer<RoomStates> {
            override fun onNext(it: RoomStates) {
                when(it){
                    is RoomStates.DeleteState ->{ }
                    is RoomStates.InsertState ->{
                        if(it.isSuccess) getNotes()
                    }
                }
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "diary room observer exception", e)
            }
            override fun onComplete() {}
        }
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
    /**
     * **/
    fun deleteAllNotes(list: List<Int>?){
        list?.let {
            mDiaryRoomRepositoryImpl.deleteAllNotes(list)
        }
    }
    /**
     * **/
    fun deleteAllNotes(){
        mDiaryRoomRepositoryImpl.deleteAllNotes()
    }
    /**
     * Возвращает лист с жлементами выборки сортировки
     * **/
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
    /**
     * Возвращает список записей, пропущенных через фильтр
     * **/
    fun getNotes(){
        mState.set(States.LoadingState())
        mDiaryRoomRepositoryImpl.getNotes(object : ItemObserver<List<NoteItem>?>{
            override fun getItem(item: List<NoteItem>?) {
                mState.set(States.SuccessState(filter(item as List<NoteItem>)))
            }

            override fun onFailure(msg: String) {
                mState.set(States.ErrorState(msg))
            }

        })
    }
    /**
     * Очищает ресурсы: observer
     * **/
    fun clear(){
        mDiaryRoomRepositoryImpl.unsubscribe(observer = mObserver)
    }
    /**
     * **/
    fun getState() = mState
}