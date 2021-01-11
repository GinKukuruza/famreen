package com.example.famreen.application.network

import android.util.Log
import com.example.famreen.states.RoomStates
import io.reactivex.Observer

class DiaryObserver {
    companion object{
        private val list: MutableList<Observer<RoomStates>> = ArrayList()
    }
    fun subscribe(observer: Observer<RoomStates>){
        Log.d("TEST","sub")
        list.add(observer)
        Log.d("TEST","list - " + list.size)
    }
    fun unsubscribe(observer: Observer<RoomStates>){
        Log.d("TEST","unsub")
        list.forEach{
            if(observer == it) list.remove(it)
        }
        Log.d("TEST","unsub list - " + list.size)
    }
    fun onInsert(isSuccess: Boolean){
        Log.d("TEST","insert")
        Log.d("TEST","insert list size - "+ list.size)
        list.forEach{
            it.onNext(RoomStates.InsertState(isSuccess))
        }
    }
    fun onDelete(isDeleted: Boolean){
        Log.d("TEST","delete")
        Log.d("TEST","delete list size - "+ list.size)
        list.forEach{
            it.onNext(RoomStates.DeleteState(isDeleted))
        }
    }
}