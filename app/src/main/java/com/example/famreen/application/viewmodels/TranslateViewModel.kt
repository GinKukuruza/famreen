package com.example.famreen.application.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.comparators.TranslateComparator
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.states.RoomStates
import com.example.famreen.states.States
import com.example.famreen.utils.Utils
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver

class TranslateViewModel(private val mDiaryRoomRepositoryImpl: DiaryRoomRepository,
                         private val mTranslateRoomRepositoryImpl: TranslateRoomRepository) {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private lateinit var observer: Observer<RoomStates>

    init{
        initObserver()
        mTranslateRoomRepositoryImpl.subscribe(observer = observer)
    }


    private fun initObserver(){
        observer = object : DisposableObserver<RoomStates>(), Observer<RoomStates> {
            override fun onNext(it: RoomStates) {
                when(it){
                    is RoomStates.DeleteState ->{}
                    is RoomStates.InsertState ->{
                        if(it.isSuccess) getTranslates()
                    }
                }
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "translate room observer exception", e)
            }
            override fun onComplete() {}
        }
    }

    private fun filter(items: List<TranslateItem>?): List<TranslateItem> {
        if(items == null) throw NullPointerException("List is null")
        var list = items
        val comparator = TranslateComparator()
        list = comparator.sortByLangFrom(
            list, AppPreferences.getProvider()!!.readTranslateSortFromLang()
        )
        list = comparator.sortByLangTo(
            list, AppPreferences.getProvider()!!.reaTranslateSortToLang()
        )
        list = comparator.sortByDescription(
            list, AppPreferences.getProvider()!!.readTranslateSortDescription()
        )
        return list
    }

    private fun prepareSelectedForTranslateDescription(selection: List<TranslateItem>): String{
        val sbDescription = StringBuilder()
        sbDescription.append("\n")
        for (item in selection) {
            sbDescription
                .append(item.mFromLang)
                .append(" : ")
                .append(item.mFromTranslate)
                .append("\n").append(item.mToLang)
                .append(" : ")
                .append(item.mToTranslate)
                .append("\n\n")
        }
        return sbDescription.toString()
    }
    /**
     * Вызывается для получения всех отфильтрованных переводов(основной метод)
     * **/
    fun getTranslates(){
        mState.set(States.LoadingState())
        mTranslateRoomRepositoryImpl.getTranslates(object : ItemObserver<List<TranslateItem>?>{
            override fun getItem(item: List<TranslateItem>?) {
                mState.set(States.SuccessState(filter(item)))
            }

            override fun onFailure(msg: String) {
                mState.set(States.ErrorState(msg))
            }
        })
    }
    /**
     * STATUS: IN PROGRESS
     * Должен вызываться для добавления выделенных элементов
     * в специальной форме в записи(Diary)
     * **/
    fun addPickedTranslates(selection: List<TranslateItem>){
        val description = prepareSelectedForTranslateDescription(selection)
        val item = NoteItem()
        item.mTitle = "Translate"
        item.mImportant = true
        item.mTime = Utils.getNoteTime()
        item.mTag = "translate"
        item.mDescription = description
        mDiaryRoomRepositoryImpl.insertNote(item)
    }
    /**
     * **/
    fun deleteAllTranslates(){
        mTranslateRoomRepositoryImpl.deleteAllTranslates()
    }
    /**
     * Очищает ресурсы: observer
     * **/
    fun clear(){
        mTranslateRoomRepositoryImpl.unsubscribe(observer = observer)
    }
    /**
     * **/
    fun getState() = mState
}