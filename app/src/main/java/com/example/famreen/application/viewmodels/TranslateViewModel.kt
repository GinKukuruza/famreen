package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.selection.Selection
import com.example.famreen.states.States
import com.example.famreen.application.comparators.TranslateComparator
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.DBConnection
import com.example.famreen.application.room.repositories.DiaryRoomRepositoryImpl
import com.example.famreen.application.room.repositories.TranslateRoomRepositoryImpl
import com.example.famreen.states.RoomStates
import com.example.famreen.utils.Utils
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.lang.NullPointerException

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
                Logger.log(3, "translate room observer exception", e)
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

    private fun prepareSelectedForTranslateDescription(selection: Selection<TranslateItem>): String{
        val sbDescription = StringBuilder()
        sbDescription.append("\n")
        for (item in selection) {
            sbDescription
                .append(item.mFrom_lang)
                .append(" : ")
                .append(item.mFrom_translate)
                .append("\n").append(item.mTo_lang)
                .append(" : ")
                .append(item.mTo_translate)
                .append("\n\n")
        }
        return sbDescription.toString()
    }
    /**
     * Вызывается для получения всех отфильтрованных переводов(основной метод)
     * **/
    fun getTranslates(){
        mState.set(States.LoadingState())
        DBConnection.getDbConnection()!!.translateDAO.all!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<TranslateItem>?>() {
                override fun onError(e: Throwable) {
                    Logger.log(9, "local translate db exception", e)
                }
                override fun onSuccess(list: List<TranslateItem>) {
                    mState.set(States.SuccessState(filter(list)))
                }
            })
    }
    /**
     * STATUS: IN PROGRESS
     * Должен вызываться для добавления выделенных элементов
     * в специальной форме в записи(Diary)
     * **/
    fun addPickedTranslates(selection: Selection<TranslateItem>){
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