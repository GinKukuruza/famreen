package com.example.famreen.application.custom.recycler_view

interface SelectionObserver{
    fun onItemStateChanged(key: Int,isSelected: Boolean)
    fun onCounterChanged(counter: Int)
    fun onFullyCleared(isCleared: Boolean)
    fun onSelectionChanged()
}