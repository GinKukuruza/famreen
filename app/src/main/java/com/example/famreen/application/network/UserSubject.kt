package com.example.famreen.application.network

import com.example.famreen.application.interfaces.SubjectRoom
import com.example.famreen.states.RoomStates
import io.reactivex.Observer

class UserSubject : SubjectRoom{
    companion object{
        private val list: MutableList<Observer<RoomStates>> = ArrayList()
    }
    override fun subscribe(observer: Observer<RoomStates>){
        list.add(observer)
    }
    override fun unsubscribe(observer: Observer<RoomStates>){
        list.forEach{
            if(observer == it) list.remove(it)
        }
    }
    override fun onInsert(isSuccess: Boolean){
        list.forEach{
            it.onNext(RoomStates.InsertState(isSuccess))
        }
    }
    override fun onDelete(isDeleted: Boolean){
        list.forEach{
            it.onNext(RoomStates.DeleteState(isDeleted))
        }
    }
}