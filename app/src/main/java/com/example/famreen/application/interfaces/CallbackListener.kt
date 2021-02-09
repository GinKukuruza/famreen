package com.example.famreen.application.interfaces

import com.example.famreen.application.items.NoteItem
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates

interface CallbackListener<T> {
    fun onItem(s: ItemStates.ItemState<T>)
    fun onList(s: ItemStates.ListState<NoteItem>){}
    fun onFailure(state: ThrowableStates)
}