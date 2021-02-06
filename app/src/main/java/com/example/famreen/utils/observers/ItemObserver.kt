package com.example.famreen.utils.observers

interface ItemObserver<T> {
    fun getItem(item: T)
    fun onFailure(msg: String)
}