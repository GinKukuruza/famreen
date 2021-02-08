package com.example.famreen.application.interfaces

interface ItemListener<T> {
    fun getItem(item: T)
    fun onFailure(msg: String)
}