package com.example.famreen.application.network

import com.example.famreen.states.RoomStates
import io.reactivex.Observer

class TranslateObserver {
    companion object{
        private val list: MutableList<Observer<RoomStates>> = ArrayList()
    }
    fun subscribe(observer: Observer<RoomStates>){
        list.add(observer)
    }
    fun unsubscribe(observer: Observer<RoomStates>){
        list.forEach{
            if(observer == it) list.remove(it)
        }
    }
    fun onInsert(isSuccess: Boolean){
        list.forEach{
            it.onNext(RoomStates.InsertState(isSuccess))
        }
    }
    fun onDelete(isDeleted: Boolean){
        list.forEach{
            it.onNext(RoomStates.DeleteState(isDeleted))
        }
    }
}