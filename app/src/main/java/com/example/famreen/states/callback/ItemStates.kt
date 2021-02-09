package com.example.famreen.states.callback

sealed class ItemStates: CallbackStates(){
    class ItemState<T>(val item: T): ItemStates()
    class ListState<T>(val item: List<T>): ItemStates()
}