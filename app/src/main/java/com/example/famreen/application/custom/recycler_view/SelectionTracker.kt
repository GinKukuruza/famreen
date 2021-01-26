@file:Suppress("PrivatePropertyName", "PrivatePropertyName")

package com.example.famreen.application.custom.recycler_view

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.famreen.application.logging.Logger
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SelectionTracker<T>(@NonNull private val recyclerView: RecyclerView, @NonNull private val detailsLookup: DetailsLookup<T>) {
    private val mTag = SelectionTracker::class.java.name

    private val DEF_DELAY = 20L
    private val LONG_DELAY = 250L

    private val mTable: HashMap<Int,T> = HashMap()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mItemDetails: DetailsLookup.ItemDetails<T>? = null
    private var mObserver: SelectionObserver? = null
    private var mHandler: DisposableCompletableObserver? = null

    private var mCounter = 0
    init {
        init()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        if(recyclerView.adapter != null){
            mAdapter = recyclerView.adapter
        }
        Logger.d(mTag,"INIT SELECTION TRACKER", "rv")
        recyclerView.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    Logger.d(mTag,"Touch: x - " + event.x + ", y - " + event.y, "rv")
                    mItemDetails = detailsLookup.getItemDetails(event)
                    if(mItemDetails == null) clear()
                    mItemDetails?.let {
                        if(mTable.size > 0){
                            startOnDelay(DEF_DELAY)
                        }else startOnDelay(LONG_DELAY)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    mHandler?.dispose()
                }
                MotionEvent.ACTION_UP -> {
                    mHandler?.dispose()
                }
            }
            false
        }
    }

    private fun deselect(key: Int): Boolean{
        if(!isSelected(key)) return true
        mAdapter?.let {
            mTable.remove(key)
            mObserver?.onCounterChanged(getCounter())
            mAdapter?.notifyItemChanged(key)
            //observer?.onItemStateChanged(key,false)
            return true
        }
        return false
    }

    private fun select(key: Int, value: T): Boolean{
        if(isSelected(key)) return true
        mAdapter?.let {
            mTable[key] = value
            mObserver?.onCounterChanged(getCounter())
            mAdapter?.notifyItemChanged(key)
            //observer?.onItemStateChanged(key,true)
            return true
        }
        return false
    }

    private fun getCounter(): Int{
        mCounter = mTable.size
        return mCounter
    }
    private fun startOnDelay(delay: Long){
        mHandler = Completable.timer(delay,TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    //Logger.d(tag,"OnLongPress", "rv")
                    press()
                }
                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "selection tracker delay exception", e)
                }
            })
    }
    private fun press(){
        mItemDetails?.let { details ->
            if(isSelected(details.getKey())){
                deselect(details.getKey())
            }else{
                details.getValue()?.let {
                    select(details.getKey(),it)
                }
            }
        }
    }
    /**
     * Устанавливает интерфейс observer для обратной связи
     * **/
    fun addObserver(observer: SelectionObserver){
        mObserver = observer
    }
    /**
     * Проверяет по ключу, является ли элемент выделенным
     * **/
    fun isSelected(key: Int) : Boolean{
        return mTable.containsKey(key)
    }
    /**
     *Полностью очищает все выделенные элементы и вызывает метод onFullyCleared() у SelectionObserver
     * **/
    fun clear(){
        mTable.clear()
        mObserver?.onFullyCleared(true)
    }
}