package com.example.famreen.application.custom.recycler_view

import android.view.MotionEvent

abstract class DetailsLookup<T> {
    abstract fun getItemDetails(e: MotionEvent):  ItemDetails<T>?
    abstract class ItemDetails<T>{
        abstract fun getKey(): Int
        abstract fun getValue(): T
    }
}