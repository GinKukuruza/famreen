package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.selection.Selection
import com.example.famreen.states.States
import com.example.famreen.application.comparators.TranslateComparator
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.DBConnection
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.states.RoomStates
import com.example.famreen.utils.Utils
import com.example.famreen.utils.default
import com.example.famreen.utils.set
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.lang.NullPointerException

class TranslateViewModel(private val diaryRoomRepository: DiaryRoomRepository,private val translateRoomRepository: TranslateRoomRepository) {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    init{
        initObserver()
        translateRoomRepository.subscribe(observer = observer)
    }

    private lateinit var observer: Observer<RoomStates>

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
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        }
    }

    fun getTranslates(){
        DBConnection.getDbConnection()!!.translateDAO.all!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<TranslateItem>?>() {
                override fun onError(e: Throwable) {

                }
                override fun onSuccess(list: List<TranslateItem>) {
                    state.set(States.SuccessState(filter(list)))
                }
            })
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
    fun addPickedTranslates(selection: Selection<TranslateItem>){
        val description = prepareSelectedForTranslateDescription(selection)
        val item = NoteItem()
        item.title = "Translate"
        item.important = true
        item.time = Utils.getNoteTime()
        item.tag = "translate"
        item.description = description
        diaryRoomRepository.insertNote(item)
    }
    private fun prepareSelectedForTranslateDescription(selection: Selection<TranslateItem>): String{
        val sbDescription = StringBuilder()
        sbDescription.append("\n")
        for (item in selection) {
            sbDescription
                .append(item.from_lang)
                .append(" : ")
                .append(item.from_translate)
                .append("\n").append(item.to_lang)
                .append(" : ")
                .append(item.to_translate)
                .append("\n\n")
        }
        return sbDescription.toString()
    }
    fun deleteAllTranslates(){
        translateRoomRepository.deleteAllTranslates()
    }
    fun clear(){
        translateRoomRepository.unsubscribe(observer = observer)
    }
}