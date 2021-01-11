package com.example.famreen.application.network

import android.util.Log
import com.example.famreen.states.RoomStates
import io.reactivex.Observer

class TranslateObserver {
    companion object{
        private val list: MutableList<Observer<RoomStates>> = ArrayList()
    }
    fun subscribe(observer: Observer<RoomStates>){
        Log.d("OBS","sub")
        list.add(observer)
        Log.d("OBS","list - " + list.size)
    }
    fun unsubscribe(observer: Observer<RoomStates>){
        Log.d("OBS","unsub")
        list.forEach{
            if(observer == it) list.remove(it)
        }
        Log.d("OBS","unsub list - " + list.size)
    }
    fun onInsert(isSuccess: Boolean){
        Log.d("OBS","insert")
        Log.d("OBS","insert list size - "+ list.size)
        list.forEach{
            it.onNext(RoomStates.InsertState(isSuccess))
        }
    }
    fun onDelete(isDeleted: Boolean){
        Log.d("OBS","delete")
        Log.d("OBS","delete list size - "+ list.size)
        list.forEach{
            it.onNext(RoomStates.DeleteState(isDeleted))
        }
    }
}