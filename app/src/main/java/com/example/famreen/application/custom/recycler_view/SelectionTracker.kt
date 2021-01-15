package com.example.famreen.application.custom.recycler_view

import android.annotation.SuppressLint
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
    private val DEF_DELAY = 20L
    private val LONG_DELAY = 250L
    private val tag = SelectionTracker::class.java.name
    private val table: HashMap<Int,T> = HashMap()
    private var adapter: RecyclerView.Adapter<*>? = null
    private var itemDetails: DetailsLookup.ItemDetails<T>? = null
    private var observer: SelectionObserver? = null
    private var handler: DisposableCompletableObserver? = null
    private var counter = 0
    init {
        init()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        if(recyclerView.adapter != null){
            adapter = recyclerView.adapter
        }
        Logger.d(tag,"INIT SELECTION TRACKER", "rv")
        recyclerView.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    Logger.d(tag,"Touch: x - " + event.x + ", y - " + event.y, "rv")
                    itemDetails = detailsLookup.getItemDetails(event)
                    if(itemDetails == null) clear()
                    itemDetails?.let {
                        if(table.size > 0){
                            startOnDelay(DEF_DELAY)
                        }else startOnDelay(LONG_DELAY)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    handler?.dispose()
                }
                MotionEvent.ACTION_UP -> {
                    handler?.dispose()
                }
            }
            false
        }
    }

    fun isSelected(key: Int) : Boolean{
        return table.containsKey(key)
    }
    fun clear(){
        table.clear()
        observer?.onFullyCleared(true)
    }

    private fun deselect(key: Int): Boolean{
        if(!isSelected(key)) return true
        adapter?.let {
            table.remove(key)
            observer?.onCounterChanged(getCounter())
            adapter?.notifyItemChanged(key)
            //observer?.onItemStateChanged(key,false)
            return true
        }
        return false
    }

    private fun select(key: Int, value: T): Boolean{
        if(isSelected(key)) return true
        adapter?.let {
            table[key] = value
            observer?.onCounterChanged(getCounter())
            adapter?.notifyItemChanged(key)
            //observer?.onItemStateChanged(key,true)
            return true
        }
        return false
    }
    fun addObserver(observer: SelectionObserver){
        this.observer = observer
    }
    private fun getCounter(): Int{
        counter = table.size
        return counter
    }
    private fun startOnDelay(delay: Long){
        handler = Completable.timer(delay,TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    //Logger.d(tag,"OnLongPress", "rv")
                    press()
                }
                override fun onError(e: Throwable) {}
            })
    }
    private fun press(){
        itemDetails?.let { details ->
            if(isSelected(details.getKey())){
                deselect(details.getKey())
            }else{
                details.getValue()?.let {
                    select(details.getKey(),it)
                }
            }
        }
    }

}